# AspectPlugin
aspect 自动集成插件

[![Release Version](https://img.shields.io/badge/release-1.5-green.svg)](https://github.com/2017398956/AspectPlugin/releases)
 
## 使用方法

在根目录的 build 中加入如下代码

    buildscript {
        repositories {
           ...
           maven { url 'https://jitpack.io' }
        }
        dependencies {
            ...
            classpath 'com.github.2017398956:AspectPlugin:1.5'
        }
    }


在需要的 module 中添加如下代码


    apply plugin: 'AspectPlugin'
    dependencies { api 'org.aspectj:aspectjrt:1.9.5'}
    
    

可以方便 [AbcPermission](https://github.com/2017398956/AbcPermission "AbcPermission") 的接入
