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
