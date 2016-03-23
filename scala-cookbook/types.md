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
## Create a Collection Whose Elements Are All of Some Base Type
## Selectively Adding New Behavior to a Closed Model
## Building Functionality with Types
