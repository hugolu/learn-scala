出處：[Monads are Elephants Part 3](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-3.html)

# 莫內是頭大象 之三 (Monads are Elephants Part 3)

在這一系列文章中，我已經透過瞎子摸象的方式呈現對 Monad 的觀點。藉由聆聽每個盲人對大象有限的解釋，逐漸認識大象。

到目前為止，我們看見了 Scala Monad 的外觀，但距離太過遙遠，該是看看內部的時候了。真正造就大象的是大象的 DNA；而莫內法則 (monad law) 就是 Monad 間有共同的 DNA。

這篇文章有很多需要消化的部分，分次閱讀或許是個不錯的主意。拿你已理解的 Monad (例如 `List`) 帶入法則也很有用。

## 眾生平等 (Equality for All)

開始之前，我必須半正式地解釋 "f(x) ≡ g(x)" 三重等號的含意，"≡" 指的是數學上的相等，是為了避免跟 "=" (assignment) 混淆。

當我說左邊的表示式跟右邊的表示式 "相等"，問題是 "相等" 究竟是什麼意思。

首先，我不是在說兩者的參考相同 (reference identity)。參考相同當然滿足定義，但這樣要求太強烈。再則，也不一定意味 `==` 相等，除非實作上剛好如此。

我指的 "相等" 是兩個物件無法直接或間接藉由原始參考相等(primitive reference equality)、參考雜湊值(reference based hash code)、或 `isInstanceOf` 來區分。

事實上，有可能左邊表示式的物件跟右邊表示式的物件內部有些微不同，但仍然視為 "相等"。例如，一個物件使用額外間接層包覆跟右邊物件一樣的結果。重點是，從外面看起來，這兩個物件的行為一模一樣。

再對 "相等" 多點註釋。所有我要講的法則都暗喻不存在任何副作用 (no side effects)。關於副作用我在文章最後會多講一些。

## 打破規則 (Breaking the Law)

不可避免地，有人會問：「如果我打破規則x那會發生什麼？」完整的答案取決於什麼規律被打破，以及如何被打破，但我想先全面地接觸它。以下是另一個數學分支的規則提醒。如果 `a`、`b`、`c` 都是有理數，那麼乘法 (`*`) 遵守下面規則：

```
a * 1 ≡ a
a * b ≡ b * a
(a * b) * c ≡ a * (b * c)
```

當然，創建一個名為 "有理數" 類別和實作 `*` 方法很容易。但是，如果沒有按照這些規則，得到的結果會讓人困惑。這個類別的用戶會試著將它代入公式而得到錯誤答案。坦白說，你很難打破這些規則還讓這些東西像是有理數乘法。

Monad 不是有理數，但確實有能夠幫助我們定義與操作 Monad 的規則。像是算術運算，有所謂公式讓你用有趣的方式使用它們。例如，Scala "for" 可以根據這些規則展開，所以打破 Monad 法則就像打破使用者對 "for" 的預期。

介紹夠了，為了解釋 Monad 法則，我會從另一個詭異的的詞開始：Functor。

## 花惹發 - 什麼是 functor (WTF - What The Functor?)

通常有 Monad 與 Functor 開頭的文章很快就會變成一碗希臘字母。這是因為兩者都是數學範疇論的抽象概念，完整解釋它們是一件數學上的苦差事。幸運的是，我的任務不是完全解釋它們，而是能在 Scala 中涵蓋它們就好。

在 Scala 中，Functor 是一個有 `map` 方法和一些簡單操作特性的類別。例如型別 `M[A]` 的 Functor，其 `map` 方法是接收一個將 `A` 轉換成 `B` 的函數然後傳回 `M[B]`。換句話說，`map` 根據函數參數將 `M[A]` 轉換成 `M[B]`。把 `map` 想成執行轉換 (transformation) 而不需要處理任何迴圈 (loop)，這點很重要。或許它會實作迴圈，也或許不會。

`map` 的函數簽名 (signature) 看起來像這樣

```scala
class M[A] {
  def map[B](f: A => B): M[B] = ...
}
```

## Functor 第一定律：同等性 (First Functor Law: Identity)

這麼說好了，我發明了一個函數叫做 `identity`，像這樣

```scala
def identity[A](x:A) = x  
```

對於任何 `x` 有以下特性

```
identity(x) ≡ x
```

它沒做什麼，這就是重點。它只不過是原封不動回傳它的參數 (不論是什麼型別)。這裏是我們第一個 functor 法則：對於任何 functor `m`

- F1. `m map identity ≡ m` // 或等效於
- F1b. `m map {x => identity(x)} ≡ m` // 或等效於
- F1c. `m map {x => x} ≡ m`

In other words, doing nothing much should result in no change. Brilliant! However, I should remind you that the expression on the left can return a different object and that object may even have a different internal structure. Just so long as you can't tell them apart.

If you were to create a functor that didn't follow this law then the following wouldn't hold true. To see why that would be confusing, pretend m is a List.

- F1d. for (x <- m) yield x ≡ m

## Functor 第二定律：結合性 (Second Functor Law: Composition)

The second functor law specifies the way several "maps" compose together.

- F2. m map g map f ≡ m map {x => f(g(x))}

This just says that if you map with g and then map with f then it's exactly the same thing as mapping with the composition "f of g." This composition law allows a programmer to do things all at once or stretch them out into multiple statements. Based on this law, a programmer can always assume the following will work.

```scala
val result1 = m map (f compose g)
val temp = m map g
val result2 =  temp map f
assert result1 == result2
```
In "for" notation this law looks like the following eye bleeder

- F2b. for (y<- (for (x <-m) yield g(x)) yield f(y) ≡ for (x <- m) yield f(g(x))

## Functor 與 Monad，萬歲萬歲萬萬歲 (Functors and Monads, Alive, Alive Oh)

As you may have guessed by now all monads are functors so they must follow the functor laws. In fact, the functor laws can be deduced from the monad laws. It's just that the functor laws are so simple that it's easier to get a handle on them and see why they should be true.

As a reminder, a Scala monad has both map and flatMap methods with the following signatures

```scala
class M[A] {
 def map[B](f: A => B):M[B] = ...
 def flatMap[B](f: A=> M[B]): M[B] = ...
}
```

Additionally, the laws I present here will be based on "unit." "unit" stands for a single argument constructor or factory with the following signature

```scala
def unit[A](x:A):M[A] = ...
```

"unit" shouldn't be taken as the literal name of a function or method unless you want it to be. Scala doesn't specify or use it but it's an important part of monads. Any function that satisfies this signature and behaves according to the monad laws will do. Normally it's handy to create a monad M as a case class or with a companion object with an appropriate apply(x:A):M[A] method so that the expression M(x) behaves as unit(x).

## Functor/Monad 連結定律：第零定律 (The Functor/Monad Connection Law: The Zeroth Law)

In the very first installment of this series I introduced a relationship

- FM1. m map f ≡ m flatMap {x => unit(f(x))}

This law doesn't do much for us alone, but it does create a connection between three concepts: unit, map, and flatMap.

This law can be expressed using "for" notation pretty nicely

- FM1a. for (x <- m) yield f(x) ≡ for (x <- m; y <- unit(f(x))) yield y

## 再論 flatten (Flatten Revisited)

In the very first article I mentioned the concept of "flatten" or "join" as something that converts a monad of type M[M[A]] into M[A], but didn't describe it formally. In that article I said that flatMap is a map followed by a flatten.

- FL1. m flatMap f ≡ flatten(m map f)

This leads to a very simple definition of flatten

```scala
flatten(m map identity) ≡ m flatMap identity // substitute identity for f
FL1a. flatten(m) ≡ m flatMap identity // by F1
```

So flattening m is the same as flatMapping m with the identity function. I won't use the flatten laws in this article as flatten isn't required by Scala but it's a nice concept to keep in your back pocket when flatMap seems too abstract.

## Monad 第一定律：同等性 (The First Monad Law: Identity)

The first and simplest of the monad laws is the monad identity law

- M1. m flatMap unit ≡ m // or equivalently
- M1a. m flatMap {x => unit(x)} ≡ m

Where the connector law connected 3 concepts, this law focuses on the relationship between 2 of them. One way of reading this law is that, in a sense, flatMap undoes whatever unit does. Again the reminder that the object that results on the left may actually be a bit different internally as long as it behaves the same as "m."

Using this and the connection law, we can derive the functor identity law

```scala
m flatMap {x => unit(x)} ≡ m // M1a
m flatMap {x => unit(identity(x))}≡ m // identity
F1b. m map {x => identity(x)} ≡ m // by FM1
```

The same derivation works in reverse, too. Expressed in "for" notation, the monad identity law is pretty straight forward

- M1c. for (x <- m; y <- unit(x)) yield y ≡ m

## Monad 第二定律：單元 (The Second Monad Law: Unit)

Monads have a sort of reverse to the monad identity law.

- M2. unit(x) flatMap f ≡ f(x) // or equivalently
- M2a. unit(x) flatMap {y => f(y)} ≡ f(x)

The law is basically saying that unit(x) must somehow preserve x in order to be able to figure out f(x) if f is handed to it. It's in precisely this sense that it's safe to say that any monad is a type of container (but that doesn't mean a monad is a collection!).

In "for" notation, the unit law becomes

- M2b. for (y <- unit(x); result <- f(y)) yield result ≡ f(x)

This law has another implication for unit and how it relates to map

```scala
unit(x) map f ≡ unit(x) map f // no, really, it does!
unit(x) map f ≡ unit(x) flatMap {y => unit(f(y))} // by FM1
M2c. unit(x) map f ≡ unit(f(x)) // by M2a
```

In other words, if we create a monad instance from a single argument x and then map it using f we should get the same result as if we had created the monad instance from the result of applying f to x. In for notation

- M2d. for (y <- unit(x)) yield f(y) ≡ unit(f(x))

## Monad 第三定律：結合性 (The Third Monad Law: Composition)

The composition law for monads is a rule for how a series of flatMaps work together.

- M3. m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f} // or equivalently
- M3a. m flatMap {x => g(x)} flatMap {y => f(y)} ≡ m flatMap {x => g(x) flatMap {y => f(y) }}

It's the most complicated of all our laws and takes some time to appreciate. On the left side we start with a monad, m, flatMap it with g. Then that result is flatMapped with f. On the right side, we create an anonymous function that applies g to its argument and then flatMaps that result with f. Finally m is flatMapped with the anonymous function. Both have same result.

In "for" notation, the composition law will send you fleeing in terror, so I recommend skipping it

- M3b. for (a <- m;b <- g(a);result <- f(b)) yield result ≡ for(a <- m; result <- for(b < g(a); temp <- f(b)) yield temp) yield result

From this law, we can derive the functor composition law. Which is to say breaking the monad composition law also breaks the (simpler) functor composition. The proof involves throwing several monad laws at the problem and it's not for the faint of heart

```scala
m map g map f ≡ m map g map f // I'm pretty sure
m map g map f ≡ m flatMap {x => unit(g(x))} flatMap {y => unit(f(y))} // by FM1, twice
m map g map f ≡ m flatMap {x => unit(g(x)) flatMap {y => unit(f(y))}} // by M3a
m map g map f ≡ m flatMap {x => unit(g(x)) map {y => f(y)}} // by FM1a
m map g map f ≡ m flatMap {x => unit(f(g(x))} // by M2c
F2. m map g map f ≡ m map {x => f(g(x))} // by FM1a
```

## 徹底的魯蛇 zero (Total Loser Zeros)

List has Nil (the empty list) and Option has None. Nil and None seem to have a certain similarity: they both represent a kind of emptiness. Formally they're called monadic zeros.

A monad may have many zeros. For instance, imagine an Option-like monad called Result. A Result can either be a Success(value) or a Failure(msg). The Failure constructor takes a string indicating why the failure occurred. Every different failure object is a different zero for Result.
A monad may have no zeros. While all collection monads will have zeros (empty collections) other kinds of monads may or may not depending on whether they have a concept of emptiness or failure that can follow the zero laws.

## zero 第一定律：相等性 (The First Zero Law: Identity)

If mzero is a monadic zero then for any f it makes sense that

- MZ1. mzero flatMap f ≡ mzero

Translated into Texan: if t'ain't nothin' to start with then t'ain't gonna be nothin' after neither.

This law allows us to derive another zero law

```scala
mzero map f ≡ mzero map f // identity
mzero map f ≡ mzero flatMap {x => unit(f(x)) // by FM1
MZ1b. mzero map f ≡ mzero // by MZ1
```

So taking a zero and mapping with any function also results in a zero. This law makes clear that a zero is different from, say, unit(null) or some other construction that may appear empty but isn't quite empty enough. To see why look at this

```scala
unit(null) map {x => "Nope, not empty enough to be a zero"} ≡ unit("Nope, not empty enough to be a zero")
```

## zero 第二定律：M 到 Zero 沒啥好攤平的  (The Second Zero Law: M to Zero in Nothing Flat)

The reverse of the zero identity law looks like this

- MZ2. m flatMap {x => mzero} ≡ mzero

Basically this says that replacing everything with nothing results in nothing which um...sure. This law just formalizes your intuition about how zeros "flatten."

## zero 第三、四定律：加法 (The Third and Fourth Zero Laws: Plus)

Monads that have zeros can also have something that works a bit like addition. For List, the "plus" equivalent is ":::" and for Option it's "orElse." Whatever it's called its signature will look this

```scala
class M[A] {
   ...
   def plus(other:M[B >: A]): M[B] = ...
}
```

Plus has the following two laws which should make sense: adding anything to a zero is that thing.

- MZ3. mzero plus m ≡ m
- MZ4. m plus mzero ≡ m

The plus laws don't say much about what "m plus n" is if neither is a monadic zero. That's left entirely up to you and will vary quite a bit depending on the monad. Typically, if concatenation makes sense for the monad then that's what plus will be. Otherwise, it will typically behave like an "or," returning the first non-zero value.

## 再論 filter (Filtering Revisited)

In the previous installment I briefly mentioned that filter can be seen in purely monadic terms, and monadic zeros are just the trick to seeing how. As a reminder, a filterable monad looks like this

```scala
class M[A] {
   def map[B](f: A => B):M[B] = ...
   def flatMap[B](f: A=> M[B]): M[B] = ...
   def filter(p: A=> Boolean): M[A] = ...
}
```

The filter method is completely described in one simple law

- FIL1. m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}

We create an anonymous function that takes x and either returns unit(x) or mzero depending on what the predicate says about x. This anonymous function is then used in a flatMap. Here are a couple of results from this

```scala
m filter {x => true} ≡ m filter {x => true} // identity
m filter {x => true} ≡ m flatMap {x => if (true) unit(x) else mzero} // by FIL1
m filter {x => true} ≡ m flatMap {x => unit(x)} // by definition of if
FIL1a. m filter {x => true} ≡ m // by M1
```

So filtering with a constant "true" results in the same object. Conversely

```
m filter {x => false} ≡ m filter {x => false} // identity
m filter {x => false} ≡ m flatMap {x => if (false) unit(x) else mzero} // by FIL1
m filter {x => false} ≡ m flatMap {x => mzero} // by definition of if
FIL1b. m filter {x => false} ≡ mzero // by MZ1
```

Filtering with a constant false results in a monadic zero.

## 副作用 (Side Effects)

Throughout this article I've implicitly assumed no side effects. Let's revisit our second functor law

```
m map g map f ≡ m map {x => (f(g(x)) }
```

If m is a List with several elements, then the order of the operations will be different between the left and right side. On the left, g will be called for every element and then f will be called for every element. On the right, calls to f and g will be interleaved. If f and g have side effects like doing IO or modifying the state of other variables then the system might behave differently if somebody "refactors" one expression into the other.

The moral of the story is this: avoid side effects when defining or using map, flatMap, and filter. Stick to foreach for side effects. Its very definition is a big warning sign that reordering things might cause different behavior.

Speaking of which, where are the foreach laws? Well, given that foreach returns no result, the only real rule I can express in this notation is

```
m foreach f ≡ ()
```

Which would imply that foreach does nothing. In a purely functional sense that's true, it converts m and f into a void result. But foreach is meant to be used for side effects - it's an imperative construct.

## 第三部分結論 (Conclusion for Part 3)

Up until now, I've focused on Option and List to let your intuition get a feel for monads. With this article you've finally seen what really makes a monad a monad. It turns out that the monad laws say nothing about collections; they're more general than that. It's just that the monad laws happen to apply very well to collections.

In part 4 I'm going to present a full grown adult elephant er monad that has nothing collection-like about it and is only a container if seen in the right light.

Here's the obligatory Scala to Haskell cheet sheet showing the more important laws

|   | Scala | Haskell |
|---|-------|---------|
| FM1 | `m map f ≡ m flatMap {x => unit(f(x))}` | `fmap f m ≡ m >>= \x -> return (f x)` |
| M1 | `m flatMap unit ≡ m` | `m >>= return ≡ m` |
| M2 | `unit(x) flatMap f ≡ f(x)` | `(return x) >>= f ≡ f x` |
| M3 | `m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f}` | `(m >>= f) >>= g ≡ m >>= (\x -> f x >>= g)` |
| MZ1 | `mzero flatMap f ≡ mzero` | `mzero >>= f ≡ mzero` |
| MZ2 | `m flatMap {x => mzero} ≡ mzero` | `m >>= (\x -> mzero) ≡ mzero` |
| MZ3 | `mzero plus m ≡ m` | `mzero mplus m ≡ m` |
| MZ4 | `m plus mzero ≡ m` | `m mplus mzero ≡ m` |
| FIL1 | `m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}` | `mfilter p m ≡ m >>= (\x -> if p x then return x else mzero)` |
