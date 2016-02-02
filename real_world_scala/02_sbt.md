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
