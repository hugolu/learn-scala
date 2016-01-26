# Variances

Scala supports variance annotations of type parameters of generic classes.

In the page about generic classes an example for a mutable stack was given. We explained that the type defined by the class Stack[T] is subject to **invariant subtyping** regarding the type parameter. 

an advanced example which combines the use of polymorphic methods, lower type bounds, and covariant type parameter annotations in a non-trivial fashion.
```scala
class Stack[+A] {
  def push[B >: A](elem: B): Stack[B] = new Stack[B] {
    override def top: B = elem
    override def pop: Stack[B] = Stack.this
    override def toString() = elem.toString() + " " + Stack.this.toString()
  }
  def top: A = sys.error("no element on stack")
  def pop: Stack[A] = sys.error("no element on stack")
  override def toString() = ""
}

var s: Stack[Any] = new Stack().push("hello");
s = s.push(new Object())
s = s.push(7)
println(s)
//7 java.lang.Object@3340302c hello
```

- The annotation ```+T``` declares type ```T``` to be used only in covariant positions.
- For our example this means ```Stack[T]``` is a subtype of ```Stack[S]``` if ```T``` is a subtype of ```S```.

```scala
class Stack[+A] {
  def push[B >: A](elem: B): Stack[B] = new Stack[B] {
    override def top: B = elem
    override def pop: Stack[B] = Stack.this
    override def toString() = elem.toString() + " " + Stack.this.toString()
  }
  def top: A = sys.error("no element on stack")
  def pop: Stack[A] = sys.error("no element on stack")
  override def toString() = ""
}

class A {}
class B extends A {}
class C extends B {}

var s: Stack[B] = new Stack()

s = s.push(new B)
//s: Stack[B] = B@250f18b3
s = s.push(new C)
//s: Stack[B] = C@36e7508c B@250f18b3
s = s.push(new A)
//<console>:14: error: type mismatch;
```
- ```Stack[C]``` is a subtype of ```Stack[B]``` if ```C``` is a subtype of ```C```
- For the stack example we would have to use the covariant type parameter ```T``` in a contravariant position for being able to define method ```push```. 
