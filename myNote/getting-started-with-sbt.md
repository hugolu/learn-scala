# Getting started with SBT

reference: http://scalatutorials.com/beginner/2013/07/18/getting-started-with-sbt/

```shell
$ mkdir test
$ cd test
$ mkdir -p src/main/scala
$ vi src/main/scala/hw.scala
$ vi build.sbt
$ sbt run
info] Set current project to hello (in build file:/Users/hugo/workspace.scala/sbt/test/)
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/}test...
[info] Resolving org.scala-lang.modules#scala-parser-combinators_2.11;[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/target/scala-2.11/classes...
[info] Running Hi
Hi!
[success] Total time: 4 s, completed 2016/3/16 下午 09:11:51
```

hw.scala:
```scala
object Hi {
  def main(args: Array[String]) = println("Hi!")
}
```

build.sbt:
```
name := "hello"
version := "1.0"
scalaVersion := "2.11.7"
```
