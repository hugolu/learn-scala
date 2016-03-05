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

## Renaming Members on Import

## Hiding a Class During the Import Process

## Using Static Imports

## Using Import Statements Anywhere
