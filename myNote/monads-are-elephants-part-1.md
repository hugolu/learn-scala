出處：[Monads are Elephants Part 1](http://james-iry.blogspot.tw/2007/09/monads-are-elephants-part-1.html)

# 莫內是頭大象 之一

這篇文章將透過 Scala 的方式介紹 Monad。

瞎子摸象寓言中，抱住象腳的盲人說「這是一棵樹」，握著象鼻的盲人說「一條大蛇」，第三位盲人說「像是掃帚或扇子的東西」。自身的限制會我們無法掌握全貌，某方面來說我們全是盲人。這就是禪。

跟寓言想告訴我們相反的是，透過一連串有限的解釋，人們更有機會掌握全貌。如果你從來都沒看過大象，但有人跟你說「腿像樹幹一樣粗」、「鼻子長得像蛇」、「尾巴像掃帚」、「耳朵像扇子」，很快你就能明白。雖然對大象的概念並不完美，但最終你看到大象的時候，它能符合先前慢慢在你腦中建立的形象。這頭大象要踩扁你的時候，你還會想「哇！它的腿還真像棵樹」。

## Monad 是容器型別 (Container Type)

`List` 是最常用的容器型別之一，我們會花點時間在這上面。先前文章提過 `Option` 也是，提醒一下，`Option` 總是 `Some(value)` 或是 `None`。或許 `List` 跟 `Option` 的關聯不是那麼清楚，但如果你把 `Option` 想像成只有一個或零個元素的 `List`，有助理解。`Tree`跟 `Set` 也是 Monad。但記住  Monad 是頭大象，對於某些 Monad 你需要瞇起眼睛把它們看是容器。

Monad 可以參數化。`List` 是種有用的概念，但你需要知道 `List` 裡面有什麼。字串串列 (`List[String]`) 跟整數串列 (`List[Int]`) 很不一樣。能將其中一種轉換成另一種，有很明顯的用處。這樣的轉換引導我們進入下個重點。

## Monad 支援高階函數 (Higher Order Function)

A higher order function is a function that takes a function as a parameter or returns a function as a result. Monads are containers which have several higher order functions defined. Or, since we're talking about Scala, monads have several higher order methods.

One such method is map. If you know any functional languages then you're probably familiar with map in one form or another. The map method takes a function and applies it to each element in the container to return a new container. For instance

```scala
def double(x: Int) = 2 * x
val xs = List(1, 2, 3)
val doubles = xs map double
// or val doubles = xs map {2 * _}
assert(doubles == List(2, 4, 6))
```

Map does not change the kind of monad, but may change its parameterized type...

```scala
val one = Some(1)
val oneString = one map {_.toString}
assert(oneString == Some("1"))
```

Here the {_.toString} notation means that the toString method should be called on the element.

## Monads are Combinable

Now let's say we have a configuration library that let's us fetch parameters. For any parameter we'll get back an Option[String] - in other words we may or may not get a string depending on whether the parameter is defined. Let's say we also have a function, stringToInt, which takes a String and returns Some[Int] if the string is parseable as an integer or None if it's not. If we try to combine them using map we run into trouble.

```scala
val opString : Option[String] = config fetchParam "MaxThreads"
def stringToInt(string:String) : Option[Int] = ...
val result = opString map stringToInt
```

Unfortunately, since we started with an Option and mapped its contained element with a function that results in another Option, the variable "result" is now an Option that contains an Option, ie result is an Option[Option[Int]]. That's probably not terribly useful in most cases.

To motivate a solution, imagine if instead of Option we'd used List and ended up with List[List[Int]]] - in other words a list containing some number of lists. Given that, we just need "flatten" - a function which takes a list of lists (List[List[A]]) and returns a single list (List[A]) by concatenating everything together.<sup>[1](#footnote1)</sup>

A flatten function for Option[Option[A]] works a bit differently.

```scala
def flatten[A](outer:Option[Option[A]]) : Option[A] = outer match {
  case None => None
  case Some(inner) => inner
}
```

If the outer option is None, then result is None. Otherwise the result is the inner Option.

These two flatten functions have similar signatures: they take an M[M[A]] and turn it into an M[A]. But the way they do it is quite different. Other monads would have their own ways of doing flatten - possibly quite sophisticated ways. This possible sophistication is why explanations of monads will often use "join" instead of "flatten." "Join" neatly indicates that some aspect of the outer monad may be combined (joined) with some aspect of the inner monad. I'll stick with "flatten," though, because it fits with our container analogy.

Now, Scala does not require you to write flatten explicitly. But it does require that each monad have a method called flatMap.<sup>[2](#footnote2)</sup> What's flatMap? It's exactly what it sounds like: doing a map and then flattening the result.

```scala
class M[A] {
  private def flatten[B](x:M[M[B]]) : M[B] = ...
  def map[B](f: A => B) : M[B] = ...
  def flatMap[B](f: A => M[B]) : M[B] = flatten(map(f))
}
```

With that, we can revisit our problematic code...

```scala
val opString : Option[String] = config fetchParam "MaxThreads"
def stringToInt(string:String) : Option[Int] = ...
val result = opString flatMap stringToInt
```

Because of flatMap we end up with "result" being an Option[Int]. If we wanted, we could take result and flatMap it with a function from Int to Option[Foo]. And then we could faltMap that with a function from Foo to Option[Bar], etc.

If you're keeping score, many papers on monads use the word "bind" instead of "flatMap" and Haskell uses the ">>=" operator. It's all the same concept.

## Monads Can Be Built In Different Ways

So we've seen how the flatMap method can be built using map. It's possible to go the other way: start with flatMap and create map based on it. In order to do so we need one more concept. In most papers on monads the concept is called "unit," in Haskell it's called "return." Scala is an object oriented language so the same concept might be called a single argument "constructor" or "factory." Basically, unit takes one value of type A and turns it into a monad of type M[A]. For List, unit(x) == List(x) and for Option, unit(x) == Some(x).

Scala does not require a separate "unit" function or method, and whether you write it or not is a matter of taste. In writing this version of map I'll explicitly write "unit" just to show how it fits into things.

```scala
class M[A](value: A) {
  private def unit[B] (value : B) = new M(value)
  def map[B](f: A => B) : M[B] = flatMap {x => unit(f(x))}
  def flatMap[B](f: A => M[B]) : M[B] = ...
}
```

In this version flatMap has to be built without reference to map or flatten - it will have to do both in one go. The interesting bit is map. It takes the function passed in (f) and turns it into a new function that is appropriate for flatMap. The new function looks like {x => unit(f(x))} meaning that first f is applied to x, then unit is applied to the result.

## Conclusion for Part I

Scala monads must have map and flatMap methods. Map can be implemented via flatMap and a constructor or flatMap can be implemented via map and flatten.

flatMap is the heart of our elephantine beast. When you're new to monads, it may help to build at least the first version of a flatMap in terms of map and flatten. Map is usually pretty straight forward. Figuring out what makes sense for flatten is the hard part.

As you move into monads that aren't collections you may find that flatMap should be implemented first and map should be implemented based on it and unit.

In part 2 I'll cover Scala's syntactic sugar for monads. In part 3 I'll present the elephant's DNA: the monad laws. Finally, in part 4 I'll show a monad that's only barely a container. In the meantime, here's a cheat sheet for translating between computer science papers on monads, Haskell, and Scala.

| Generic | Haskell | Scala |
|---------|---------|-------|
| `M`	| `data M a`<br>`newtype M a`<br>`instance Monad (M a)` | `class M[A]`<br>`case class M[A]`<br>`trait M[A]` |
| `M a` | `M a` | `M[A]` |
| `unit v`| `return v` | `new M(v)`<br>`M(v)` |
| `map f m` | `fmap f m` | `m map f` |
| `bind f m` | `m >>= f`<br>`f =<< m` | `m flatMap f` |
| `join` | `join` | `flatten` | 
|  | `do` | `for` |

## Footnotes

1. <a name="footnote1"></a> The Scala standard library includes a flatten method on List. It's pretty slick, but to explain it I would have to go into implicit conversions which would be a significant distraction. The slick part is that flatten makes sense on List[List[A]] but not on List[A], yet Scala's flatten method is defined on all Lists while still being statically type checked.
2. <a name="footnote2"></a> I'm using a bit of shorthand here. Scala doesn't "require" any particular method names to make a monad. You can call your methods "germufaBitz" or "frizzleMuck". However, if you stick with map and flatMap then you'll be able to use Scala's "for comprehensions"
