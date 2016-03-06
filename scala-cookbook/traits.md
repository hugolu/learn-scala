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

## Using a Trait Like an Abstract Class

## Using Traits as Simple Mixins

## Limiting Which Classes Can Use a Trait by Inheritance

## Marking Traits So They Can Only Be Used by Subclasses of a Certain Type

## Ensuring a Trait Can Only Be Added to a Type That Has a Specific Method

## Adding a Trait to an Object Instance

## Extending a Java Interface Like a Trait
