# Class Hierarchy

## Implementation of binary tree
```scala
object IntSet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val t1 = new NonEmpty(3, new Empty, new Empty)  //> t1  : week3.NonEmpty = {.3.}
  val t2 = t1 incl 4                              //> t2  : week3.IntSet = {.3{.4.}}
}

abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
}

class Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, new Empty, new Empty)
  override def toString = "."
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true
  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x) else this

  override def toString = "{" + left + elem + right + "}"
}
```
- ```Empty``` and ```NonEmpty``` both extend the class ```IntSet```.
 - ```IntSet``` is the super-class, ```Empty``` and ```NonEmpty``` is the sub-class
 - This implies that the types ```Empty``` and ```NonEmpty``` conform to the type ```IntSet```

## Singleton
In the ```IntSet``` example, one could argue that there is really only a single empty ```IntSet```.
So it seems overkill to have the user create many instances of it. We can express this case better with an object definition.
This defines a *singleton* object named ```Empty```.

```scala
object IntSet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val t1 = new NonEmpty(3, Empty, Empty)          //> t1  : week3.NonEmpty = {.3.}
  val t2 = t1 incl 4                              //> t2  : week3.IntSet = {.3{.4.}}
}

abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
}

object Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
  override def toString = "."
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true
  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x) else this

  override def toString = "{" + left + elem + right + "}"
}
```

## Union
```scala
object IntSet {
  val t1 = new NonEmpty(3, Empty, Empty)          //> t1  : week3.NonEmpty = {.3.}
  val t2 = t1 incl 4 incl 5                       //> t2  : week3.IntSet = {.3{.4{.5.}}}

  val t3 = new NonEmpty(6, Empty, Empty)          //> t3  : week3.NonEmpty = {.6.}
  val t4 = t3 incl 5 incl 4                       //> t4  : week3.IntSet = {{{.4.}5.}6.}

  val t5 = t2 union t4                            //> t5  : week3.IntSet = {{{{.3.}4.}5.}6.}
}

abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
  def union(other: IntSet): IntSet
}

object Empty extends IntSet {
  override def toString = "."

  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
  def union(other: IntSet): IntSet = other
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  override def toString = "{" + left + elem + right + "}"

  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true
  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x) else this
  def union(that: IntSet): IntSet = ((left union right) union that) incl elem
}
```
- ```Empty.union```本身是空的，結合 ```that```等於```that```
- ```NonEmpty.union```組合自身的```left```、```right```與```that```，最後再包含自身的```elem```，確保遞迴呼叫不會變成無窮迴圈。
