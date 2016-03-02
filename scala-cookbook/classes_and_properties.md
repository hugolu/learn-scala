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

## Defining Auxiliary Constructors

## Defining a Private Primary Constructor

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
