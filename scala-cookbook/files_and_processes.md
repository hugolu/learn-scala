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

### my trail
Test.scala:
```scala
import scala.io._

object Test extends App {
  def ReadText(filename: String) = {
    var in = None: Option[BufferedSource]

    println(s">> ReadText($filename)")
    try {
      in = Some(Source.fromFile(filename))
      for (line <- in.get.getLines)
        println(line)
    } catch {
      case e: Exception => println("something's wrong")
    } finally {
      if (in.isDefined) in.get.close
    }
  }

  ReadText("fruits.txt")
  ReadText("no_such_file")
}
```

```shell
$ scalac Test.scala
$ scala Test
>> ReadText(fruits.txt)
apple
banana
coconut

>> ReadText(no_such_file)
something's wrong
```

## Writing Text Files
```scala
scala> import java.io._
import java.io._

scala> val pw = new PrintWriter(new File("foo.txt"))
pw: java.io.PrintWriter = java.io.PrintWriter@7bc18623
scala> pw.write("hello world")
scala> pw.close

scala> var fw = new FileWriter(new File("qiz.txt"))
fw: java.io.FileWriter = java.io.FileWriter@4e73e6cd
scala> fw.write("hello world")
scala> fw.close

scala> val bw = new BufferedWriter(new FileWriter(new File("bar.txt")))
bw: java.io.BufferedWriter = java.io.BufferedWriter@3579defb
scala> bw.write("hello world")
scala> bw.close
```
- `FileWriter` throws IOExceptions, whereas `PrintWriter` does not throw exceptions, and instead sets Boolean flags that can be checked.

## Reading and Writing Binary Files

Reference:
- https://docs.oracle.com/javase/7/docs/api/java/io/FileInputStream.html
- https://docs.oracle.com/javase/7/docs/api/java/io/FileOutputStream.html

```scala
scala> import java.io._
import java.io._

scala> val bytes = Array.fill[Byte](1024)(0)
bytes: Array[Byte] = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,...

scala> val in = new FileInputStream("input.txt")
in: java.io.FileInputStream = java.io.FileInputStream@566caa0d

scala> val byte = in.read
byte: Int = 14

scala> in.read(bytes)
res0: Int = 1024

scala> in.read(bytes, 100, 100)
res1: Int = 100

scala> in.close

scala> val out = new FileOutputStream("output.txt")
out: java.io.FileOutputStream = java.io.FileOutputStream@36665b29

scala> out.write(byte)

scala> out.write(bytes)

scala> out.write(bytes, 100, 100)

scala> out.close
```
- `int read()`: Reads a byte of data from this input stream.
- `int read(byte[] b)`: Reads up to b.length bytes of data from this input stream into an array of bytes.
- `int read(byte[] b, int off, int len)`: Reads up to len bytes of data from this input stream into an array of bytes.
- `void write(int b)`: Writes the specified byte to this file output stream.
- `void write(byte[] b)`: Writes b.length bytes from the specified byte array to this file output stream.
- `void rite(byte[] b, int off, int len)`: Writes len bytes from the specified byte array starting at offset off to this file output stream.

copy byte by byte:
```scala
scala> import java.io._
import java.io._

scala> val in = new FileInputStream("100k")
in: java.io.FileInputStream = java.io.FileInputStream@74491643

scala> val out = new FileOutputStream("100k.copy")
out: java.io.FileOutputStream = java.io.FileOutputStream@c3f2507

scala> var c: Int = 0
c: Int = 0

scala> while ({c = in.read; c != -1}) { out.write(c) }

scala> in.close

scala> out.close
```

copy with a buffer of array
```scala
scala> import java.io._
import java.io._

scala> val in = new FileInputStream("100k")
in: java.io.FileInputStream = java.io.FileInputStream@2ecb6c33

scala> val out = new FileOutputStream("100k.copy")
out: java.io.FileOutputStream = java.io.FileOutputStream@3b4bd56d

scala> val bytes = Array.fill[Byte](1024)(0)
bytes: Array[Byte] = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,...
scala>

scala> var length = 0
length: Int = 0

scala> while({length = in.read(bytes); length > 0}) { out.write(bytes, 0, length) }

scala> in.close

scala> out.close
```

### FileInputStream/FileOutputStream & Option & Copying buffer
CopyBinary.scala
```scala
import java.io._

object CopyBinary extends App {
  var in = None : Option[FileInputStream]
  var out = None : Option[FileOutputStream]
  val bytes = Array.fill[Byte](1024)(0)
  var length = 0

  try {
    in = Some(new FileInputStream("100k"))
    out = Some(new FileOutputStream("100k.copy"))

    while ({length = in.get.read(bytes); length > 0}) {
      out.get.write(bytes, 0, length)
    }
  } catch {
    case e: IOException => e.printStackTrace
  } finally {
    println ("close in & out")
    if (in.isDefined) in.get.close
    if (out.isDefined) out.get.close
  }
}
```

```shell
$ scalac CopyBinary.scala
$ scala CopyBinary
close in & out
$ diff 100k 100k.copy
```
> Hope that Scala will support "optional binding" & "implicitly unwrapped optionals" like Swift in the near feature :)

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
