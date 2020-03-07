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
            String[] args = ["-showWeaveInfo",
                             "-1.5",
                             "-inpath", javaCompile.destinationDir.toString(),
                             "-aspectpath", javaCompile.classpath.asPath,
                             "-d", javaCompile.destinationDir.toString(),
                             "-classpath", javaCompile.classpath.asPath,
                             "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
            MessageHandler handler = new MessageHandler(true)
            new Main().run(args, handler)

            def log = project.logger
            for (IMessage message : handler.getMessages(null, true)) {
                switch (message.getKind()) {
                    case IMessage.ABORT:
                    case IMessage.ERROR:
                    case IMessage.FAIL:
                        log.error message.message, message.thrown
                        break
                    case IMessage.WARNING:
                    case IMessage.INFO:
                        log.info message.message, message.thrown
                        break
                    case IMessage.DEBUG:
                        log.debug message.message, message.thrown
                        break
                }
            }
        }
    }
}
