出處：[Monads are Elephants Part 1](http://james-iry.blogspot.tw/2007/09/monads-are-elephants-part-1.html)

# 莫內是頭大象 之一

這篇文章將透過 Scala 的方式介紹 Monad。

瞎子摸象寓言中，抱住象腳的盲人說「這是一棵樹」，握著象鼻的盲人說「一條大蛇」，第三位盲人說「像是掃帚或扇子的東西」。自身限制讓我們無法掌握事物的全貌，某方面來說我們都是盲人。這就是禪。

跟寓言故事想告訴我們相反的是，透過一連串有限的解釋，人們更有機會掌握全貌。如果你從來都沒看過大象，但有人跟你說「腿像樹幹一樣粗」、「鼻子長得像蛇」、「尾巴像掃帚」、「耳朵像扇子」，那麼很快你就能理解。雖然對大象的概念並不完美，但最終你看到大象的時候，它能符合先前慢慢在你腦中建立的形象。當這頭大象要踩扁你的時候，你還會想「哇！它的腿還真像棵樹」。

## 莫內是容器型別 (Monads are Container Types)

`List` 是最常用的容器型別之一，我們會花點時間在這上面。先前文章提過 `Option` 也是，提醒一下，`Option` 總是 `Some(value)` 或是 `None`。或許 `List` 跟 `Option` 的關聯不是那麼清楚，但如果你把 `Option` 想像成只有一個或零個元素的 `List`，有助理解。`Tree`跟 `Set` 也是 Monad。但記住  Monad 是頭大象，對於某些 Monad 你需要瞇起眼睛把它們看是容器。

Monad 可以參數化。`List` 是種有用的概念，但你需要知道 `List` 裡面有什麼。字串串列 (`List[String]`) 跟整數串列 (`List[Int]`) 很不一樣。能將其中一種轉換成另一種，有很明顯的用處。這樣的轉換引導我們進入下個重點。

## 莫內支援高階函數 (Monads Support Higher Order Functions)

A higher order function is a function that takes a function as a parameter or returns a function as a result. Monads are containers which have several higher order functions defined. Or, since we're talking about Scala, monads have several higher order methods.

高階函數是一個把函數當成參數傳入或把函數當成結果傳出的函數。Monad 這種容器包含許多高階函數。既然聊到 Scala，這麼說好了，Monad 擁有許多高階的方法 (method)。

`map` 是其中一個方法。如果你知道任何函數式語言，也許熟悉用 `map` 做格式轉換。`map` 方法接收一個函數，把它作用在容器內的每個元素，然後回傳一個新的容器。例如，

```scala
def double(x: Int) = 2 * x
val xs = List(1, 2, 3)
val doubles = xs map double
// or val doubles = xs map {2 * _}
assert(doubles == List(2, 4, 6))
```

`map` 不會改變 Monad 的種類，但可能改變參數類型...

```scala
val one = Some(1)
val oneString = one map {_.toString}
assert(oneString == Some("1"))
```

此處 `{_.toString}` 表示容器內的元素會被呼叫到 `toString` 的方法。

## 莫內可以合併 (Monads are Combinable)

比方說，有個能夠取得參數的配置函式庫 (configuration library)。對於任何參數都能得到一個 `Option[String]` 的結果，換句話說，有沒有定義參數決定我們能不能獲得相對的字串。現在有個函數 `stringToInt` 接受字串作參數，如果傳入的字串可以解析則回傳 `Some[Int]`，如果無法解析則回傳 `None`。試著合併兩者的話會出現這樣的問題。

```scala
val opString: Option[String] = config fetchParam "MaxThreads"
def stringToInt(string: String): Option[Int] = ...
val result = opString map stringToInt
```

很不幸，用 `map` 方式轉換 `Option` 內容，結果得到一個包含 `Option` 的 `Option`，也就是 `Option[Option[Int]]`。大多數情況下沒啥鳥用。

為了找出解決方法，來想像一下，假如我們用的是 `List` 然後得到 `List[List[Int]]`。這種情形下，我們需要用 `flatten` 來把 `List[List[Int]]` 攤平成 `List[Int]`。<sup>[1](#footnote1)</sup>

`Option[Option[A]]` 的 `flatten` 函數運作方式有點不同。

```scala
def flatten[A](outer:Option[Option[A]]) : Option[A] = outer match {
  case None => None
  case Some(inner) => inner
}
```

如果外層 `Option` 是 `None`，結果就是 `None`。否則結果就是內層的 `Option`。

這兩個 `flatten` 函數有相似之處：接受 `M[M[A]]`，傳回 `M[A]`，但運作方式很不一樣。其他 Monad 也有自己 `flatten` 的函數，可能用很複雜的方式做到。由於這種潛在複雜性，解釋 Monad 時常用 `join` 而不是 `flatten`。`join` 的概念清楚解釋外層 Monad 如何結合 (`join`) 內層 Monad。不過，接下來的文章我還是採用 `flatten` 這個術語，因為它比較適合對容器的類比。

Scala 不需要你明確寫 `flatten` 函數，但每個 Monad 都需要 `flatMap`。什麼是 `flatMap`？就像字面那樣，做完 `map` 後再做 `flatten`。

```scala
class M[A] {
  private def flatten[B](x: M[M[B]]): M[B] = ...
  def map[B](f: A => B): M[B] = ...
  def flatMap[B](f: A => M[B]): M[B] = flatten(map(f))
}
```

有了那個，我們可以改寫有問題的程式碼...

```scala
val opString: Option[String] = config fetchParam "MaxThreads"
def stringToInt(string: String): Option[Int] = ...
val result = opString flatMap stringToInt
```

因為 `flatMap`，我們得到 `Option[Int]` 的結果。如果想要，還可以再做一次 `flatMap`，透過用能把 `Int` 轉成 `Option[Foo]` 的函數。然後再做 `flatMap`，透過把 `Foo` 轉成 `Option[Bar]` 的函數，等等。

許多文章會用 `bind` 取代 `flatMap`，Haskell 會用 `>>=` 運算符號，都是一樣的概念。

## 莫內能用不同的方式產生 (Monads Can Be Built In Different Ways)

剛看過用 `map` 做到 `flatMap`。另一個方向也能通喔：用 `flatMap` 做到 `map`。為了做到這件事，還需要一個概念。大部份文章叫它做 `unit`，在 Haskell 稱呼它為 `return`。Scala 是一種物件導向語言，所以相同的概念會稱作單一參數的建構函數或工廠函數。基本上，`unit` 接收型別 `A` 的值，傳回型別 `M[A]` 的 Monad。以 `List` 來說，`unit(x) == List(x)`。以 `Option` 來說，`unit(x) == Some(x)`。

Scala 不需要單獨的 `unit` 函數或方法，寫不寫只是品味的問題。因為要弄這個版本的 `map`，我會明確地寫出 `unit` 並展示他的用途。

```scala
class M[A](value: A) {
  private def unit[B](value: B) = new M(value)
  def map[B](f: A => B): M[B] = flatMap {x => unit(f(x))}
  def flatMap[B](f: A => M[B]): M[B] = ...
}
```

這個版本的 `flatMap` 不用 `map` 或 `flatten`。有趣的部分是 `map`，它接收一個函數 (`f`) 然後傳給一個作用在 `flatMap` 裡的新函數。這個新函數看起來像 `{x => unit(f(x))}`，`f`先對`x`作用，然後`unit`再對其結果作用。

## 第一部份的結論

Scala Monad 必須具有 `map` 與 `flatMap` 方法。`map` 可以藉由 `flatMap` 與建構函數做到，或是 `flatMap` 可以透過 `map` 與 `flatten` 辦到。

`flatMap` 是大象的心臟。如果你對 Monad 陌生，用 `map` 與 `flatten` 至少能做到 `flatMap`。`map` 通常很直覺，理解 `flatten` 的意義比較困難。

當你接觸非集合 (collection) 的 Monad，你會發現應該先實做出 `flatMap`，然後再用它跟 `unit` 做出 `map`。

在第二部分，我會涵蓋有關 Monad 的語法糖。在第三部分，我會介紹大象的 DNA：Monad 的法則。最後第四部分，我用一個容器來展示 Monad。同時，這裏有張用來翻譯論文中有關 Monad、Haskell、Scala 的小抄。

| Generic | Haskell | Scala |
|---------|---------|-------|
| `M`	| `data M a`<br>`newtype M a`<br>`instance Monad (M a)` | `class M[A]`<br>`case class M[A]`<br>`trait M[A]` |
| `M a` | `M a` | `M[A]` |
| `unit v`| `return v` | `new M(v)`<br>`M(v)` |
| `map f m` | `fmap f m` | `m map f` |
| `bind f m` | `m >>= f`<br>`f =<< m` | `m flatMap f` |
| `join` | `join` | `flatten` | 
|  | `do` | `for` |

## 腳注

1. <a name="footnote1"></a> Scala 標準函式庫包含 `List` 的 `flatten` 方法。這相當靈活，不過想多做解釋就會在隱式轉換上岔題。靈活的部分是 `flatten` 對 `List[List[A]]` 才有意義，而非 `List[A]`。Scala `flatten` 方法定義在所有的 `List` 中，同時又能進行靜態型別檢查。
2. <a name="footnote2"></a> 這句話說得有點快，Scala 不*需要*用任何特定的方法來產生 Monad。你可以把你的方法叫做 `germufaBitz` 或 `frizzleMuck`。然而，如果你採用 `map` 與 `flatMap`，就能使用 Scala *for comprehensions*。
