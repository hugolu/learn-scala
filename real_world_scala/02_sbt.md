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

## Resovers
默认情况下， SBT回去默认的Maven2的Repository中抓取依赖，但如果默认的Repository中找不到我们的依赖，那我们可以通过resolver机制，追加更多的repository让SBT去查找并抓取
```
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```
- ```at```之前是要追加的repository的标志名称（任意取），```at```后面则是要追加的repository的路径。

除了可远程访问的Maven Repo，我们也可以将本地的Maven Repo追加到resolver的搜索范围：
```
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

___
# SBT 進階篇

## .scala形式的build定义
对于简单的项目来讲，.sbt形式的build定义文件就可以满足需要了，但如果我们想要使用SBT的一些高级特性，比如自定义Task， 多模块的项目构建， 就必须使用.scala形式的build定义了。 简单来讲，.sbt能干的事情，.scala形式的build定义都能干，反之，则不然。

要使用.scala形式的build定义，只要在当前项目根目录下的project/子目录下新建一个.scala后缀名的scala源代码文件即可，比如Build.scala（名称可以任意，一般使用Build.scala）

build的定义只要扩展sbt.Build，然后添加相应的逻辑即可，所有代码都是标准的scala代码，在Build定义中，我们可以添加更多的settings， 添加自定义的task，添加相应的val和方法定义等等...

## SBT项目结构的本质

一个SBT项目，与构建相关联的基本设施可以概况为3个部分

1. 项目的根目录， 比如```hello/```， 用来界定项目构建的边界；
2. 项目根目录下的```*.sbt```文件， 比如```hello/build.sbt```， 用来指定一般性的build定义；
3. 项目根目录下的```project/*.scala```文件，比如```hello/project/Build.scala```， 用来指定一些复杂的，```*.sbt```形式的build定义文件不太好搞的设置；

对于一个SBT项目来说，SBT在构建的时候，只关心两点：

1. build文件的类型（是```*.sbt```还是```*.scala```）；
2. build文件的存放位置（```*.sbt```文件只有存放在项目的根目录下， SBT才会关注它或者它们， 而```*.scala```文件只有存放在项目根目录下的project目录下，SBT才不会无视它或者它们）

```
hello/
    *.scala
    build.sbt
    project/
        *.scala
        build.sbt
        /project
            *.scala
```
从第一层的项目根目录开始， 其下project/目录内部再嵌套project/目录，可以无限递归，而且每一层的project/目录都界定了一个SBT项目，而每一个下层的project目录界定的SBT项目其实都是对上一层的SBT项目做支持，作为上一层SBT项目的build定义项目，这就跟俄罗斯娃娃这种玩具似的， 递归嵌套，一层又包一层

## 自定义SBT Task

大部分情况下，我们都是使用SBT内建的Task，比如compile，run等，实际上，除了这些，我们还可以在build定义中添加更多自定义的Task。

自定义SBT的Task其实很简单

1. 定义task；
2. 将task添加到项目的settings当中；
3. 使用自定义的task；

### 定义task
1. 定义一个TaskKey来标志Task
2. 定义Task的执行逻辑。
 
假设我们要定义一个简单的打印"hello, sbt~"信息的task
```
val hello = TaskKey[Unit]("hello", "just say hello")
```
- TaskKey的类型指定了对应task的执行结果，因为我们只想打印一个字符串，不需要返回什么数据，所以定义的是TaskKey[Unit]。
- 定义TaskKey最主要的一点就是要指定一个名称（```"hello"```），这个名称将是我们调用该task的标志性建筑。另外，还可以可选择的通过第二个参数传入该task的相应描述和说明 (```"just say hello"```)。

定义task对应的执行逻辑，并通过:=方法将相应的key和执行逻辑定义关联到一起：
```
hello := {
    println("hello, sbt~")
}
```

完整定義如下
```
val hello = TaskKey[Unit]("hello", "just say hello")

hello := {
    println("hello, sbt~")
}
```

### 将task添加到项目的settings当中

```
object ProjectBuild extends Build {

  val hello = TaskKey[Unit]("hello", "just say hello")

  val helloTaskSetting = hello := {
    println("hello, sbt~")
  }

  lazy val root = Project(id = "", base = file(".")).settings(Defaults.defaultSettings ++ Seq(helloTaskSetting): _*)

}
```

<TBC>
