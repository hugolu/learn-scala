# Command-Line Tasks

## Getting Started with the Scala REPL
```scala
scala> var x, y = 1
x: Int = 1
y: Int = 1

scala> x + y
res0: Int = 2

scala> val a = Array(1,2,3)
a: Array[Int] = Array(1, 2, 3)

scala> a.sum
res1: Int = 6

scala> res1.getClass
res2: Class[Int] = int
```

### REPL command-line options
```scala
scala> Runtime.getRuntime.maxMemory / 1024
res0: Long = 233472

scala> :quit
$ scala -J-Xms256m -J-Xmx512m

scala> Runtime.getRuntime.maxMemory / 1024
res0: Long = 466432
```

### Deprecation and feature warnings
```scala
scala> import sys.process._
import sys.process._

scala> "date" !
warning: there was one feature warning; re-run with -feature for details
2016年 3月21日 周一 21時50分02秒 CST
res8: Int = 0

scala> :quit
$ scala -feature

scala> "date" !
<console>:14: warning: postfix operator ! should be enabled
by making the implicit value scala.language.postfixOps visible.
This can be achieved by adding the import clause 'import scala.language.postfixOps'
or by setting the compiler option -language:postfixOps.
See the Scala docs for value scala.language.postfixOps for a discussion
why the feature should be explicitly enabled.
       "date" !
              ^
2016年 3月21日 周一 21時50分37秒 CST
res0: Int = 0

scala> "date".!
2016年 3月21日 周一 21時50分45秒 CST
res1: Int = 0
```

## Pasting and Loading Blocks of Code into the REPL

### The `:paste` command
```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)

if (true)
  println("True")
else
  println("False")

// Exiting paste mode, now interpreting.

True
```

### The `:load` command
```scala
$ echo "case class Foo(n: Int)" > Foo.scala
$ scala

scala> :load Foo.scala
Loading Foo.scala...
defined class Foo

scala> Foo(123)
res0: Foo = Foo(123)
```

## Adding JAR Files and Classes to the REPL Classpath
Foo.scala:
```scala
package com.whatever
case class Foo(n: Int)
```

Foo.mf:
```
Main-Class: Foo
```

```shell
$ scala Foo.scala
$ tree .
.
├── Foo.scala
└── com
    └── whatever
        ├── Foo$.class
        └── Foo.class
$ jar -cvfm Foo.jar Foo.mf com/whatever/Foo.class
已新增資訊清單
新增: com/whatever/Foo.class (讀=4036)(寫=2235)(壓縮 44%)
$ scala -classpath Foo.jar

scala> import com.whatever._
import com.whatever._

scala> var foo = Foo(123)
foo: com.whatever.Foo = Foo(123)
```

## Running a Shell Command from the REPL
repl-commands:
```scala
import sys.process._

def clear = "clear".!
def cmd(cmd: String) = cmd.!!.trim
def ls(dir: String) = println(cmd(s"ls -al $dir"))
```
```
$ scala -i repl-commands
Loading repl-commands...
import sys.process._
clear: Int
cmd: (cmd: String)String
ls: (dir: String)Unit

Welcome to Scala version 2.11.7 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_73).
Type in expressions to have them evaluated.
Type :help for more information.

scala> ls("/opt")
total 0
drwxr-xr-x@  4 root  wheel   136  8 11  2015 .
drwxr-xr-x  33 root  wheel  1190  3  7 21:56 ..
drwxr-xr-x   8 root  wheel   272  8 12  2014 X11
drwxr-xr-x   4 root  wheel   136  3 18  2015 vagrant

scala> cmd("echo hello world")
res1: String = hello world
```

## Compiling with scalac and Running with scala
hello.scala:
```scala
object Hello extends App {
  println("hello world")
}
```
```shell
$ scalac hello.scala
$ scala Hello
hello world
```

## Disassembling and Decompiling Scala Code

### Use `javap`
Foo.scala:
```scala
case class Foo(n: Int, s: String)
```
```shell
$ scalac Foo.scala
$ javap Foo
Compiled from "Foo.scala"
public class Foo implements scala.Product,scala.Serializable {
  public static scala.Option<java.lang.Object> unapply(Foo);
  public static Foo apply(int);
  public static <A> scala.Function1<java.lang.Object, A> andThen(scala.Function1<Foo, A>);
  public static <A> scala.Function1<A, Foo> compose(scala.Function1<A, java.lang.Object>);
  public int n();
  public Foo copy(int);
  public int copy$default$1();
  public java.lang.String productPrefix();
  public int productArity();
  public java.lang.Object productElement(int);
  public scala.collection.Iterator<java.lang.Object> productIterator();
  public boolean canEqual(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
  public boolean equals(java.lang.Object);
  public Foo(int);
}
```

### Use `scalac` option `-Xprint:parse`
Hello.scala;
```scala
object Hello extends App {
  println("hello world")
}
```
```shell
$ scalac -Xprint:parse Hello.scala
[[syntax trees at end of                    parser]] // Hello.scala
package <empty> {
  object Hello extends App {
    def <init>() = {
      super.<init>();
      ()
    };
    println("hello world")
  }
}
```
### Use `jad`
Bar.scala
```scala
case object Bar
```
```shell
$ scalac Bar.scala
$ jad Bar
$ cat Bar.jad
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Bar.scala

import scala.collection.Iterator;

public final class Bar
{

    public static String toString()
    {
        return Bar$.MODULE$.toString();
    }

    public static int hashCode()
    {
        return Bar$.MODULE$.hashCode();
    }

    public static boolean canEqual(Object obj)
    {
        return Bar$.MODULE$.canEqual(obj);
    }

    public static Iterator productIterator()
    {
        return Bar$.MODULE$.productIterator();
    }

    public static Object productElement(int i)
    {
        return Bar$.MODULE$.productElement(i);
    }

    public static int productArity()
    {
        return Bar$.MODULE$.productArity();
    }

    public static String productPrefix()
    {
        return Bar$.MODULE$.productPrefix();
    }
}
```
## Finding Scala Libraries
## Generating Documentation with scaladoc
## Faster Command-Line Compiling with fsc
Foo.bar:
```scala
case object Foo
```

Bar.scala:
```scala
case object Bar
```

Test.scala:
```scala
object Test extends App {
  var foo = Foo
  var bar = Bar

  println(foo, bar)
}
```

```shell
$ fsc *.scala
$ ls *.class
Bar$.class                  Test$.class
Bar.class                   Test$delayedInit$body.class
Foo$.class                  Test.class
Foo.class
$ scala Test
(Foo,Bar)

$ ps auxw | grep CompileServer
hugo            85718   0.0  0.0  2443608   1100 s000  S     9:19下午   0:00.01 bash /usr/local/Cellar/scala/2.11.7/libexec/bin/scala scala.tools.nsc.CompileServer

$ fsc -shutdown
[Compile server exited]
```

## Using Scala as a Scripting Language
print.sh:
```script
~/workspace.scala/cmdline$ cat printall.sh
#!/bin/sh
exec scala "$0" "$@"
!#

args.foreach(println)
```
- The `#!` in the first line is the usual way to start a Unixs hell script. It invokes a Unix Bourne shell.
- The `exec` command is a shell built-in. `$0` expands to the name of the shell script, and `$@` expands to the positional parameters.
- The `!#` characters as the third line of the scripti show the header section is closed.
```shell
$ ./print.sh hello world
hello
world
```
```shell
$ scala println.scala hello world
hello
world
```

print2.sh
```script
#!/bin/sh
exec scala "$0" "$@"
!#

object Main {
  def main(args: Array[String]) {
    args.foreach(println)
  }
}

Main.main(args)
```
```shell
$ ./print2.sh hello world
hello
world
```

## Accessing Command-Line Arguments from a Script
print3.sh:
```script
#!/bin/sh
exec scala "$0" "$@"
!#

if (args.length != 2) {
  Console.err.println("Usage: print3 <first string> <second string>")
  System.exit(1)
}

val first = args(0)
val second = args(1)

println(s"$first, $second")
```
```shell
$ ./print3.sh
Usage: print3 <first string> <second string>

$ ./print3.sh hello world
hello, world
```

## Prompting for Input from a Scala Shell Script

read.sh:
```script
#!/bin/sh
exec scala "$0" "$@"
!#

val name = readLine("What's your name? ")
val age = readLine("How old are you? ").toInt

println(s"Hi $name, you're $age years old.")
```
```
$ ./read.sh
one warning found
What's your name? Scala
How old are you? 12
Hi Scala, you're 12 years old.
```

## Make Your Scala Scripts Run Faster
echo1.sh:
```script
#!/bin/sh
exec scala "$0" "$@"
!#

println("args:")
args foreach println
```
```shell
$ time ./echo1.sh hello world
args:
hello
world

real	0m0.986s
user	0m0.428s
sys	0m0.078s
$ time ./echo1.sh hello world
args:
hello
world

real	0m0.728s
user	0m0.413s
sys	0m0.075s
```

## use the option `-savecompiled`
Run the script once. This generates a compiled version of the script. After that, the script runs with a consistently lower real time (wall clock) on all subsequent runs.

echo2.sh:
```script
#!/bin/sh
exec scala -savecompiled "$0" "$@"
!#

println("args:")
args foreach println
```
```shell
$ time ./echo2.sh hello world
args:
hello
world

real	0m0.992s
user	0m0.432s
sys	0m0.080s

$ ls -al echo2.sh*
-rwxr-xr-x  1 hugo  staff    87  3 21 21:30 echo2.sh
-rw-r--r--  1 hugo  staff  2698  3 21 21:33 echo2.sh.jar

$ time ./echo2.sh hello world
args:
hello
world

real	0m0.319s
user	0m0.314s
sys	0m0.064s
```
