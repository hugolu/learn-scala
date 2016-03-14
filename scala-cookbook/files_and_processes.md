# Files and Processes

## How to Open and Read a Text File
### Using the concise syntax
```scala
scala> io.Source.fromFile("fruits.txt").getLines.toArray
res0: Array[String] = Array(apple, banana, coconut)
```
- This approach has the side effect of leaving the file open as long as the JVM is running, but for short-lived shell scripts, this shouldn’t be an issue; the file is closed when the JVM shuts down.

### Properly closing the file
```scala
scala> val bs = io.Source.fromFile("fruits.txt")
bs: scala.io.BufferedSource = non-empty iterator

scala> bs.getLines.toArray
res1: Array[String] = Array(apple, banana, coconut)

scala> bs.close
```
- To properly close the file, get a reference to the BufferedSource when opening the file, and manually close it when you’re finished with the file.

### Automatically closing the resource
Control.scala:
```scala
object Control {
  def using[A <: { def close(): Unit}, B](resource: A)(f: A=>B):B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}
```

TestUsing.scala:
```scala
import Control._

object TestUsing extends App {
  using(io.Source.fromFile("fruits.txt")) { source =>
    for(line <- source.getLines) {
      println(line)
    }
  }
}
```

fruits.txt:
```
apple
banana
coconut
```

compile & run
```shell
$ scalac *.scala
$ ls
Control$.class
Control.class
Control.scala
TestUsing$$anonfun$1$$anonfun$apply$1.class
TestUsing$$anonfun$1.class
TestUsing$.class
TestUsing$delayedInit$body.class
TestUsing.class
TestUsing.scala
fruits.txt
$ scala TestUsing
apple
banana
coconut
```

### Handling exceptions
```scala
scala> import java.io.{FileNotFoundException, IOException}
import java.io.{FileNotFoundException, IOException}

scala> :paste
// Entering paste mode (ctrl-D to finish)

try {
  io.Source.fromFile("no-such-file").getLines.foreach(println)
} catch {
  case e: FileNotFoundException => println("file not found")
  case e: Exception => println("exception")
}

// Exiting paste mode, now interpreting.

file not found
```
- use Scala’s `try/catch` syntax to handle exceptions generated when trying to open a file

### using + try/catch
Control.scala:
```scala
object Control {
  def using[A <: { def close(): Unit}, B](resource: A)(f: A=>B):B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}
```

ReadText.scala:
```scala
import Control._

object ReadText {
  def readTextFile(filename: String): Option[List[String]] = {
    try {
      val lines = using(io.Source.fromFile(filename)) { source =>
        (for (line <- source.getLines) yield line).toList
      }
      Some(lines)
    } catch {
      case e: Exception => None
    }
  }
}
```

TestRead.scala:
```scala
import Control._
import ReadText._

object TestRead extends App {
  def readFile(filename: String) = {
    println(s">> readFile($filename)")
    readTextFile(filename) match {
      case Some(lines) => lines.foreach(println)
      case None => println("cannot read file")
    }
  }

  readFile("fruits.txt")
  readFile("no-such-file")
}
```

```shell
$ scalac *.scala
$
$ scala TestRead
>> readFile(fruits.txt)
apple
banana
coconut
>> readFile(no-such-file)
cannot read file
```

## Reading and Writing Binary Files
## How to Process Every Character in a Text File
## How to Process a CSV File
## Pretending that a String Is a File
## Using Serialization
## Listing Files in a Directory
## Listing Subdirectories Beneath a Directory
## Executing External Commands
## Executing External Commands and Using STDOUT
## Handling STDOUT and STDERR for External Commands
## Building a Pipeline of Commands
## Redirecting the STDOUT and STDIN of External Commands
## Using AND (&&) and OR (||) with Processes
## Handling Wildcard Characters in External Commands
## How to Run a Process in a Different Directory
## Setting Environment Variables When Running Commands
## An Index of Methods to Execute External Commands
