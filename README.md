# AspectPlugin
aspect 自动集成插件（适用于 Android Studio 环境下，其它环境下未测试）

[![Release Version](https://img.shields.io/badge/release-1.6-green.svg)](https://github.com/2017398956/AspectPlugin/releases)
 
## 更新说明

####1.6 支持 java 和 kotlin 混编

## 使用方法

在根目录的 build 中加入如下代码

    buildscript {
        repositories {
           ...
           maven { url 'https://jitpack.io' }
        }
        dependencies {
            ...
            classpath 'com.github.2017398956:AspectPlugin:1.6'
        }
    }


在需要的 module 中添加如下代码


    apply plugin: 'AspectPlugin'
    dependencies { api 'org.aspectj:aspectjrt:1.9.6'}
    
    

可以方便 [AbcPermission](https://github.com/2017398956/AbcPermission "AbcPermission") 的接入
