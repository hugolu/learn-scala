# The Simple Build Tool (SBT)

## Creating a Project Directory Structure for SBT
mkdir4sbt.sh:
```shell
#!/bin/sh

name=$1
version=$2
scalaVersion=2.11.7

mkdir -p src/{main,test}/{java,resources,scala}
mkdir lib project target

# create an initial build.sbt file

cat <<END > build.sbt
name := "$name"
version := "$version"
scalaVersion := "$scalaVersion"
END
```
- copy `mkdir4sbt.sh` to `/usr/local/bin` for convenience sake 

```shell
$ mkdir test
$ cd test
$ mkdir4sbt.sh test 1.0
$ tree
.
├── build.sbt
├── lib
├── project
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── scala
│   └── test
│       ├── java
│       ├── resources
│       └── scala
└── target

12 directories, 1 file
$ cat build.sbt
name := "test"
version := "1.0"
scalaVersion := "2.11.7"
```

## Compiling, Running, and Packaging a Scala Project with SBT

```shell
$ src/main/scala/Hello.scala
$ sbt compile
$ sbt run
$ sbt package
$ jar tvf target/scala-2.11/test_2.11-1.0.jar
   265 Thu Mar 17 15:35:02 CST 2016 META-INF/MANIFEST.MF
     0 Thu Mar 17 15:35:02 CST 2016 foo/
     0 Thu Mar 17 15:35:02 CST 2016 foo/bar/
     0 Thu Mar 17 15:35:02 CST 2016 foo/bar/baz/
   798 Thu Mar 17 15:34:42 CST 2016 foo/bar/baz/Main$delayedInit$body.class
   976 Thu Mar 17 15:34:42 CST 2016 foo/bar/baz/Main.class
  2435 Thu Mar 17 15:34:42 CST 2016 foo/bar/baz/Main$.class
```

src/main/scala/Hello.scala:
```scala
package foo.bar.baz

object Main extends App {
  println("Hello, world")
}
```

## Running Tests with SBT and ScalaTest

template of test cases:
```scala
import org.scalatest.FunSuite

class HelloTests extends FunSuite {
  test("describing the target of the test") {
    result = expressions
    assert(result == desire)
  }
}
```

src/main/scala/Hello.scala:
```scala
package foo.bar.baz

object Main extends App {
  println("Hello, world")
}

case class Foo(var name: String)
```

src/test/scala/HelloTest.scala:
```scala
import foo.bar.baz._
import org.scalatest.FunSuite

class HelloTests extends FunSuite {
  test("the name of Foo is set correctly in constructor") {
    val f = Foo("foo")
    assert(f.name == "foo")
  }

  test("the Foo's name cat be changed") {
    val f = Foo("foo")
    f.name = "bar"
    assert(f.name == "bar")
  }
}
```

```shell
$ sbt test
[info] Set current project to test (in build file:/Users/hugo/workspace.scala/sbt/test/)
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/target/scala-2.11/classes...
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/target/scala-2.11/test-classes...
[info] HelloTests:
[info] - the name of Foo is set correctly in constructor
[info] - the Foo's name cat be changed
[info] Run completed in 308 milliseconds.
[info] Total number of tests run: 2
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 8 s, completed 2016/3/17 下午 03:47:50
```

## Managing Dependencies with SBT
## Controlling Which Version of a Managed Dependency Is Used
## Creating a Project with Subprojects
## Using SBT with Eclipse
## Generating Project API Documentation
## Specifying a Main Class to Run
## Using GitHub Projects as Project Dependencies
## Telling SBT How to Find a Repository (Working with Resolvers)
## Resolving Problems by Getting an SBT Stack Trace
## Setting the SBT Log Level
## Deploying a Single, Executable JAR File
## Publishing Your Library
## Using Build.scala Instead of build.sbt
## Using a Maven Repository Library with SBT
## Building a Scala Project with Ant
