# Types

Scala’s type system uses a collection of symbols to express different generic type concepts, including variance, bounds, and constraints.

### Variance
*Type variance* is a generic type concept, and defines the rules by which parameterized types can be passed into methods.

| Symbol | Name | Description |
|--------|------|-------------|
| Array[T] | Invariant | Used when elements in the container are mutable. |
| Seq[+T] | Covariant | Used when elements in the container are immutable. This makes the container more flexible. | 
| Foo[-T] | Contravariant | Contravariance is essentially the opposite of covariance, and is rarely used. See Scala’s Function1 trait for an example of how it is used. |

```scala
class A
class B extends A
class C extends B

class InvariantClass[T]
class CovariantClass[+T]
class ContravariantClass[-T]

var x: InvariantClass[B] = _
x = new InvariantClass[A] 			// won't compile
x = new InvariantClass[B]
x = new InvariantClass[C] 			// won't compile

var y: CovariantClass[B] = _
y = new CovariantClass[A] 			// won't compile
y = new CovariantClass[B]
y = new CovariantClass[C]

var z: ContravariantClass[B]
z = new ContravariantClass[A]
z = new ContravariantClass[B]
z = new ContravariantClass[C] 	// won't compile
```

### Bounds
Bounds let you place restrictions on type parameters.

|   | Name | Descript |
|---|------|----------|
| `A <: B` | Upper bound | A must be a subtype of B. |
| `A >: B` | Lower bound | A must be a supertype of B. |
| `A <: Upper >: Lower` | Lower and upper bounds used together | The type A has both an upper and lower bound. |

### Type Constraints
Scala lets you specify additional type constraints. 

- `A =:= B` // A must be equal to B
- `A <:< B` // A must be a subtype of B
- `A <%< B` // A must be viewable as B

## Creating Classes That Use Generic Types

Standard symbols for generic type parameters
| Symbol | Description |
|--------|-------------|
| `A` | Refers to a simple type, such as `List[A]`. |
| `B`, `C`, `D` | Used for the 2nd, 3rd, 4th types, etc. |
| `K` | Typically refers to a key in a Java map. Scala collections use `A` in this situation. |
| `N` | Refers to a numeric value. | 
| `V` | Typically refers to a value in a Java map. Scala collections use `B` in this situation. |

```scala
class Stack[A] {
  private class Node[A](val elem: A) {
    var next: Node[A] = _
    override def toString = elem.toString
  }

  private var head: Node[A] = _

  def push(elem: A) {
    val n = new Node(elem)
    n.next = head
    head = n
  }

  def pop(): A = {
    val n = head
    head = n.next
    n.elem
  }

  private def printNodes(n: Node[A]) {
    if (n != null) {
      println(n)
      printNodes(n.next)
    }
  }

  def printAll() { printNodes(head) }
}

val nums = new Stack[Int]()                     //> nums  : myTest.Stack[Int] = myTest.Stack@420b8aff
nums.push(1)
nums.push(2)
nums.push(3)
nums.printAll                                   //> 3
                                                //| 2
                                                //| 1
nums.pop()                                      //> res0: Int = 3
nums.pop()                                      //> res1: Int = 2
nums.printAll                                   //> 1

val fruits = new Stack[String]()                //> fruits  : myTest.Stack[String] = myTest.Stack@afe676b
fruits.push("apple")
fruits.push("banana")
fruits.push("cherry")
fruits.printAll                                 //> cherry
                                                //| banana
                                                //| apple
fruits.pop()                                    //> res2: String = cherry
fruits.pop()                                    //> res3: String = banana
fruits.printAll                                 //> apple
```

## Creating a Method That Takes a Simple Generic Type

```scala
  def randomElement[A](seq: Seq[A]) = {
    val n = util.Random.nextInt(seq.length)
    seq(n)
  }                                               //> randomElement: [A](seq: Seq[A])A

  val nums = Seq(1, 2, 3, 4, 5)                   //> nums  : Seq[Int] = List(1, 2, 3, 4, 5)
  randomElement(nums)                             //> res0: Int = 5

  val names = Seq("Aleka", "Christina", "Tyler", "Molly")
                                                  //> names  : Seq[String] = List(Aleka, Christina, Tyler, Molly)
  randomElement(names)                            //> res1: String = Christina
```
- As with Scala classes, specify the generic type parameters in brackets, like `[A]`.

## Using Duck Typing (Structural Types)
You’re used to “Duck Typing” (structural types) from another language like Python or Ruby, and want to use this feature in your Scala code.
Scala’s version of “Duck Typing” is known as using a structural type. 

```scala
class Dog { def speak() = println("woof") }
class Cat { def speak() = println("meow") }

def callSpeak[A <: { def speak(): Unit }](animal: A) = animal.speak()
                                                //> callSpeak: [A <: AnyRef{def speak(): Unit}](animal: A)Unit

callSpeak(new Dog)                              //> woof
callSpeak(new Cat)                              //> meow
```

## Make Mutable Collections Invariant

When creating a collection of elements that can be changed (mutated), its generic type parameter should be declared as `[A]`, making it *invariant*.

Declaring a type as invariant has several effects. First, the container can hold both the specified types as well as its subtypes. 

```scala
import scala.collection.mutable.ArrayBuffer

class A { override def toString = "A" }
class B extends A { override def toString = "B" }
class C extends B { override def toString = "C" }

val bs = ArrayBuffer[B]()                       //> bs  : scala.collection.mutable.ArrayBuffer[myTest.test97.B] = ArrayBuffer()
bs += new B                                     //> res0: myTest.test97.bs.type = ArrayBuffer(B)
bs += new C                                     //> res1: myTest.test97.bs.type = ArrayBuffer(B, C)
bs.foreach(println)                             //> B
                                                //| C
```

The second effect of declaring an invariant type is the primary purpose of this recipe. A method only accepts `ArrayBuffer[B]`, but not its subtype.

```scala
def show(array: ArrayBuffer[B]) {
  array.foreach(println)
}                                               //> show: (array: scala.collection.mutable.ArrayBuffer[myTest.test97.B])Unit

val bs = ArrayBuffer[B]()                       //> bs  : scala.collection.mutable.ArrayBuffer[myTest.test97.B] = ArrayBuffer()
bs += new B                                     //> res0: myTest.test97.bs.type = ArrayBuffer(B)
bs += new C                                     //> res1: myTest.test97.bs.type = ArrayBuffer(B, C)
show(bs)                                        //> B
                                                //| C

val cs = ArrayBuffer[C]()                       //> cs  : scala.collection.mutable.ArrayBuffer[myTest.test97.C] = ArrayBuffer()
cs += new C                                     //> res2: myTest.test97.cs.type = ArrayBuffer(C)
cs += new C                                     //> res3: myTest.test97.cs.type = ArrayBuffer(C)
show(cs)                                        // won't compile
```

- Elements in an `ArrayBuffer` can be mutated.
- `show` is defined to accept a parameter of type `ArrayBuffer[B]`.
- You’re attempting to pass in cs, whose type is `ArrayBuffer[C]`.
- If the compiler allowed this, `show` could replace `C` elements in `cs` with plain old `B` elements. This can’t be allowed.

### Mutable Collections
The elements of the `Array`, `ArrayBuffer`, and `ListBuffer` classes can be **mutated**, and they’re all defined with *invariant* type parameters:
```scala
class Array[T]
class ArrayBuffer[A]
class ListBuffer[A]
```

### Immutable Collecitons
Conversely, collections classes that are **immutable** identify their generic type parameters differently, with the `+` symbol, as shown here:
```scala
class List[+T]
class Vector[+A]
trait Seq[+A]
```

The `+` symbol used on the type parameters of the **immutable** collections defines their parameters to be *covariant*. Because their elements can’t be mutated, adding this symbol makes them more flexible. 

## Make Immutable Collections Covariant
You *can* define a collection of **immutable** elements as invariant, but your collection will be much more flexible if you declare that your type parameter is **covariant**. To make a type parameter covariant, declare it with the `+` symbol, like `[+A]`.

```scala
class A { override def toString = "A" }
class B extends A { override def toString = "B" }
class C extends B { override def toString = "C" }

case class Container[+T](val elem: T)

def showContent(c: Container[B]) = println(c.elem)
                                                //> showContent: (c: myTest.test98.Container[myTest.test98.B])Unit

showContent(Container(new A))										// won't compile
showContent(Container(new B))                   //> B
showContent(Container(new C))                   //> C
```

## Create a Collection Whose Elements Are All of Some Base Type
You want to specify that a class or method takes a type parameter, and that parameter is limited so it can only be a base type, or a subtype of that base type. Define the class or method by specifying the type parameter with an `upper bound`.

```scala
class A { override def toString = "A" }
class B extends A { override def toString = "B" }
class C extends B { override def toString = "C" }

val a = new A                                   //> a  : test.A = A
val b = new B                                   //> b  : test.B = B
val c = new C                                   //> c  : test.C = C

class Container[T <: B](val elem: T)
val ca = new Container(a)                       // wont' compile
val cb = new Container(b)                       //> cb  : test.Container[test.B] = test$$anonfun$main$1$Container$1@2d583afc
val cc = new Container(c)                       //> cc  : test.Container[test.C] = test$$anonfun$main$1$Container$1@7b888da5

def show[T <: B](obj: T) = println(obj)         //> show: [T <: test.B](obj: T)Unit
show(a)                                         // won't compile
show(b)                                         //> B
show(c)                                         //> C
```
- `Container[T <: B]` is a class taking a type parameter with an `upper bound` of `B`
- `show[T <: B]` is a method taking a type parameter with an `upper bound` of `B`

## Selectively Adding New Behavior to a Closed Model
```scala
 def add[A](x: A, y: A)(implicit numeric: Numeric[A]): A = numeric.plus(x, y)
                                                //> add: [A](x: A, y: A)(implicit numeric: Numeric[A])A
add(1, 2)                                       //> res0: Int = 3
add(1.0, 2.0)                                   //> res1: Double = 3.0
add(1L, 2L)                                     //> res2: Long = 3
```

The process of creating a type class:
- Usually you start with a need,such as having a closed model to which you want to add new behavior.
- To add the new behavior, you define a *type class*. The typical approach is to create a base trait, and then write specific implementations of that trait using implicit objects.
- Back in your main application, create a method that uses the type class to apply the behavior to the closed model.

### Type Class
```scala
final class Dog(val name: String)
final class Cat(val name: String)

trait Speakable[A] {
  def speak(speaker: A): Unit
}

implicit object SpeakingDog extends Speakable[Dog] {
  def speak(dog: Dog) { println(s"Woof! I'm a Dog, my name is ${dog.name}") }
}

def makeItSpeak(dog: Dog)(implicit speakable: Speakable[Dog]) {
  speakable.speak(dog)
}                                               //> makeItSpeak: (dog: myTest.test02.Dog)(implicit speakable: myTest.test02.Speakable[myTest.test02.Dog])Unit

val dog = new Dog("Puppy")                      //> dog  : myTest.test02.Dog = myTest.test02$$anonfun$main$1$Dog$1@6ba00355
makeItSpeak(dog)                                //> Woof! I'm a Dog, my name is Puppy

val cat = new Cat("Kitty")                      //> cat  : myTest.test02.Cat = myTest.test02$$anonfun$main$1$Cat$1@31730d7b
makeItSpeak(cat)                                // won't compile
```

### Implicit Conversion
```scala
final class Dog(val name: String)
final class Cat(val name: String)

implicit class DogSpeaking(val dog: Dog) {
  def speak = println(s"Woof! I'm a Dog, my name is ${dog.name}")
}

def makeItSpeak(dog: Dog) {
  dog.speak
}                                               //> makeItSpeak: (dog: myTest.test04.Dog)Unit

val dog = new Dog("Puppy")                      //> dog  : myTest.test04.Dog = myTest.test04$$anonfun$main$1$Dog$1@7f81f91a
makeItSpeak(dog)                                //> Woof! I'm a Dog, my name is Puppy

val cat = new Cat("Kitty")                      //> cat  : myTest.test04.Cat = myTest.test04$$anonfun$main$1$Cat$1@53c9f789
//makeItSpeak(cat)
```

### My Try
```scala
class Animal(val name: String)
final class Dog(name: String) extends Animal(name)
final class Cat(name: String) extends Animal(name)

val dog = new Dog("Puppy")                      //> dog  : myTest.test05.Dog = myTest.test05$$anonfun$main$1$Dog$1@693b004c
val cat = new Cat("Kitty")                      //> cat  : myTest.test05.Cat = myTest.test05$$anonfun$main$1$Cat$1@2090b38d

// type class
trait Speakable[A] {
	def speak(speaker: A): Unit
}
implicit object SpeakingAnimal extends Speakable[Animal] {
	def speak(animal: Animal) = println(s"Hello, my name is ${animal.name}")
}

def makeItSpeak(animal: Animal)(implicit speakable: Speakable[Animal]) {
	speakable.speak(animal)
}                                               //> makeItSpeak: (animal: myTest.test05.Animal)(implicit speakable: myTest.test05.Speakable[myTest.test05.Animal])Unit

makeItSpeak(dog)                                //> Hello, my name is Puppy
makeItSpeak(cat)                                //> Hello, my name is Kitty

// implicit conversion
implicit class AnimalSpeaking(animal: Animal) {
	def speak = println(s"Hello, my name is ${animal.name}")
}

dog.speak                                       //> Hello, my name is Puppy
cat.speak                                       //> Hello, my name is Kitty
```
- Using *implicit conversion* is more convenient than *type class*, isn't it?

## Building Functionality with Types
