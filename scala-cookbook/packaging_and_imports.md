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
- The second _ character inside the curly braces is the same as stating that you want to import everything else in the package, like `import java.util._`

## Using Static Imports

You want to import members in a way similar to the Java static import approach, so you can refer to the member names directly, without having to prefix them with their class name.

```scala
scala> PI
<console>:11: error: not found: value PI
       PI
       ^

scala> java.lang.Math.PI
res1: Double = 3.141592653589793

scala> import java.lang.Math._
import java.lang.Math._

scala> PI
res2: Double = 3.141592653589793
```

## Using Import Statements Anywhere
You can place an import statement almost anywhere inside a program. As with Java, you can import members at the top of a class definition, and then use the imported resource later in your code.

You can even place an import statement inside a block, limiting the scope of the import to only the code that follows the statement, inside that block.

```scala
def printRandom {
	{
  	import scala.util.Random
  	val r1 = new Random // this is fine
	}
  val r2 = new Random // error: not found: type Random
}
 ```
 
Although placing import statements at the top of a file or just before they’re used can be a matter of style, I find this flexibility to be useful when placing multiple classes or packages in one file. In these cases, **it’s nice to keep the imports in a small scope to limit namespace issues**, and also to make the code easier to refactor as it grows.
