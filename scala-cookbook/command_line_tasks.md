# Command-Line Tasks

## Getting Started with the Scala REPL
## Pasting and Loading Blocks of Code into the REPL
## Adding JAR Files and Classes to the REPL Classpath

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
## Disassembling and Decompiling Scala Code
## Finding Scala Libraries
## Generating Documentation with scaladoc
## Faster Command-Line Compiling with fsc
## Using Scala as a Scripting Language
## Accessing Command-Line Arguments from a Script
## Prompting for Input from a Scala Shell Script
## Make Your Scala Scripts Run Faster
