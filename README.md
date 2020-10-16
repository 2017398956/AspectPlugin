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
    
    
一个简单的 Demo ： [AspectPluginDemo](https://github.com/2017398956/AspectPluginDemo)

应用示例：[AbcPermission](https://github.com/2017398956/AbcPermission "AbcPermission") 
