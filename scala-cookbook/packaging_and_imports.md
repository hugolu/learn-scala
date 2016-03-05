# Packaging and Imports

## Packaging with the Curly Braces Style Notation
```shell
$ tree
.
├── Bar.scala
├── Foo.scala
└── test.scala
```

### Foo.scala
```scala
package foo

class Foo { override def toString = "foo.Foo" }
```

### Bar.scala
```scala
package foo.bar {
  class Bar { override def toString = "foo.bar.Bar" }
}
```

### test.scala
```scala
import foo._
import foo.bar._

object test extends App {
  println(new Foo)
  println(new Bar)
}
```

### compile & run
```shell
$ scalac Foo.scala
$ scalac Bar.scala
$ scalac test.scala
$ tree
.
├── Bar.scala
├── Foo.scala
├── foo
│   ├── Foo.class
│   └── bar
│       └── Bar.class
├── test$.class
├── test$delayedInit$body.class
├── test.class
└── test.scala

$ scala test
foo.Foo
foo.bar.Bar
```

## Importing One or More Members
```scala
import java.io.File
import java.io.IOException
import java.io.FileNotFoundException
```

```scala
import java.io.{File, IOException, FileNotFoundException}
```
- import selector clause

```scala
import java.io._
```
- The `_` character in this example is similar to the `*` wildcard character in Java.

### Placing import statements anywhere
```scala
package foo

import java.io.File
import java.io.PrintWriter

class Foo {
  import javax.swing.JFrame // only visible in this class
  // ...
}

class Bar {
  import scala.util.Random // only visible in this class
  // ...
}
```

## Renaming Members on Import

```scala
package foo {
  class Foo { override def toString = "foo.Foo" }
}

package test {
	import foo.{Foo => Bar}

  object test {
    var bar = new Bar                             //> bar  : foo.Foo = foo.Foo
  }
}
```

As an interesting combination of several recipes, not only can you rename classes on import, but you can even rename class members. 

```scala
scala> import System.out.{println => p}
import System.out.{println=>p}

scala> p("hello")
hello
```

## Hiding a Class During the Import Process

```scala
package foo {
  class Bar { override def toString = "Bar" }
  class Buz { override def toString = "Buz" }
}

package test {
  import foo.{ Buz => _, _ }

  object test {
    var bar = new Bar                             //> bar  : foo.Bar = Bar
		var buz = new Buz					                    // won't compile
  }
}
```
## Using Static Imports

## Using Import Statements Anywhere
