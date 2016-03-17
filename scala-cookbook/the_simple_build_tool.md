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
- If you have JAR files (unmanaged dependencies) that you want to use in your project, simply copy them to the lib folder in the root directory of your SBT project, and SBT will find them automatically. If those JARs depend on other JAR files, you’ll have to download those other JAR files and copy them to the lib directory as well.
- If you have a single managed dependency, add a libraryDependencies line like this to your build.sbt file. A managed dependency is a dependency that’s managed by the build tool.

two general forms for adding a managed dependency to a build.sbt:
- `libraryDependencies += groupID % artifactID % revision`
- `libraryDependencies += groupID % artifactID % revision % configuration`

`libraryDependencies += "org.specs2" %% "specs2" % "1.14" % "test"` >>>
```maven
<dependency>
   <groupId>org.specs2</groupId>
   <artifactId>specs2_2.10</artifactId>
   <version>1.14</version>
   <scope>test</scope>
</dependency>
```

### using `Seq()`
```scala
libraryDependencies += "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.4"
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
libraryDependencies += "org.foobar" %% "foobar" % "1.6"
```

```scala
libraryDependencies ++= Seq(
   "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.4",
   "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
   "org.foobar" %% "foobar" % "1.8"
)
```

## Controlling Which Version of a Managed Dependency Is Used

### specific revision
```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```
```shell
$ sbt reload update "show update"
...
[info] 	org.scalatest:scalatest_2.11
[info] 		- 2.2.6
[info] 			status: release
[info] 			publicationDate: Wed Jan 06 03:54:37 CST 2016
[info] 			resolver: sbt-chain
[info] 			artifactResolver: sbt-chain
[info] 			evicted: false
[info] 			homepage: http://www.scalatest.org
[info] 			isDefault: false
[info] 			configurations: default(compile), default, compile, runtime, master
[info] 			licenses: (the Apache License, ASL Version 2.0,Some(http://www.apache.org/licenses/LICENSE-2.0))
[info] 			callers: default:test_2.11:1.0
```

### latest.integration
```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "latest.integration" % "test"
```
```shell
$ sbt reload update "show update"
...
[info] 	org.scalatest:scalatest_2.11
[info] 		- 3.0.0-SNAP13
[info] 			status: release
[info] 			publicationDate: Thu Nov 12 20:44:03 CST 2015
[info] 			resolver: sbt-chain
[info] 			artifactResolver: sbt-chain
[info] 			evicted: false
[info] 			homepage: http://www.scalatest.org
[info] 			isDefault: false
[info] 			configurations: default(compile), default, compile, runtime, master
[info] 			licenses: (the Apache License, ASL Version 2.0,Some(http://www.apache.org/licenses/LICENSE-2.0))
[info] 			callers: default:test_2.11:1.0
```

### latest.milestone
```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "latest.milestone" % "test"
```
```scala
$ sbt reload update "show update"
...
[info] 	org.scalatest:scalatest_2.11
[info] 		- 3.0.0-SNAP13
[info] 			status: release
[info] 			publicationDate: Thu Nov 12 20:44:03 CST 2015
[info] 			resolver: sbt-chain
[info] 			artifactResolver: sbt-chain
[info] 			evicted: false
[info] 			homepage: http://www.scalatest.org
[info] 			isDefault: false
[info] 			configurations: default(compile), default, compile, runtime, master
[info] 			licenses: (the Apache License, ASL Version 2.0,Some(http://www.apache.org/licenses/LICENSE-2.0))
[info] 			callers: default:test_2.11:1.0
```

### revision with a `+` character
```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.+" % "test"
```
```shell
$ sbt reload update "show update"
...
[info] 	org.scalatest:scalatest_2.11
[info] 		- 2.1.7
[info] 			status: release
[info] 			publicationDate: Sat May 17 14:50:00 CST 2014
[info] 			resolver: sbt-chain
[info] 			artifactResolver: sbt-chain
[info] 			evicted: false
[info] 			homepage: http://www.scalatest.org
[info] 			isDefault: false
[info] 			configurations: default(compile), default, compile, runtime, master
[info] 			licenses: (the Apache License, ASL Version 2.0,Some(http://www.apache.org/licenses/LICENSE-2.0))
[info] 			callers: default:test_2.11:1.0
```

## Creating a Project with Subprojects

```shell
$ mkdir test; cd test; mkdir4sbt.sh hello 1.0
$ (mkdir foo; cd foo; mkdir4sbt.sh foo 1.0)
$ (mkdir bar; cd bar; mkdir4sbt.sh bar 1.0)
$ tree
.
├── bar
│   ├── build.sbt
│   ├── lib
│   ├── project
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   ├── resources
│   │   │   └── scala
│   │   └── test
│   │       ├── java
│   │       ├── resources
│   │       └── scala
│   └── target
├── build.sbt
├── foo
│   ├── build.sbt
│   ├── lib
│   ├── project
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   ├── resources
│   │   │   └── scala
│   │   └── test
│   │       ├── java
│   │       ├── resources
│   │       └── scala
│   └── target
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

38 directories, 3 files
```

build.sbt:
```scala
name := "hello"
version := "1.0"
scalaVersion := "2.11.7"
```

Foo/build.sbt:
```scala
name := "foo"
version := "1.0"
scalaVersion := "2.11.7"
```

Bar/build.sbt:
```scala
name := "bar"
version := "1.0"
scalaVersion := "2.11.7"
```

project/Build.scala:
```scala
import sbt._
import Keys._

object HelloBuild extends Build {
  lazy val root = Project(id = "hello",
    base = file(".")) aggregate(foo, bar) dependsOn(foo, bar)

  lazy val foo = Project(id = "foo",
    base = file("foo"))

  lazy val bar = Project(id = "bar",
    base = file("bar"))
}
```

src/main/scala/Hello.scala:
```scala
package com.whatever.hello

import com.whatever.foo._
import com.whatever.bar._

object Hello extends App {
  println(Foo(123))
  println(Bar(456))
}
```

Foo/src/main/scala/Foo.scala:
```scala
package com.whatever.foo

object Foo extends App {
  println("Hello, I'm Foo")
}

case class Foo(num: Int)
```

Bar/src/main/scala/Bar.scala:
```scala
package com.whatever.bar

object Bar extends App {
  println("Hello, I'm Bar")
}

case class Bar(num: Int)
```

```shell
$ sbt run
[info] Loading project definition from /Users/hugo/workspace.scala/sbt/test/project
[info] Set current project to hello (in build file:/Users/hugo/workspace.scala/sbt/test/)
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/}foo...
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/}bar...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/}hello...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/bar/target/scala-2.11/classes...
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/foo/target/scala-2.11/classes...
[info] Compiling 1 Scala source to /Users/hugo/workspace.scala/sbt/test/target/scala-2.11/classes...
[info] Running com.whatever.hello.Hello
Foo(123)
Bar(456)
[success] Total time: 7 s, completed 2016/3/17 下午 04:54:46
```
```shell
$ (cd Foo; sbt run)
[info] Set current project to foo (in build file:/Users/hugo/workspace.scala/sbt/test/foo/)
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/foo/}foo...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Running com.whatever.foo.Foo
Hello, I'm Foo
[success] Total time: 2 s, completed 2016/3/17 下午 04:55:29
```
```shell
$ (cd Bar; sbt run)
[info] Set current project to bar (in build file:/Users/hugo/workspace.scala/sbt/test/bar/)
[info] Updating {file:/Users/hugo/workspace.scala/sbt/test/bar/}bar...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Running com.whatever.bar.Bar
Hello, I'm Bar
[success] Total time: 2 s, completed 2016/3/17 下午 04:55:48
```

## Using SBT with Eclipse
## Generating Project API Documentation

```shell
$ sbt doc
```
-  places the root `index.html` Scaladoc file at `target/scala-2.11/api/index.html` under the root directory of your project.

## Specifying a Main Class to Run

```shell
$ mkdir test; cd test; mkdir4sbt.sh hello 1.0
$ vi Hello.scala
$ vi Foo.scala
$ vi Bar.scala
$ sbt run
[info] Set current project to hello (in build file:/Users/hugo/workspace.scala/sbt/test/)
[warn] Multiple main classes detected.  Run 'show discoveredMainClasses' to see the list

Multiple main classes detected, select one to run:

 [1] com.whatever.bar.Bar
 [2] com.whatever.foo.Foo
 [3] com.whatever.hello.Hello

Enter number: 1

[info] Running com.whatever.bar.Bar
I'm Bar
[success] Total time: 4 s, completed 2016/3/17 下午 05:09:38

$ sbt "run-main com.whatever.foo.Foo"
[info] Set current project to hello (in build file:/Users/hugo/workspace.scala/sbt/test/)
[info] Running com.whatever.foo.Foo
I'm Foo
[success] Total time: 1 s, completed 2016/3/17 下午 05:10:05
```

Hello.scala:
```scala
package com.whatever.hello

object Hello {
  def main(args: Array[String]) = println("Hello, world")
}
```

Foo.scala:
```scala
package com.whatever.foo

object Foo {
  def main(args: Array[String]) = println("I'm Foo")
}
```

Bar.scala:
```scala
package com.whatever.bar

object Bar {
  def main(args: Array[String]) = println("I'm Bar")
}
```

### set the main class for 'sbt run'
build.sbt
```scala
name := "hello"
version := "1.0"
scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("com.whatever.hello.Hello")
```
```shell
$ sbt run
[info] Set current project to hello (in build file:/Users/hugo/workspace.scala/sbt/test/)
[info] Running com.whatever.hello.Hello
Hello, world
[success] Total time: 1 s, completed 2016/3/17 下午 05:14:39
```

## Using GitHub Projects as Project Dependencies
## Telling SBT How to Find a Repository (Working with Resolvers)

Use the resolvers key in the build.sbt file to add any unknown Ivy repositories. 
```scala
resolvers += "repository name" at "location"

resolvers ++= Seq(
   "repository name 1" at "location 1"
   "repository name 2" at "location 2"
   ...
)
```

## Resolving Problems by Getting an SBT Stack Trace

When an SBT command silently fails (typically with a “Nonzero exit code” message), but you can’t tell why, run your command from within the SBT shell, then use the last run command after the command that failed.

```scala
```
$ sbt run   // something fails here, but you can't tell what

$ sbt
> run       // failure happens again
> last run  // this shows the full stack trace
```

## Setting the SBT Log Level

Set the SBT logging level in your build.sbt file with this setting:
```scala
logLevel := Level.Debug
```
- Level.Info
- Level.Warning
- Level.Error

## Deploying a Single, Executable JAR File
## Publishing Your Library
## Using Build.scala Instead of build.sbt

reference: [What is the difference between build.sbt and build.scala?](http://stackoverflow.com/questions/18000103/what-is-the-difference-between-build-sbt-and-build-scala)

`build.sbt`:
```scala
name := "hello"
version := "1.0"
```
is a shorthand notation roughly equivalent to this `project/Build.scala`:
```scala
import sbt._
import Keys._

object Build extends Build {
  lazy val root = Project(id = "root", base = file(".")).settings(
    name := "hello",
    version := "1.0"      
  )
}
```

## Using a Maven Repository Library with SBT
## Building a Scala Project with Ant
