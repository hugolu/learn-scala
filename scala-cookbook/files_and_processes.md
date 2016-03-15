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
fruits.txt (the file to process):
```
Apple
Banana
Cherry
```

```scala
scala> val source = io.Source.fromFile("fruits.txt")
source: scala.io.BufferedSource = non-empty iterator

scala> for (line <- source.getLines) println(line)
Apple
Banana
Cherry

scala> source.close
```

```scala
scala> val source = io.Source.fromFile("fruits.txt")
source: scala.io.BufferedSource = non-empty iterator

scala> source.toArray.map(_.toByte)
res19: Array[Byte] = Array(65, 112, 112, 108, 101, 10, 66, 97, 110, 97, 110, 97, 10, 67, 104, 101, 114, 114, 121, 10)

scala> source.close
```

Check text file byte by byte:
```scala
scala> val source = io.Source.fromFile("fruits.txt")
source: scala.io.BufferedSource = non-empty iterator

scala> val NEWLINE = 10
NEWLINE: Int = 10

scala> var newlineCnt = 0
newlineCnt: Int = 0

scala> for {
     |   char <- source
     |   if char.toByte == NEWLINE
     | } newlineCnt += 1

scala> println(newlineCnt)
3
```

Treat text file as binary file:
```scala
scala> val in = new FileInputStream("fruits.txt")
in: java.io.FileInputStream = java.io.FileInputStream@2aa3e9a6

scala> val NEWLINE = 10
NEWLINE: Int = 10

scala> var newlineCnt = 0
newlineCnt: Int = 0

scala> var c: Int = 0
c: Int = 0

scala> while({c = in.read; c != -1}) {
     |   if (c == NEWLINE) newlineCnt += 1
     | }

scala> println(newlineCnt)
3

scala> in.close
```

## How to Process a CSV File
fruits.csv (the file to process):
```
pple, 100, 123
Banana, 200, 456
Cherry, 300, 789
```

```scala
scala> val source = io.Source.fromFile("fruits.csv")
source: scala.io.BufferedSource = non-empty iterator

scala> for (line <- source.getLines) println(line.split(",").map(_.trim).mkString("|"))
Apple|100|123
Banana|200|456
Cherry|300|789

scala> source.close
```

```scala
scala> var source = io.Source.fromFile("fruits.csv")
source: scala.io.BufferedSource = non-empty iterator

scala> var lines = source.getLines.toArray.map { line =>
     |   line.split(",").map(_.trim).mkString("|")
     | }
lines: Array[String] = Array(Apple|100|123, Banana|200|456, Cherry|300|789)

scala> lines.foreach(println)
Apple|100|123
Banana|200|456
Cherry|300|789

scala> source.close
```

```scala
scala> val source = io.Source.fromFile("fruits.csv")
source: scala.io.BufferedSource = non-empty iterator

scala> val lines = source.getLines
lines: Iterator[String] = non-empty iterator

scala> val rows = new Array[Array[String]](3)
rows: Array[Array[String]] = Array(null, null, null)

scala> for((line, index) <- lines.zipWithIndex)
     |   rows(index) = line.split(",").map(_.trim)

scala> rows.foreach(row => println(row.mkString("|")))
Apple|100|123
Banana|200|456
Cherry|300|789
```

## Pretending that a String Is a File
```scala
scala> var source = io.Source.fromFile("fruits.txt")
source: scala.io.BufferedSource = non-empty iterator

scala> source.getLines.foreach(println)
apple
banana
cherry

scala> source.close
```

```scala
scala> var source = io.Source.fromString("apple\nbanana\ncherry")
source: scala.io.Source = non-empty iterator

scala> source.getLines.foreach(println)
apple
banana
cherry

scala> source.close
```

## Using Serialization
TestSerialization.scala:
```scala
import java.io._

object TestSerialization extends App {
  val foo = new Foo("hello", 123)

  val out = new ObjectOutputStream(new FileOutputStream("hello.serial"))
  out.writeObject(foo)

  val in = new ObjectInputStream(new FileInputStream("hello.serial"))
  val obj = in.readObject.asInstanceOf[Foo]

  println(obj)

  in.close
  out.close
}

@SerialVersionUID(100L)
class Foo (var str: String, var num: Int) extends Serializable {
  override def toString = s"Foo($str, $num)"
}
```

```shell
$ scalac TestSerialization.scala
$ scala TestSerialization
Foo(hello, 123)
```
- `ObjectInputStream.writeObject` for serial-out
- `ObjectOutputStream.readObject.asInstanceOf[T]` for serial-in

## Listing Files in a Directory

```scala
scala> import java.io.File
import java.io.File

scala> val dir = new File(".")
dir: java.io.File = .

scala> if (dir.isDirectory) dir.listFiles.filter(_.isFile).map(_.getName)
res0: Any = Array(file00, file01, file02)
```

## Listing Subdirectories Beneath a Directory

```shell
$ tree
.
├── dir1
│   ├── file01
│   ├── file02
│   └── file03
├── dir2
│   ├── file04
│   ├── file05
│   └── file06
├── dir3
│   ├── file07
│   ├── file08
│   └── file09
├── file00
├── file01
└── file02
```

```scala
scala> import java.io._
import java.io._

scala> val pwd = new File(".")
pwd: java.io.File = .

scala> def listFile(dir: File) = {
     |   val files = dir.listFiles.filter(_.isFile).map(_.getName)
     |   println(s"${dir.getName} has ${files.mkString(", ")}")
     | }
listFile: (dir: java.io.File)Unit

scala> listFile(pwd)
. has file00, file01, file02

scala> pwd.listFiles.filter(_.isDirectory).foreach(listFile)
dir1 has file01, file02, file03
dir2 has file04, file05, file06
dir3 has file07, file08, file09
```

## Executing External Commands
```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> "ls -al" !
warning: there was one feature warning; re-run with -feature for details
total 0
drwxr-xr-x   5 hugo  staff  170  3 15 15:08 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file00
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file01
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file02
res0: Int = 0

scala> "ls -al" !!
warning: there was one feature warning; re-run with -feature for details
res1: String =
"total 0
drwxr-xr-x   5 hugo  staff  170  3 15 15:08 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file00
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file01
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file02
"
```
- Use the `!` method to execute the command and get **its exit status**.
- Use the `!!` method to execute the command and get **its output**.

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> val process = Process("find /usr -print")
process: scala.sys.process.ProcessBuilder = [find, /usr, -print]

scala> val stream = process.lines
stream: Stream[String] = Stream(/usr, ?)

scala> val it = stream.iterator
it: Iterator[String] = non-empty iterator

scala> it.next
res0: String = /usr

scala> it.next
res1: String = /usr/bin
```
- With `lines`, you can immediately execute a command in the background.

## Executing External Commands and Using STDOUT

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> val result = "ls -al" !!
warning: there was one feature warning; re-run with -feature for details
result: String =
"total 0
drwxr-xr-x   5 hugo  staff  170  3 15 15:08 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file00
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file01
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file02
"

scala> println(result)
total 0
drwxr-xr-x   5 hugo  staff  170  3 15 15:08 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file00
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file01
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file02
```
- `!!` returns the STDOUT from the command rather than the exit code of the command.

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> val result = Seq("ls", "-al") !!
warning: there was one feature warning; re-run with -feature for details
result: String =
"total 0
drwxr-xr-x   5 hugo  staff  170  3 15 15:08 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file00
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file01
-rw-r--r--   1 hugo  staff    0  3 15 13:58 file02
```
- using a `Seq` is a good way to execute a system command that requires arguments

### Using the lines_! method
```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> "ls foo" !
warning: there was one feature warning; re-run with -feature for details
ls: foo: No such file or directory
res7: Int = 1

scala> "ls foo" !!
warning: there was one feature warning; re-run with -feature for details
ls: foo: No such file or directory
java.lang.RuntimeException: Nonzero exit value: 1
  at scala.sys.package$.error(package.scala:27)
  at scala.sys.process.ProcessBuilderImpl$AbstractBuilder.slurp(ProcessBuilderImpl.scala:132)
  at scala.sys.process.ProcessBuilderImpl$AbstractBuilder.$bang$bang(ProcessBuilderImpl.scala:102)
  ... 33 elided
```
- If the value of executing `!` is nonzero, you know that the executable is not available on the current system.

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> "which foo".lines_!.headOption
warning: there was one deprecation warning; re-run with -deprecation for details
res16: Option[String] = None

scala> "which ls".lines_!.headOption
warning: there was one deprecation warning; re-run with -deprecation for details
res17: Option[String] = Some(/bin/ls)
```

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> "which foo".lineStream_!.headOption
res18: Option[String] = None

scala> "which ls".lineStream_!.headOption
res19: Option[String] = Some(/bin/ls)
```

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> def execute(cmd: String) = {
     |   cmd.lines_!.headOption match {
     |     case None => println("cmd failed")
     |     case Some(str) => println(str)
     |   }
     | }
warning: there was one deprecation warning; re-run with -deprecation for details
execute: (cmd: String)Unit

scala> execute("ls foo.txt")
foo.txt

scala> execute("ls bar.txt")
ls: bar.txt: No such file or directory
cmd failed
```

## Handling STDOUT and STDERR for External Commands

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> val stdout = new StringBuilder
stdout: StringBuilder =

scala> val stderr = new StringBuilder
stderr: StringBuilder =

scala> val status = Seq("find", "/usr", "-name", "make") ! ProcessLogger(stdout append _, stderr append _)
status: Int = 1

scala> println(status)
1

scala> println(stdout)
/usr/bin/make/usr/local/Library/ENV/4.3/make

scala> println(stderr)
find: /usr/sbin/authserver: Permission denied

```
- `stdout` variable contains the STDOUT if the command is successful
- `stderr` contains the STDERR from the command if there are problems
- capture the output with a `ProcessLogger`

## Building a Pipeline of Commands
```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> ("ls -al" #| "wc -l").!!
res6: String =
"       5
"

scala> ("ls -al" #| "wc -l").!!.trim
res7: String = 5
```
- Use the `#|` method to pipe the output from one command into the input stream of another command.

```scala
scala> ("whoami | date").!!.trim
usage: whoami
java.lang.RuntimeException: Nonzero exit value: 1
```
- This doesn’t work because the piping capability comes from a shell (Bourne shell, Bash, etc.), and when you run a command like this, you don’t have a shell.

```scala
scala> ("whoami" ### "date").!!.trim
res12: String =
hugo
2016年 3月15日 周二 16時07分28秒 CST
```
- use the `###` operator as the Unix `;` symbol

## Redirecting the STDOUT and STDIN of External Commands

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> import java.io._
import java.io._

scala> ("ls -al" #> new File("ls.txt")).!
res1: Int = 0

scala> ("cat" #< new File("ls.txt")).!!
res2: String =
"total 8
drwxr-xr-x   5 hugo  staff  170  3 15 16:19 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff    0  3 15 15:53 FRED
-rw-r--r--   1 hugo  staff    0  3 15 16:19 ls.txt
-rwxr-xr-x   1 hugo  staff  272  3 15 15:53 scala.sh
"
```

## Using AND (&&) and OR (||) with Processes

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> ("ls hello.txt" #&& "wc -c hello.txt" #|| "echo file not found").!
hello.txt
      12 hello.txt
res4: Int = 0
```

## Handling Wildcard Characters in External Commands

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> "ls *".!
ls: *: No such file or directory
res0: Int = 1

scala> Seq("/bin/sh", "-c", "ls -al *").!!.trim
res1: String = -rw-r--r--  1 hugo  staff  12  3 15 16:22 hello.txt
```
- Putting a shell wildcard character like `*` into a command doesn’t work because the `*` needs to be interpreted and expanded by a shell, like the Bourne or Bash shells.

## How to Run a Process in a Different Directory

```scala
scala> Process("ls -al", new File("/tmp")).!!
res14: String =
"total 0
drwxrwxrwt  7 root  wheel  238  3 15 15:00 .
drwxr-xr-x@ 6 root  wheel  204 11 24  2014 ..
drwx------  4 hugo  wheel  136  3 11 15:25 .vbox-hugo-ipc
drwx------  3 hugo  wheel  102  3 15 15:00 KSOutOfProcessFetcher.501.ppfIhqX0vjaTSb8AJYobDV7Cu68=
drwx------  3 hugo  wheel  102  3  7 21:55 com.apple.launchd.HZVHs3tJPU
drwx------  3 hugo  wheel  102  3  7 21:55 com.apple.launchd.hQVmFSLdqo
drwx------  3 hugo  wheel  102  3  7 21:55 com.apple.launchd.tRG29JZqtL
"
```
- `Process().!!` creates a ProcessBuilder and exexutes it

## Setting Environment Variables When Running Commands

/Users/hugo/bin/hello.sh:
```shell
#!/bin/sh
echo "hello world"
```

/Users/hugo/bin/foo.sh:
```shell
#!/bin/sh

echo VAR1=${VAR1}
echo VAR2=${VAR2}
```

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> import java.io._
import java.io._

scala> Process("hello.sh", new File("/Users/hugo/bin")).!!.trim
res0: String = hello world

scala> Process("hello.sh", None, "PATH" -> ".:/Users/hugo/bin").!!.trim
res1: String = hello world

scala> Process("foo.sh", new File("/Users/hugo/bin"), "VAR1" -> "foo", "VAR2" -> "bar").!!.trim
res2: String =
VAR1=foo
VAR2=bar
```

## An Index of Methods to Execute External Commands
reference: http://www.scala-lang.org/api/current/index.html#scala.sys.process.ProcessBuilder

| Method | Description |
|--------|-------------|
| `!: Int` | Starts the process represented by this builder, blocks until it exits, and returns the exit code. |
| `!!: String` | Starts the process represented by this builder, blocks until it exits, and returns the output as a String. Standard error is sent to the console. If the exit code is non-zero, an exception is thrown. |
| `###(other: ProcessBuilder): ProcessBuilder` | Constructs a command that will run this command and then other. The exit code will be the exit code of other. |
| `#&&(other: ProcessBuilder): ProcessBuilder`| Constructs a command that runs this command first and then other if this command succeeds. |
| `#||(other: ProcessBuilder): ProcessBuilder` | Constructs a command that runs this command first and then other if this command does not succeed. |
| `#|(other: ProcessBuilder): ProcessBuilder` | Constructs a command that will run this command and pipes the output to other. other must be a simple command. |
| `lineStream: Stream[String]` | Starts the process represented by this builder. The output is returned as a Stream that blocks when lines are not available but the process has not completed. |
| `lineStream_!: Stream[String]` | Starts the process represented by this builder. The output is returned as a Stream that blocks when lines are not available but the process has not completed. |
| `#<(in: ⇒ InputStream): ProcessBuilder` | Reads the given InputStream into the input stream of this process. |
| `#>(out: ⇒ OutputStream): ProcessBuilder` | Writes the output stream of this process to the given OutputStream. |
| `#>>(f: File): ProcessBuilder` | Appends the output stream of this process to the given file. |

Deprecated Value Members:
- `lines: Stream[String]`: Deprecated (renamed). Use `lineStream` instead.
- `lines_!: Stream[String]`: Deprecated (renamed). Use `lineStream_!` instead.

`lineStream`: returns immediately like `run`, and the output being generated is provided through a `Stream[String]`. Getting the next element of that `Stream` may block until it becomes available. This method will throw an exception if the return code is different than zero -- if this is not desired, use the `lineStream_!` method.

```scala
scala> import scala.sys.process._
import scala.sys.process._

scala> val stream = "ls -al".lineStream
stream: Stream[String] = Stream(total 8, ?)

scala> if (stream.isEmpty == false) stream.foreach(println)
total 8
drwxr-xr-x   3 hugo  staff  102  3 15 16:22 .
drwxr-xr-x  13 hugo  staff  442  3 15 13:58 ..
-rw-r--r--   1 hugo  staff   12  3 15 16:22 hello.txt

scala> val stream = "ls foo".lineStream
ls: foo: No such file or directory
java.lang.RuntimeException: Nonzero exit code: 1

scala> val stream = "ls foo".lineStream_!
ls: foo: No such file or directory
stream: Stream[String] = Stream()

scala> if (stream.isEmpty == false) stream.foreach(println)
```
