# SBT

## Hello World

```shell
$ mkdir test
$ cd test
$ vi HelloWorld.scala
```

```scala
// HelloWorld.scala
object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, SBT")
  }
}
```

```shell
$ sbt
[info] Set current project to test (in build file:/home/hadoop/test/)
> run
[info] Updating {file:/home/hadoop/test/}test...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /home/hadoop/test/target/scala-2.10/classes...
[info] Running HelloWorld
Hello, SBT
[success] Total time: 5 s, completed Feb 2, 2016 1:19:22 AM
>
```

## build.sbt
等同Maven project pom.xml，define how to build
```
name := "hello"

orgnization := "xxx.xxx.xxx"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"
```

## ```project/```
- build.properties - 声明使用的要使用哪个版本的SBT来编译当前项目
- plugins.sbt - 用来声明当前项目希望使用哪些插件来增强当前项目使用的sbt的功能

如果我们使用git来做版本控制，那么就可以在.gitignore中添加一行"target/"来排除项目根目录下和project目录下的target目录及其相关文件。

## SBT的使用

1. 批处理模式(batch mode)
```
$ sbt compile test package
```

2. 可交互模式(interactive mode)
```
$ sbt
> compile
[success] Total time: 1 s, completed Sep 3, 2012 9:34:58 PM
> test
[info] No tests to run for test:test
[success] Total time: 0 s, completed Sep 3, 2012 9:35:04 PM
> package
[info] Packaging XXX_XXX_2.9.2-0.1-SNAPSHOT.jar ...
[info] Done packaging.
[success] Total time: 0 s, completed Sep 3, 2012 9:35:08 PM
```

sbt命令
- compile
- test-compile
- run
- test
- package
这些命令在某些情况下也可以结合SBT的触发执行(Trigger Execution)机制一起使用， 唯一需要做的就只是在相应的命令前追加~符号

```
$ sbt ~compile
```
以上命令意味着， 我更改了任何源代码并且保存之后，将直接触发SBT编译相应的源代码以及相应的依赖变更。 

## SBT的依赖管理

在SBT中， 类库的依赖管理可以分为两类：
- unmanaged dependencies
- managed dependencies

### Unmanaged Dependencies
```
unmanagedBase <<= baseDirectory { base => base / "3rdlibs" }
```
- unmanagedBase这个Key用来表示unmanaged dependencies存放第三方jar包的路径， 具体的值默认是lib
- 为了改变这个Key的值， 采用<<=操作符， 根据baseDirectory的值转换并计算出一个新值赋值给unmanagedBase这个Key
- baseDirectory指的是当前项目目录，而<<=操作符(其实是Key的方法)则负责从已知的某些Key的值计算出新的值并赋值给指定的Key

### Managed Dependancies
sbt的managed dependencies采用Apache Ivy的依赖管理方式， 可以支持从Maven或者Ivy的Repository中自动下载相应的依赖。

简单来说，在SBT中， 使用managed dependencies基本上就意味着往libraryDependencies这个Key中添加所需要的依赖， 添加的一般格式如下:
```
libraryDependencies += groupID % artifactID % revision
```

範例：
```
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```
- 限定依赖的范围只限于测试期间

```
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" exclude("org", "artifact")
```
- 允许排除递归依赖中某些我们需要排除的依赖

```
libraryDependencies += "org.apache.derby" %% "derby" % "10.4.1.3" 
```
- 在依赖查找的时候，将当前项目使用的scala版本号追加到artifactId之后作为完整的artifactId来查找依赖
- 比如如果我们的项目使用```scala2.9.2```，那么依赖声明实际上等同于"org.apache.derby" %% "derby_2.9.2" % "10.4.1.3"


如果有一堆依赖要添加，一行一行的添加是一种方式，其实也可以一次添加多个依赖：
```
libraryDependencies ++= Seq("org.apache.derby" %% "derby" % "10.4.1.3",
                            "org.scala-tools" %% "scala-stm" % "0.3", 
                            ...)
```
