# AspectPlugin
Aspect automatic integration plugin (applicable to Android Studio environment; not tested in other environments)

[![Release Version](https://img.shields.io/badge/release-2.5.1-green.svg)](https://github.com/2017398956/AspectPlugin/releases)
 
## Update Notes

#### 2.5.1 Bug fixes
#### 2.5 Adapted to Gradle 8.0
#### 2.4 Resolved compilation errors on Linux and Mac
#### 2.3 Resolved cache issues when mixing Java and Kotlin
#### 2.0 Supported mixed Java and Kotlin programming

## Usage

Add the following code to the `build` section of the root directory:

    buildscript {
        repositories {
           ...
           maven { url 'https://jitpack.io' }
        }
        dependencies {
            ...
            classpath 'com.github.2017398956:AspectPlugin:2.5.1'
        }
    }


Add the following code to the required module:

    apply plugin: 'AspectPlugin'
    dependencies { api 'org.aspectj:aspectjrt:1.9.6'}
    
Additionally, if using Kotlin, place `apply plugin: 'AspectPlugin'` after `apply plugin: 'kotlin-xxx'`.

**Note**: The file performing the AOP operation and the object being operated on must be in the same module.

Support for cross-module operations and AOP operations on jars or aars will be added when I have time.

A temporary workaround for cross-module operations is provided here. For example: if you need to perform an AOP operation on `LogXXX.java` in module A from module B, you can create an `AspForLogxxxx.java` file in module A and add a listener (or a collection of listeners) to it. Then, in module B, you can bypass the cross-module issue by instantiating this listener or adding a listener to the collection.
    
    
A simple Demo: [AspectPluginDemo](https://github.com/2017398956/AspectPluginDemo)

Application Example: [AbcPermission](https://github.com/2017398956/AbcPermission "AbcPermission")
