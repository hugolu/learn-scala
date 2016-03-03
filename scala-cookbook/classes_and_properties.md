# Classes and Properties

## Creating a Primary Constructor

### Foo.scala
```scala
class Foo(val x: Int) {
  println("start of Foo")

  // some class fields
  var y: Int = _

  // some methods
  override def toString = s"Foo($x,$y)"
  def printFoo { println(this) }

  printFoo
  println("end of Foo")
}
```

### compile Foo.scala
```shell
$ scalac Foo.scala
$ ls Foo.*
Foo.class Foo.scala
$
$ javap Foo
Compiled from "Foo.scala"
public class Foo {
  public int x();
  public int y();
  public void y_$eq(int);
  public java.lang.String toString();
  public void printFoo();
  public Foo(int);
}
```
- Because the fields are mutable, Scala generates both accessor and mutator methods for them.
- `val x` has an accessor `public int x();`
- `var y` has an accessor `public int y();` and a mutator `public void y_$eq(int);`

### install JAD
ref: [JAD Java Decompiler](http://varaneckas.com/jad/)

```shell
$ wget http://varaneckas.com/jad/jad158g.mac.intel.zip
$ unzip jad158g.mac.intel.zip
$ sudo mv jad /usr/local/bin/
$ sudo mv jad.1 /usr/local/share/man/man1
```

### decompie Foo.class
```shell
$ jad Foo
Parsing Foo... Generating Foo.jad
$ cat Foo.jad
...
    public Foo(int x)
    {
        this.x = x;
        super();
        Predef$.MODULE$.println("start of Foo");
        printFoo();
        Predef$.MODULE$.println("end of Foo");
    }

    private final int x;
    private int y;
}
```
- Any method that’s called in the body of the class is really being called from the primary constructor.
  - `Predef$.MODULE$.println("start of Foo");`
  - `printFoo();`
  - `Predef$.MODULE$.println("end of Foo");`

### The primary constructor of a Scala class
- The constructor parameters
- Methods that are called in the body of the class
- Statements and expressions that are executed in the body of the class

```scala
scala> :load ./Foo.scala
Loading ./Foo.scala...
defined class Foo

scala> var f = new Foo(1)
start of Foo
Foo(1,0)
end of Foo
f: Foo = Foo(1,0)

scala> f.x
res1: Int = 1

scala> f.y
res2: Int = 0

scala> f.y = 2
f.y: Int = 2

scala> f
res3: Foo = Foo(1,2)
```

## Controlling the Visibility of Constructor Fields

| Visibility | Accessor? | Mutator? |
|------------|-----------|----------|
| `var` | Yes  |Yes |
| `val` | Yes | No |
| Default visibility (no `var` or `val`) | No | No |
| Adding the `private` keyword to `var` or `val` | No | No |

### var fields
```scala
class Person(var name: String)
```
```shell
$ scala Person.scala
$ javap Person
Compiled from "Person.scala"
public class Person {
  public java.lang.String name();
  public void name_$eq(java.lang.String);
  public Person(java.lang.String);
}
```
- If a field is declared as a var, Scala generates both getter and setter methods for that field.
- getter: `public java.lang.String name()`
- setter: `public void name_$eq(java.lang.String)`

### val fields
```scala
class Person(val name: String)
```
```shell
$ scalac Person.scala
$ javap Person
Compiled from "Person.scala"
public class Person {
  public java.lang.String name();
  public Person(java.lang.String);
}
```
- If the field is a val, Scala generates only a getter method for it.
- getter: `public java.lang.String name()`

### Fields without val or var
```scala
class Person(name: String)
```
```shell
$ scalac Person.scala
$ javap Person
Compiled from "Person.scala"
public class Person {
  public Person(java.lang.String);
}
```
-  If a field doesn’t have a var or val modifier, Scala gets conservative, and doesn’t generate a getter or setter method for the field.

### Adding private to val or var
```scala
class Person(private var name: String) { def getName {println(name)} }
```
```shell
$ scalac Person.scala
$ javap Person
Compiled from "Person.scala"
public class Person {
  public void getName();
  public Person(java.lang.String);
}
```
- Additionally, var and val fields can be modified with the private keyword, which prevents getters and setters from being generated.

## Defining Auxiliary Constructors

```scala
  class Foo(val num: Int, val str: String) {
    def this(num: Int) {
      this(num, Foo.DEFAULT_STRING)
    }
    def this(str: String) {
      this(Foo.DEFAULT_NUMBER, str)
    }
    def this() {
      this(Foo.DEFAULT_NUMBER, Foo.DEFAULT_STRING)
    }
    override def toString = "Foo(" + num + "," + str + ")"
  }

  object Foo {
    val DEFAULT_NUMBER = 123
    val DEFAULT_STRING = "xyz"
  }

  new Foo(111, "hello")                           //> res0: myTest.test45.Foo = Foo(111,hello)
  new Foo(111)                                    //> res1: myTest.test45.Foo = Foo(111,xyz)
  new Foo("hello")                                //> res2: myTest.test45.Foo = Foo(123,hello)
  new Foo                                         //> res3: myTest.test45.Foo = Foo(123,xyz)
```

Rules
- Auxiliary constructors are defined by creating methods named `this`.
- Each auxiliary constructor must begin with a call to a previously defined constructor. (呼叫其它constructor之前不能做其他事情)
- Each constructor must have a different signature.
- One constructor calls another constructor with the name `this`.

### Generating auxiliary constructors for case classes 
```scala
scala> object testFoo {
     |   case class Foo(val num: Int, val str: String)
     |
     |   object Foo {
     |     def apply() = new Foo(123, "xyz")
     |     def apply(num: Int) = new Foo(num, "xyz")
     |     def apply(str: String) = new Foo(123, str)
     |   }
     | }
defined object testFoo

scala> import testFoo._
import testFoo._

scala> Foo()
res4: testFoo.Foo = Foo(123,xyz)

scala> Foo(111)
res5: testFoo.Foo = Foo(111,xyz)

scala> Foo("abc")
res6: testFoo.Foo = Foo(123,abc)

scala> Foo(111,"abc")
res7: testFoo.Foo = Foo(111,abc)
```
- to add apply methods to the companion object of the `Foo` case class

```scala
scala> case class Bar(val num: Int = 123, val str: String = "xyz")
defined class Bar

scala> Bar()
res8: Bar = Bar(123,xyz)

scala> Bar(num=111)
res9: Bar = Bar(111,xyz)

scala> Bar(str="abc")
res10: Bar = Bar(123,abc)

scala> Bar(111,"abc")
res11: Bar = Bar(111,abc)
```
- `Bar` case class parameters with default values

## Defining a Private Primary Constructor

```scala
scala> object testSingleton {
     |   class Foo private {
     |     override def toString = "I'm Foo"
     |   }
     |
     |   object Foo {
     |     val foo = new Foo
     |     def getInstance = foo
     |   }
     | }
defined object testSingleton

scala> import testSingleton._
import testSingleton._

scala> Foo.getInstance
res0: testSingleton.Foo = I'm Foo
```
- A simple way to enforce the Singleton pattern in Scala is to make the primary constructor private, then put a getInstance method in the companion object of the class.
- To make the primary constructor private, insert the private keyword in between the class name and any parameters the constructor accepts.

## Providing Default Values for Constructor Parameters

## Overriding Default Accessors and Mutators

## Preventing Getter and Setter Methods from Being Generated

## Assigning a Field to a Block or Function

## Setting Uninitialized var Field Types

## Handling Constructor Parameters When Extending a Class

## Calling a Superclass Constructor

## When to Use an Abstract Class

## Defining Properties in an Abstract Base Class (or Trait)

## Generating Boilerplate Code with Case Classes

## Defining an equals Method (Object Equality)

## Creating Inner Classes
