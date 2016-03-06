# Traits

## Using a Trait as an Interface
```scala
trait X {
	def doX
}
trait Y {
	def doY = println("doY")
}
trait Z {
	def doZ(z: String) = println(s"doZ: $z")
}

class Foo extends X with Y with Z {
	def doX = println("doX")
}

abstract class Bar extends X with Y {}

val f =new Foo                                  //> f  : myTest.test69.Foo = myTest.test69$$anonfun$main$1$Foo$1@2faa819
f.doX                                           //> doX
f.doY                                           //> doY
f.doZ("hello")                                  //> doZ: hello
```
- When extending a class and one or more traits, use extends for the class, and with for subsequent traits
- If a class extends a trait but does not implement the abstract methods defined in that trait, it must be declared abstract.

```scala
abstract class Human {
  val name: String
}

trait Eatable {
  def eat = println("eating")
}
trait Sleepable {
  def sleep = println("sleeping")
}

class Student(_name: String) extends Human with Eatable with Sleepable {
  val name = _name
}

val s = new Student("Hugo")                     //> s  : myTest.test70.Student = myTest.test70$$anonfun$main$1$Student$1@7b888da
                                                //| 5
s.eat                                           //> eating
s.sleep                                         //> sleeping
```
-  If a class extends a class (or abstract class) and a trait, always use extends before the class name, and use with before the trait name(s).

```scala
class Foo {
	def foo = println("foo...")
}

trait Bar {
	def bar = println("bar...")
}

val f = new Foo with Bar                        //> f  : myTest.test71.Foo with myTest.test71.Bar = myTest.test71$$anonfun$main$
                                                //| 1$$anon$1@62811340
f.foo                                           //> foo...
f.bar                                           //> bar...
```
- When a class has multiple traits, those traits are said to be mixed in to the class.
- The term “mixed in” is also used when extending *a single object instance* with a trait.

## Using Abstract and Concrete Fields in Traits

```scala
trait Foo {
  def doX = "X" //concrete method
  def doY: String //abstract method
}

class Bar extends Foo {
  def doY = "Y"
}

var b = new Bar                                 //> b  : myTest.test72.Bar = myTest.test72$$anonfun$main$1$Bar$1@2e095b5c
b.doX                                           //> res0: String = X
b.doY                                           //> res1: String = Y
```
- Define a field with an initial value to make it concrete;
- otherwise, don’t assign it an initial value to make it abstract. 

## Using a Trait Like an Abstract Class
```scala
trait Foo {
	def doX = println("Foo's x") // concrete
	def doY // abstract
}

class Bar extends Foo {
	def doY = println("Bar's Y")
}

class Qiz extends Foo {
	override def doX = println("Qiz's X")
	def doY = println("Qiz's Y")
}

var b = new Bar                                 //> b  : myTest.test72.Bar = myTest.test72$$anonfun$main$1$Bar$1@7f81f91a
b.doX                                           //> Foo's x
b.doY                                           //> Bar's Y

var q = new Qiz                                 //> q  : myTest.test72.Qiz = myTest.test72$$anonfun$main$1$Qiz$1@40dd550c
q.doX                                           //> Qiz's X
q.doY                                           //> Qiz's Y
```
- In the class that extends the trait, you can override those methods or use them as they are defined in the trait.

## Using Traits as Simple Mixins

```scala
abstract class Foo {
  def doXY
}

trait Bar {
  def doX = println("doing x")
  def doY = println("doing y")
}

class Qiz extends Foo with Bar {
  def doXY = {
    doX
    doY
  }
}

var q = new Qiz                                 //> q  : myTest.test72.Qiz = myTest.test72$$anonfun$main$1$Qiz$1@1a7811df
q.doXY                                          //> doing x
                                                //| doing y
```
- To implement a simple mixin, define the methods you want in your trait, then add the trait to your class using extends or with.

## Limiting Which Classes Can Use a Trait by Inheritance

Use the following syntax to declare a trait named TraitName, where TraitName can only be mixed into classes that extend a type named SuperThing, where SuperThing may be a trait, class, or abstract class.
```scala
trait [TraitName] extends [SuperThing]
```

```scala
class Foo
trait Bar extends Foo
class Qiz extends Foo with Bar
```

## Marking Traits So They Can Only Be Used by Subclasses of a Certain Type

## Ensuring a Trait Can Only Be Added to a Type That Has a Specific Method

## Adding a Trait to an Object Instance

## Extending a Java Interface Like a Trait
