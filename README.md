# AspectPlugin
aspect 自动集成插件（适用于 Android Studio 环境下，其它环境下未测试）

[![Release Version](https://img.shields.io/badge/release-2.4-green.svg)](https://github.com/2017398956/AspectPlugin/releases)
 
## 更新说明

#### 2.4 解决 Linux 和 mac 下编译出错的问题
#### 2.3 解决 java 和 kotlin 混编时的缓存问题
#### 2.0 支持 java 和 kotlin 混编

## 使用方法

在根目录的 build 中加入如下代码

    buildscript {
        repositories {
           ...
           maven { url 'https://jitpack.io' }
        }
        dependencies {
            ...
            classpath 'com.github.2017398956:AspectPlugin:2.4'
        }
    }


在需要的 module 中添加如下代码


    apply plugin: 'AspectPlugin'
    dependencies { api 'org.aspectj:aspectjrt:1.9.6'}
    
另外，如果使用了 kotlin 要把 apply plugin: 'AspectPlugin' 放在 apply plugin: 'kotlin-xxx' 后面

**注意** ：进行 AOP 操作的文件和 AOP 操作的对象要在一个 module 中。

关于跨 module 操作、 对 jar 或 aar 进行 aop 操作，等我有空再支持吧。

这里提供一个临时解决跨 module 的方案，例如：你需要在 B module 中对 A module 的 LogXXX.java 进行 aop 操作，那么你可以在 A module 中 写一个 AspForLogxxxx.java 的文件，并在这个 AspForLogxxxx.java 添加一个 listener （或者 一个 listener 集合）；然后在 B moudle 中 通过实例化这个 listener 或者往这个集合中添加 listener 的方式来绕过跨 module 的问题。
    
    
一个简单的 Demo ： [AspectPluginDemo](https://github.com/2017398956/AspectPluginDemo)

应用示例：[AbcPermission](https://github.com/2017398956/AbcPermission "AbcPermission") 
