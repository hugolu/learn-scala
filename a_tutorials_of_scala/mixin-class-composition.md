# Mixin Class Composition

```scala
abstract class AbsIterator {
  type T
  def hasNext: Boolean
  def next: T
}

trait RichIterator extends AbsIterator {
  def foreach(f: T => Unit) { while (hasNext) f(next) }
}

class StringIterator(s: String) extends AbsIterator {
  type T = Char
  private var i = 0
  def hasNext = i < s.length()
  def next = { val ch = s charAt i; i += 1; ch }
}

class Iter extends StringIterator("hello world") with RichIterator
val iter = new Iter
iter foreach print
//hello world
```

- ```AbsIterator```
  - an abstraction for iterators
- ```RichIterator```
  - a mixin class which extends ```AbsIterator``` with a method ```foreach``` which applies a given function to every element returned by the iterator
- ```StringIterator```
  - a concrete iterator class, which returns successive characters of a given string
- ```Iter```
  - ```Iter``` is constructed from a mixin composition of the parents ```StringIterator``` and ```RichIterator``` with the keyword ```with```
- *mixin-class composition* 
  - it allows the programmers to reuse the delta of a class definition, i.e., all new definitions that are not inherited. (```foreach``` in this example) 
