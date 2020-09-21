package personal.nfl.aspect.plugin

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by 2017398956 on 2017/12/25.
 */
class AspectPlugin implements Plugin<Project> {

    String sourceJDK = "1.8"
    String targetJDK = "1.8"
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

        if (project.hasProperty('android') && project.android != null) {
            if (project.android.hasProperty('applicationVariants')
                    && project.android.applicationVariants != null) {
                project.android.applicationVariants.all { variant ->
                    // WARNING: API 'variant.getJavaCompiler()' is obsolete and
                    // has been replaced with 'variant.getJavaCompileProvider()'.
                    // doLast(variant.getJavaCompiler())
                    doLast(variant.getJavaCompileProvider().get())
                }
            }
            if (project.android.hasProperty('libraryVariants')
                    && project.android.libraryVariants != null) {
                project.android.libraryVariants.all { variant ->
                    doLast(variant.getJavaCompileProvider().get())
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
                                  "-bootclasspath", android.bootClasspath.join(File.pathSeparator)]
            new Main().run(javacArgs, handler)

            File[] kotlinClassFiles = FileUtils.listFiles(new File(kotlinInPath), null, true)
            File javacKotlinFile
            for (File temp : kotlinClassFiles) {
                if (temp.isFile() && temp.getName().endsWith(".class")) {
                    javacKotlinFile = new File(inPath + File.separator + temp.absolutePath.replace(kotlinInPath, ""))
                    if (null != javacKotlinFile && javacKotlinFile.exists()) {
                        FileUtils.copyFile(javacKotlinFile, temp)
                        FileUtils.deleteQuietly(javacKotlinFile)
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
