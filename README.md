# JavaCompile
### 简介
这是一款运行在Android 平台的Java编译器，它可以在你的设备上运行编译简单的Java程序，并且采用了树状结构目录，层次清晰明了。

### 内部编译相关的内容已经封装为库，开箱即用。 
### [JavaCompileEngine](https://github.com/xiaoyvyv/JavaCompileEngine)

#### 编译相关
* 内置的 Java 字节码编译器使用的是 Eclipse 编译器 [Eclipse Compiler for Java (ECJ)](https://mvnrepository.com/artifact/org.eclipse.jdt.core.compiler/ecj)。
* class 转 dex 使用的 Android SDK 中的 [d8.jar](https://github.com/xiaoyvyv/JavaCompileEngine)
#### 代码格式化
* Java 代码格式化引用的 [谷歌官方的代码格式化](https://github.com/google/google-java-format)
