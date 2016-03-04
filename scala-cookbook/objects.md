# Objects

## Object Casting
```scala
class Foo
class Bar extends Foo

val b1 = (new Bar).asInstanceOf[Foo]            //> b1  : myTest.test64.Foo = myTest.test64$$anonfun$main$1$Bar$1@2e7f4425
val b2: Foo = new Bar                           //> b2  : myTest.test64.Foo = myTest.test64$$anonfun$main$1$Bar$1@2552f0fc
```

```scala
val n1 = 1                                      //> n1  : Int = 1
val n2 = n1.asInstanceOf[Long]                  //> n2  : Long = 1
```

## The Scala Equivalent of Java’s .class
```scala
scala> val stringClass = classOf[String]
stringClass: Class[String] = class java.lang.String

scala> stringClass.getMethods
res2: Array[java.lang.reflect.Method] = Array(public boolean java.lang.String.equals(java.lang.Object), public java.lang.String java.lang.String.toString(), public int java.lang.String.hashCode(), public int java.lang.String.compareTo(java.lang.Object), public int java.lang.String.compareTo(java.lang.String), public int java.lang.String.indexOf(java.lang.String,int), public int java.lang.String.indexOf(java.lang.String), public int java.lang.String.indexOf(int), public int java.lang.String.indexOf(int,int), public static java.lang.String java.lang.String.valueOf(char), public static java.lang.String java.lang.String.valueOf(java.lang.Object), public static java.lang.String java.lang.String.valueOf(boolean), public static java.lang.String java.lang.String.valueOf(char[],int,int), public ...
```
- This approach also lets you begin with simple reflection techniques.
  - use `String.class` in java

## Determining the Class of an Object
```scala
scala> "hello".getClass
res3: Class[_ <: String] = class java.lang.String
```

## Launching an Application with an Object
```scala
// Hello.Scala

object Hello extends App {
  println("Hello, world")
}
```

```shell
$ scalac Hello.scala
$ scala Hello
Hello, world
```

### `App.args`
When using this approach, any command-line arguments to your application are im‐ plicitly available through an args object, which is inherited from the App trait.
```scala
object Hello1 extends App {
  println(s"Hello, ${args(0)}.")
}
```
```shell
$ scalac Hello1.scala
$ scala Hello1 kitty
Hello, kitty.
```

### `main` method
To launching an application is to manually implement a main method with the correct signature in an object, in a manner similar to Java.

```scala
object Hello2 {
  def main(args: Array[String]) {
    println(s"Hello, ${args(0)}.")
  }
}
```
```shell
$ scalac Hello2.scala
$ scala Hello2 puppy
Hello, puppy.
```

> Note that in both cases, Scala applications are launched from an object, not a class.

## Creating Singletons with object

## Creating Static Members with Companion Objects

## Putting Common Code in Package Objects

## Creating Object Instances Without Using the new Keyword

## Implement the Factory Method in Scala with apply

