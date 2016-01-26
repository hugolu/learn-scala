# Generic Classes

Like in Java 5 (aka. JDK 1.5), Scala has built-in support for **classes parameterized with types**. Such generic classes are particularly useful for the development of collection classes.
```scala
class Stack[T] {
  var elems: List[T] = Nil
  def push(x: T) { elems = x :: elems }
  def top: T = elems.head
  def pop() { elems = elems.tail }
}

val stack = new Stack[Int]
stack.push(1)
stack.push('a')
println(stack.top)
//97
stack.pop()
println(stack.top)
//1
```
- Class ```Stack``` models imperative (mutable) stacks of an arbitrary element type ```T```.
- The type parameters enforces that only legal elements (that are of type T) are pushed onto the stack. 
- Similarly, with type parameters we can express that method ```top``` will only yield elements of the given type.
