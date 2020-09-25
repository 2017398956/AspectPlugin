package personal.nfl.aspect.plugin

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import personal.nfl.aspect.plugin.utils.FileUtil

/**
 * Created by 2017398956 on 2017/12/25.
 */
class AspectPlugin implements Plugin<Project> {

    private String sourceJDK = "1.8"
    private String targetJDK = "1.8"
    private File aopTemp

    void apply(Project project) {

        // def hasApp = project.plugins.withType(AppPlugin)  //判断是否是主module
        // def hasLib = project.plugins.withType(LibraryPlugin)//判断是否是library
        // final def variants
        // if (hasApp) {
        //      variants = project.android.applicationVariants
        // } else {
        //      variants = project.android.libraryVariants
        // }
        //
        // project.dependencies {
        //      compile 'org.aspectj:aspectjrt:1.8.10'
        // }
        //
        // project.extensions.create('aspect')

        aopTemp = new File(project.buildFile.parentFile.absolutePath + "\\src\\main\\java\\AopTemp.java")
        FileUtil.delete(aopTemp)

        if (project.plugins.findPlugin("kotlin-android") != null) {
            project.kotlin.target.compilations.all { variant ->
                Task kotlinCompile = variant.getCompileKotlinTaskProvider().get()
                if ("release" == variant.name || "debug" == variant.name) {
                    kotlinCompile.doLast {
                        // TODO 执行 aop 操作（待解决：当 java 代码没有变化时，javaCompile 不执行）
                        println("kolinCompile.path : " + kotlinCompile.path)
                        println("This is print after kotlin compile and should exc aop .")
                        FileWriter fileWriter = new FileWriter(aopTemp)
                        fileWriter.write("public class AopTemp {int a = " + new Random().nextInt() + ";}")
                        fileWriter.flush()
                        fileWriter.close()
                    }
                }
            }
        }

        if (project.hasProperty('android') && project.android != null) {
            if (project.android.hasProperty('applicationVariants')
                    && project.android.applicationVariants != null) {
                project.android.applicationVariants.all { variant ->
                    // WARNING: API 'variant.getJavaCompiler()' is obsolete and
                    // has been replaced with 'variant.getJavaCompileProvider()'.
                    // doLast(variant.getJavaCompiler())
                    doFirst(variant.getJavaCompileProvider().get())
                    doLast(variant.getJavaCompileProvider().get())
                }
            }
            if (project.android.hasProperty('libraryVariants')
                    && project.android.libraryVariants != null) {
                project.android.libraryVariants.all { variant ->
                    doFirst(variant.getJavaCompileProvider().get())
                    doLast(variant.getJavaCompileProvider().get())
                }
            }
        }
    }

    private void doFirst(Task javaCompile) {
        javaCompile.doFirst {
            if (project.hasProperty('android') && project.android != null) {
                if (project.android.hasProperty('compileOptions') && project.android.compileOptions != null) {
                    if (project.android.compileOptions.hasProperty('targetCompatibility') && project.android.compileOptions.targetCompatibility != null) {
                        targetJDK = project.android.compileOptions.properties.get('targetCompatibility')
                    }
                    if (project.android.compileOptions.hasProperty('sourceCompatibility') && project.android.compileOptions.sourceCompatibility != null) {
                        sourceJDK = project.android.compileOptions.properties.get('sourceCompatibility')
                    }
                }
            }
        }
    }

    private void doLast(Task javaCompile) {
        javaCompile.doLast {

            MessageHandler handler = new MessageHandler(true)
            String aspectPath = javaCompile.classpath.asPath
            String inPath = javaCompile.destinationDir.toString()
            String dPath = javaCompile.destinationDir.toString();
            String classpath = javaCompile.classpath.asPath

            if (null != aopTemp && aopTemp.exists()) {
                println("delete file:" + aopTemp.absolutePath)
                FileUtil.delete(aopTemp)
            }
            File aopTempClass = new File(dPath + File.separator + "AopTemp.class")
            if (aopTempClass.exists()) {
                println("delete file:" + aopTempClass.absolutePath)
                FileUtil.delete(aopTempClass)
            }

            // 配置 kotlin 相关参数
            String kotlinInPath = ""
            if (dPath.contains("debug\\classes")) {
                kotlinInPath = javaCompile.temporaryDir.getParentFile().path + File.separator + "kotlin-classes" + File.separator + "debug"
            } else {
                kotlinInPath = javaCompile.temporaryDir.getParentFile().path + File.separator + "kotlin-classes" + File.separator + "release"
            }
            // java 的 class 文件实现 aop
            String[] javacArgs = ["-showWeaveInfo",
                                  "-source", sourceJDK,
                                  "-target", targetJDK,
                                  "-inpath", kotlinInPath + ";" + inPath,
                                  "-aspectpath", aspectPath,
                                  "-d", dPath,
                                  "-classpath", classpath,
                                  "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
            new Main().run(javacArgs, handler)
            File[] kotlinClassFiles = FileUtil.listFiles(kotlinInPath, true)
            File javacKotlinFile
            for (File temp : kotlinClassFiles) {
                if (temp.isFile() && temp.getName().endsWith(".class")) {
                    javacKotlinFile = new File(inPath + File.separator + temp.absolutePath.replace(kotlinInPath, ""))
                    if (null != javacKotlinFile && javacKotlinFile.exists()) {
                        FileUtil.delete(temp)
                        FileUtil.copyFile(javacKotlinFile, temp)
                        FileUtil.delete(javacKotlinFile)
                    }
                }
            }

            def log = project.logger
            for (IMessage message : handler.getMessages(null, true)) {
                switch (message.getKind()) {
                    case IMessage.ABORT:
                    case IMessage.ERROR:
                    case IMessage.FAIL:
                        log.error message.message, message.thrown
                        break;
                    case IMessage.WARNING:
                    case IMessage.INFO:
                        log.info message.message, message.thrown
                        break;
                    case IMessage.DEBUG:
                        log.debug message.message, message.thrown
                        break;
                }
            }
        }
    }
}
