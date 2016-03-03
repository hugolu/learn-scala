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

### Utility classes
```scala
import FooUtils._

object testSingleton {
	hiFoo()                                   //> Hello, Foo
	hiBar()                                   //> Hello, Bar
}

object FooUtils {
	def hiFoo() = println("Hello, Foo")
	def hiBar() = println("Hello, Bar")
}
```
- in Java you’d create a file utilities class by defining _static_ methods in a Jav `class`
- in Scala you do the same thing by putting all the methods in a Scala `object`

## Providing Default Values for Constructor Parameters

```scala
scala> class Foo(val x: Int = 123) { override def toString = s"Foo($x)" }
defined class Foo

scala> new Foo
res0: Foo = Foo(123)

scala> new Foo(111)
res1: Foo = Foo(111)

scala> new Foo(x=222)
res2: Foo = Foo(222)
```

## Overriding Default Accessors and Mutators

```scala
scala> class Foo(private var _x: Int) {
     |   def x = _x
     |   def x_$eq(x: Int) = { _x = x }
     | }
defined class Foo

scala> val foo = new Foo(1)
foo: Foo = Foo@2395a0d9

scala> foo.x
res1: Int = 1

scala> foo.x = 2
foo.x: Int = 2

scala> foo.x
res2: Int = 2
```
- change the name of the field you use in the class constructor so it won’t collide with the name of the getter method you want to use
	1. Create a private var constructor parameter with a name you want to reference from within your class.
	2. Define getter and setter names that you want other classes to use. 
	3. Modify the body of the getter and setter methods as desired.

```shell
$ javap Foo
Compiled from "Foo.scala"
public class Foo {
  public int x();
  public void x_$eq(int);
  public Foo(int);
}
```

```vim
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Foo.scala


public class Foo
{

    private int _x()
    {
        return _x;
    }

    private void _x_$eq(int x$1)
    {
        _x = x$1;
    }

    public int x()
    {
        return _x();
    }

    public void x_$eq(int x)
    {
        _x_$eq(x);
    }

    public Foo(int _x)
    {
        this._x = _x;
        super();
    }

    private int _x;
}
```

## Preventing Getter and Setter Methods from Being Generated
When you define a class field as a var, Scala automatically generates getter and setter methods for the field, and defining a field as a val automatically generates a getter method, but you don’t want either a getter or setter.

### private fields
```scala
scala> class Woman(private val age: Int) {
     |   def isOrderThan(that: Woman): Boolean = this.age > that.age
     | }
defined class Woman

scala> val w1 = new Woman(30)
w1: Woman = Woman@4b9d09e2

scala> val w2 = new Woman(40)
w2: Woman = Woman@5019b943

scala> w1 isOrderThan w2
res3: Boolean = false

scala> w1.age
<console>:13: error: value age in class Woman cannot be accessed in Woman
       w1.age
          ^
```
- Defining a field as private limits the field so it’s only available to instances of the same class.

### object-private fields
```scala
scala> class Woman(private[this] val age: Int) {
     |   def isOrderThan(that: Woman): Int = this.age > that.age
     | }
<console>:12: error: value age is not a member of Woman
         def isOrderThan(that: Woman): Int = this.age > that.age
                                                             ^
```
- Defining a field as `private[this]` takes this privacy a step further, and makes the field object-private, which means that it can only be accessed from the object that contains it.

## Assigning a Field to a Block or Function

```scala
scala> class Foo {
     |   val num = { println("do something..."); 123 }
     | }
defined class Foo

scala> var foo = new Foo
do something...
foo: Foo = Foo@3368838

scala> foo.num
res7: Int = 123
```
- Because the assignment of the code block to the text field and the println statement are both in the body of the Foo class, they are in the class’s constructor, and will be executed when a new instance of the class is created. 

```scala
scala> class Bar {
     |   lazy val num = { println("do something..."); 123 }
     | }
defined class Bar

scala> val bar = new Bar
bar: Bar = Bar@136422b8

scala> bar.num
do something...
res8: Int = 123
```
- When this code is compiled and run, there is no output, because the text field isn’t initialized until it’s accessed. That’s how a __lazy__ field works.

## Setting Uninitialized var Field Types

```scala
class Foo {
  var str = None: Option[String]
}

val foo = new Foo                               //> foo  : myTest.test52.Foo = myTest.test52$$anonfun$main$1$Foo$1@684d0ca0
foo.str                                         //> res0: Option[String] = None
foo.str.getOrElse("<empty>")                    //> res1: String = <empty>

foo.str = Some("hello world")
foo.str                                         //> res2: Option[String] = Some(hello world)
foo.str.getOrElse("<empty>")                    //> res3: String = hello world
```

### None is harmless
```scala
scala> val none: Option[List[Int]] = None
none: Option[List[Int]] = None

scala> none.foreach(println)

scala> val some: Option[List[Int]] = Some(List(1, 2, 3))
some: Option[List[Int]] = Some(List(1, 2, 3))

scala> some.foreach(println)
List(1, 2, 3)
```
- If the value hasn’t been assigned, it is a `None`, and calling foreach on it does no harm, the loop is just skipped over.
- If the value is assigned, it will be a `Some[List[Int]]`, so the foreach loop will be entered and the data printed.

## Handling Constructor Parameters When Extending a Class
```scala
class Base(var n: Int)
class Sub(n: Int) extends Base(n)
```
- Because you don’t declare the parameters in Sub as `var`, Scala won’t attempt to generate methods for those fields.

```shell
$ javap Base
Compiled from "Base.scala"
public class Base {
  public int n();
  public void n_$eq(int);
  public Base(int);
}

$ javap Sub
Compiled from "Sub.scala"
public class Sub extends Base {
  public Sub(int);
}
```
- The Sub class inherits that behavior from `Base`.
	- accessor: `public int n()`
  - modifier: `public void n_$eq(int)`

## Calling a Superclass Constructor
```scala
class Foo(var x: Int, var y: Int) {
  override def toString = s"Foo($x,$y)"
  def this() = this(1, 2)
  def this(x: Int) = this(x, 2)
}

class Bar0(var z: Int) extends Foo() {
  override def toString = s"Bar0($x,$y,$z)"
}
new Bar0(3)                                     //> res0: myTest.test55.Bar0 = Bar0(1,2,3)

class Bar1(x: Int, var z: Int) extends Foo(x) {
  override def toString = s"Bar1($x,$y,$z)"
}
new Bar1(1, 3)                                  //> res1: myTest.test55.Bar1 = Bar1(1,2,3)

class Bar2(x: Int, y: Int, var z: Int) extends Foo(x, y) {
  override def toString = s"Bar2($x,$y,$z)"
}
new Bar2(1, 2, 3)                               //> res2: myTest.test55.Bar2 = Bar2(1,2,3)
```
- the first line of an auxiliary constructor must be a call to another constructor of the current class
- there is no way for auxiliary con‐ structors to call a superclass constructor
- Can `Bar1`, `Bar2`, `Bar3` become a single `Bar`??

## When to Use an Abstract Class
- You want to create a base class that requires constructor arguments.
- The code will be called from Java code.
- Be aware that a class can extend only one abstract class.

```scala
abstract class Shape {
  val name: String 	// abstract field
  def area: Int 		// abstract method
  override def toString = s"$name($area)"
}

class Square(val l: Int) extends Shape {
  val name = "Square"
  val area = l * l
}

class Rectangle(val l: Int, val w: Int) extends Shape {
  val name = "Rectangle"
  val area = l * w
}

val x = new Square(3)                           //> x  : myTest.test56.Square = Square(9)
val y = new Rectangle(2, 4)                     //> y  : myTest.test56.Rectangle = Rectangle(8)
```

## Defining Properties in an Abstract Base Class (or Trait)
You can declare both val and var fields in an abstract class (or trait), and those fields can be abstract or have concrete implementations. 

```scala
abstract class Foo {
  val str: String
  val num = 123
}

class FooSub extends Foo {
  val str = "FooSub"
}

trait Bar {
  val str: String
  val num = 123
}

class BarSub extends Bar {
  val str = "BarSub"
}

new FooSub                                      //> res0: myTest.test57.FooSub = myTest.test57$$anonfun$main$1$FooSub$1@4586793e
new BarSub                                      //> res1: myTest.test57.BarSub = myTest.test57$$anonfun$main$1$BarSub$1@2d583afc
```
- the fields don’t actually exist in the abstract base class (or trait), the override keyword is not necessary

When you define an abstract field in an abstract class or trait, the Scala compiler does not create a field in the resulting code; it only generates the methods that correspond to the val or var field.

```scala
$ cat Foo.jad
public abstract class Foo
{
    public abstract String str();

    public int num()
    {
        return num;
    }

    public Foo()
    {
    }

    private final int num = 123;
}

$ cat FooSub.jad
public class FooSub extends Foo
{
    public String str()
    {
        return str;
    }

    public FooSub()
    {
    }

    private final String str = "FooSub";
}
```
- scala `abstract class` 使用 java `abstract class` 實作: 只針對已有的欄位產生存取方法

```scala
$ cat Bar.jad
public interface Bar
{
    public abstract void Bar$_setter_$num_$eq(int i);

    public abstract String str();

    public abstract int num();
}

$ cat BarSub.jad
public class BarSub
    implements Bar
{
    public int num()
    {
        return num;
    }

    public void Bar$_setter_$num_$eq(int x$1)
    {
        num = x$1;
    }

    public String str()
    {
        return str;
    }

    public BarSub()
    {
        Bar.class.$init$(this);
    }

    private final String str = "BarSub";
    private final int num;
}
```
- scala `trait` 使用 java `interface` 實作: 不存放欄位，所有方法都是 `abstract`

## Generating Boilerplate Code with Case Classes
Defining a class as a case class results in a lot of boilerplate code being generated, with the following benefits:
- An `apply` method is generated, so you don’t need to use the new keyword to create a new instance of the class.
- `Accessor` methods are generated for the constructor parameters because case class constructor parameters are val by default. `Mutator` methods are also generated for parameters declared as var.
- A good, default `toStrin`g method is generated.
- An `unapply` method is generated, making it easy to use case classes in match ex‐
pressions.
- `equals` and `hashCode` methods are generated.
- A `copy` method is generated.

```scala
case class Foo(var n: Int)

var foo = Foo.apply(1)                          //> foo  : myTest.test57.Foo = Foo(1)
foo.n                                           //> res0: Int = 1
foo.n = 2

println(foo)                                    //> Foo(2)
foo match {
  case Foo(n) => println(s"n=$n")
}                                               //> n=2

var foo2 = Foo(2)                               //> foo2  : myTest.test57.Foo = Foo(2)
foo.equals(foo)                                 //> res1: Boolean = true
foo.equals(foo2)                                //> res2: Boolean = true

foo.hashCode                                    //> res3: Int = 369114374
foo2.hashCode                                   //> res4: Int = 369114374

var foo3 = foo.copy(2)                          //> foo3  : myTest.test57.Foo = Foo(2)
```

```shell
$ javap Foo
Compiled from "Foo.scala"
public class Foo implements scala.Product,scala.Serializable {
  public static scala.Option<java.lang.Object> unapply(Foo);
  public static Foo apply(int);
  public static <A extends java/lang/Object> scala.Function1<java.lang.Object, A> andThen(scala.Function1<Foo, A>);
  public static <A extends java/lang/Object> scala.Function1<A, Foo> compose(scala.Function1<A, java.lang.Object>);
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

## Defining an equals Method (Object Equality)

## Creating Inner Classes
