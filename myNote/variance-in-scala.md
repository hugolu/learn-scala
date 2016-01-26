以下內容翻譯文章 [Variance in scala](http://like-a-boss.net/2012/09/17/variance-in-scala.html)
- 專有名詞除第一次出現時給予中文翻譯，其餘文章皆沿用原文，避免歧義。

___
Variance ([wikipedia](http://en.wikipedia.org/wiki/Variance_%28computer_science%29), [scala-lang](http://www.scala-lang.org/node/129)) 乍看瑣碎，卻是保證型別安全的有用工具。我花了一點時間弄懂它隱含的意思，以下總結 variance 以及它能做什麼。

# Variance (可變)

在 Scala 中，類別的通用化參數可額外加註 variance 註記。那些註記進一步約束宣告的類別如何使用。

Variance 有點像 [Liskov Substitution Principle](http://en.wikipedia.org/wiki/Liskov_substitution_principle)。LSP 描述子類能夠替換其超類被使用。

Variance 施加額外限制讓我們從子型別 (sub-typing) 與超型別 (super-typing) 的角度使用通用類別。

## Covariance (協變)

An example of a covariant class in scala is the immutable Vector.

Consider this class hierarchy which we will use for the examples:

```scala
class Animal
class Dog extends Animal
```

Covariance defines the following relationship: if A is a subtype of B then Vector[A] is a subtype of Vector[B]. Concretely Vector[Dog] is a subtype of Vector[Animal].

Another way of looking at it is if a Vector is covariant we must be able to use Vector[Dog] in any place that we would use Vector[Animal] because Animal is of type Dog or wider. Covariance implies a conversion between a narrower type and a wider type - you may treat Vector[Dog] as if it’s Vector[Animal].

```scala
// we can assign a Vector of Animals to a Vector of Animals 
scala> val aCovariantVector: Vector[Animal] = Vector.empty[Animal]
aCovariantVector: Vector[Animal] = Vector()

// We can also asign a Vector of Dogs to a Vector of Animals
scala> val aCovariantVector: Vector[Animal] = Vector.empty[Dog]
aCovariantVector: Vector[Animal] = Vector()

// Because Animal is the wider type this isn't possible.
scala> val aCovariantVector: Vector[Dog] = Vector.empty[Animal]
<console>:9: error: type mismatch;
 found   : scala.collection.immutable.Vector[Animal]
 required: Vector[Dog]
       val aCovariantVector: Vector[Dog] = Vector.empty[Animal]
                                                       ^
```

## Invariance (不變)

If a class parameter is invariant it means that no conversion wider to narrower, nor narrower to wider may be performed on the class.

The most common usage of invariance are mutable collections. An Array is an example of an invariant class.

```scala
scala> val anInvariantArray: Array[Animal] = Array.empty[Animal]
anInvariantArray: Array[Animal] = Array()

scala> val anInvariantArray: Array[Animal] = Array.empty[Dog]
<console>:9: error: type mismatch;
 found   : Array[Dog]
 required: Array[Animal]
Note: Dog <: Animal, but class Array is invariant in type T.
You may wish to investigate a wildcard type such as `_ <: Animal`. (SLS 3.2.10)
       val anInvariantArray: Array[Animal] = Array.empty[Dog]
                                                       ^

scala> val anInvariantArray: Array[Dog] = Array.empty[Animal]
<console>:9: error: type mismatch;
 found   : Array[Animal]
 required: Array[Dog]
Note: Animal >: Dog, but class Array is invariant in type T.
You may wish to investigate a wildcard type such as `_ >: Dog`. (SLS 3.2.10)
       val anInvariantArray: Array[Dog] = Array.empty[Animal]
```

Invariance is important for type safety of mutable collections. Java, famously, has covariant mutable arrays. This should show you why it’s a bad idea:

```scala
// declare an array of strings
String[] a = new String[1];

// because in java arrays are covariant we should be able to
// use it as if it's an array of objects
Object[] b = a;

// but storing an Integer will rightfully cause an java.lang.ArrayStoreException
b[0] = 1;
```

Because of this users of arrays in can be fooled into thinking they are dealing with Object[] instead String[]. If java would allow storing Integers into a String array this would blow up at the read site - the “readers” of the array would think they are still dealing with a String array but and not a Object array and try to read strings from it.

## Contravariance (逆變)

Contravariance is in some way the polar opposite of covariance. The canonical example of a contrvariant class in scala is Function1[-T1, +R]. Why does Function1 need to be contrvariant on its the input parameter?

Let’s think in term of conversions. Covariance, which was discused before, implies that there exists a conversion between Vector[Dog] to Vector[Animal] because Dog is a subclass of Animal. Does a similar conversion make sense in the case of Function1?

If we have Function1[Dog, Any] then in the general case this function should work for Dogs and its subtypes. But not necessarily for animals because it may use “features” (methods) that are only available to the subtype.

The reverse conversion works however - if we have a function that works on Animals then this function by design should work on Dogs.

Contravariance means that if B is a supertype of A then Function1[A, R] is a supertype of Function1[B, R]. Concretely Function1[Dog, Any] is a supertype of Function1[Animal, Any].

```scala
class Contravariant[-A] 

// this all works as expected:
scala> val contravariantClass: Contravariant[Animal] = new Contravariant[Animal]
contravariantClass: Contravariant[Animal] = Contravariant@632b6836

scala> val contravariantClass: Contravariant[Dog] = new Contravariant[Animal]
contravariantClass: Contravariant[Dog] = Contravariant@6dce272d

// because Dog is a subtype of Animal not the other way around we get an error
scala> val contravariantClass: Contravariant[Animal] = new Contravariant[Dog]
<console>:10: error: type mismatch;
 found   : Contravariant[Dog]
 required: Contravariant[Animal]
       val contravariantClass: Contravariant[Animal] = new Contravariant[Dog]
                                                    ^
```

# Variance and type safety (可變與型別安全)

When defining a generic class with a var field we can get compile time errors:

```scala
scala> class Invariant[T](var t: T)
defined class Invariant

scala> class Covariant[+T](var t: T)
<console>:7: error: covariant type T occurs in contravariant position in type T of value t_=
       class Covariant[+T](var t: T)
             ^

scala> class Contravariant[-T](var t: T)
<console>:7: error: contravariant type T occurs in covariant position in type => T of method t
       class Contravariant[-T](var t: T)
```

Let’s break it down a little. Why doesn’t the compiler allow getters in the Covariant class?

```scala
scala> abstract trait Covariant[+T] {
     |   def take(t: T): Unit
     | }
<console>:8: error: covariant type T occurs in contravariant position in type T of value t
         def take(t: T): Unit
                  ^

scala> abstract trait Contravariant[-T] {
     |   def take(t: T): Unit
     | }
defined trait Contravariant
```

Why? Let’s think about usages of covariance let’s say that we have a class:

```scala
class Printer[+T] {
     |    def print(t: T): Unit = ???
     | }
<console>:8: error: covariant type T occurs in contravariant position in type T of value t
          def print(t: T): Unit = ???
```

If the print method can print Dogs does it make sense (in general) that it should also print Animals? Maybe sometimes but in the general sense if we want to generalize the Printer class we should use contravariance. The compiler is smart enough to check this type of usage for us.

Let’s think about the second use case: returning a generic parameter:

```scala
scala> class Create[-T] {
     |   def create: T = ???
     | }
<console>:8: error: contravariant type T occurs in covariant position in type => T of method create
         def create: T = ???
```

And again - does it make sense that Create should generalize by contravariance? If Create returns instances of the Animal class should we be able to use it in every place that expects Create[Dog]? The scala compiler is smart enough that it explodes in our face if we try it.
