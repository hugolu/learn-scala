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

換句話說，沒做什麼事所以結果也沒改變。聰明！不過，我應該提醒你左邊表示式可以回傳不同的物件並且這個物件可以有不同的內部結構。只要讓你分辨不出兩者即可。

如果你產生的 functor 沒有遵守這個法則，那麼以下規則也不為真。追根究底可能讓人困惑，你就假裝 `m` 是 `List` 吧。

- F1d. `for (x <- m) yield x ≡ m`

## Functor 第二定律：結合性 (Second Functor Law: Composition)

第二定律規範多個 `map` 結合的方法。

- F2. `m map g map f ≡ m map {x => f(g(x))}`

如果你 `map` 函數 "g" 然後再 `map` 函數 "f"，就如同 `map` 組合的 "f of g"。這個組合法則允許程式一口氣做完所有的事，或拆成多個步驟。根據這條法則，你永遠可以假設以下程式可行。

```scala
val result1 = m map (f compose g)
val temp = m map g
val result2 =  temp map f
assert result1 == result2
```

"for" 表示法中，這法則看起來像是下面這樣讓人眼睛脫窗的程式碼

- F2b. `for (y <- (for (x <- m) yield g(x)) yield f(y) ≡ for (x <- m) yield f(g(x))`

## Functor 與 Monad，萬歲萬歲萬萬歲 (Functors and Monads, Alive, Alive Oh)

如同你現在猜測的，所有 Monad 都是 Functor，所以都要遵守 Functor 法則。事實上，Functor 法則可以由 Monad 法則推導而來。只是，Functor 法則很單純所以操作跟觀察它們比較容易。

提醒一下，Scala Monad 有下面這樣的 `map` 和 `flatMap` 方法

```scala
class M[A] {
  def map[B](f: A => B): M[B] = ...
  def flatMap[B](f: A => M[B]): M[B] = ...
}
```

事實上，此處介紹的法則將根據 `unit`。`unit` 表示單一參數的建構函數 (constructor) 或工廠函數 (factory)，有以下的函數簽名 (signature)

```scala
def unit[A](x: A): M[A] = ...
```

`unit` 不該依照字面解釋，除非你真的想這樣。Scala 沒有指定或使用它，但它是 Monad 重要的一環。任何根據 Monad 法則滿足這個簽名與行為的函數都算 `unit`。正常來說，用 `case class` 或呼叫伴生物件(companion object) `apply(x: A): M[A]` 來產生 Monad `M` 很便利，所以表示式 `M(x)` 行為如同 `unit(x)`。

## Functor/Monad 連結定律：第零定律 (The Functor/Monad Connection Law: The Zeroth Law)

這一列的開頭，我介紹了一個關係

- FM1. `m map f ≡ m flatMap {x => unit(f(x))}`

這法則沒做什麼，但連結了三個概念：`unit`、`map`、`flatMap`。

這法則可以用 "for" 漂亮的表示出來

- FM1a. `for (x <- m) yield f(x) ≡ for (x <- m; y <- unit(f(x))) yield y`

## 再論 flatten (Flatten Revisited)

很前面的文章，提過 `flatten` 或 `join` 的概念像是某種把 `M[M[A]]` 轉換成 `M[A]` 的東西，不過還沒有正式描述。那篇文章我說 `flatMap` 是 `map` 之後接著做 `flatten`。

- FL1. `m flatMap f ≡ flatten(m map f)`

導出簡單的 `flatten` 定義

- `flatten(m map identity) ≡ m flatMap identity` // substitute identity for f
- FL1a. `flatten(m) ≡ m flatMap identity` // by F1

所以 `flatten(m)` 如同 `m flatMap identify`。我不會在這篇文章中用 Flatten 法則，Scala 不是那麼需要 `flatten`，但要是 `flatMap` 太過抽象，這個放在你背包的好概念就可以拿出來用。

## Monad 第一定律：同等性 (The First Monad Law: Identity)

第一個也是簡單的 Monad 定律是 Monad 同等定律

- M1. `m flatMap unit ≡ m` // or equivalently
- M1a. `m flatMap {x => unit(x)} ≡ m`

連結定律連接三個概念，這個定律聚焦在其中兩者關係。閱讀這個定律，其中一個方式是不管 `unit` 做過什麼 `flatMap` 就把它回復原狀。再次提醒，左邊結果確實內在有些不同，不過只要它行為上像 `m` 即可。

使用這個跟連接定律，能衍伸出 functor 同等定律

- `m flatMap {x => unit(x)} ≡ m` // M1a
- `m flatMap {x => unit(identity(x))}≡ m` // identity
- F1b. `m map {x => identity(x)} ≡ m` // by FM1

反過來，推導也成立。用 "for" 來表示，Monad 同等定律相當直覺。

- M1c. `for (x <- m; y <- unit(x)) yield y ≡ m`

## Monad 第二定律：單元 (The Second Monad Law: Unit)

Monad 有種同等性的逆定律。

- M2. `unit(x) flatMap f ≡ f(x)` // or equivalently
- M2a. `unit(x) flatMap {y => f(y)} ≡ f(x)`

基本上，這法則是說如果要用 `f`，`unit(x)` 就要保留 `x` 以便得到 `f(x)`。可以這麼說，任何 Monad 都是一種容器。（但不表示 Monad 是個集合！）

用 "for" 來表示，Unit 法則變成

- M2b. `for (y <- unit(x); result <- f(y)) yield result ≡ f(x)`

這法則有個 `unit` 的另外暗喻，以及它如何關聯 `map`

```scala
unit(x) map f ≡ unit(x) map f                       // 不豪洨，真的，就醬！
unit(x) map f ≡ unit(x) flatMap {y => unit(f(y))}   // 根據 FM1
```
- M2c. `unit(x) map f ≡ unit(f(x))` // by M2a

換句話說，從單一參數 `x` 產生 Monad 實體後用 `f` 做 `map`，得到的結果應該就如同用 `f` 應用到 `x` 的結果產生 Monad。 "for" 表示法如下

- M2d. `for (y <- unit(x)) yield f(y) ≡ unit(f(x))`

## Monad 第三定律：結合性 (The Third Monad Law: Composition)

Monad 結合定律規定一連串 `flatMap` 如何一起工作。

- M3. `m flatMap g flatMap f ≡ m flatMap {x => g(x) flatMap f}` // or equivalently
- M3a. `m flatMap {x => g(x)} flatMap {y => f(y)} ≡ m flatMap {x => g(x) flatMap {y => f(y)}}`

上面這條是所有定律裡面最複雜的了，需要花點時間欣賞。左邊開始一個 Monad `m`，先用 `g` 函數對 `m` 做 `flatMap` ，然後再用 `f` 函數對其結果做 `flatMap`。右邊有個匿名函數，用 `g` 函數作用在匿名函數的參數 `x`，然後再用 `f` 函數對其結果做 `flatMap`，最後用匿名函數對 `m` 做 `flatMap`。左右兩邊結果一樣。

用 "for" 來表示，結合定律給人很恐怖的感覺，我建議跳過它。

- M3b. `for (a <- m; b <- g(a); result <- f(b)) yield result ≡ for(a <- m; result <- for(b <- g(a); temp <- f(b)) yield temp) yield result`

(譯注：上面公式重新排版如下)
```scala
for {
  a <- m
  b <- g(a)
  result <- f(b)
} yield result
≡ 
for{
  a <- m
  result <- for {
    b <- g(a)
    temp <- f(b)
  } yield temp
} yield result
```

從這定律延伸 Functor 結合定律。意思是說，違背 Monad 結合定律也違背 (較簡單的) Functor 結合定律。證明牽涉多個 Moand 定律，不適合心臟不好的朋友。

```scala
m map g map f ≡ m map g map f                                           // 我很確定
m map g map f ≡ m flatMap {x => unit(g(x))} flatMap {y => unit(f(y))}   // 根據 FM1, 兩次
m map g map f ≡ m flatMap {x => unit(g(x)) flatMap {y => unit(f(y))}}   // 根據 M3a
m map g map f ≡ m flatMap {x => unit(g(x)) map {y => f(y)}}             // 根據 FM1a
m map g map f ≡ m flatMap {x => unit(f(g(x))}                           // 根據 M2c
m map g map f ≡ m map {x => f(g(x))}                                    // 根據 FM1a
```
- F2. `m map g map f ≡ m map {x => f(g(x))}`

## 徹底的魯蛇 zero (Total Loser Zeros)

`List` 有 `Nil` (空的串列)，`Option` 有 `None`。`Nil` 與 `None` 似乎有某些共通性：他們都表示某種空無 (emptiness) 的感覺。正式來說他們被叫做 Monadic Zero。

一個 Monad 可以有很多 Zero。例如，想像類似 `Option` 的 Monad 被稱作結果 (Result)。一個結果可以是成功 `Success(value)` 或失敗 `Failuare(msg)`。失敗的建構函式接受一個字串說明為何失敗。每個不同的失敗物件都是針對不同失敗的 Zero。

Monad 也可以沒有 Zero。雖然所有的集合都有 Zero (空集合)，其他 Monad 依據是否需要空無或失敗的概念決定有或沒有 Zero。

## zero 第一定律：同等性 (The First Zero Law: Identity)

如果 `mzero` 是一種 Monadic Zero，對於任何 `f` 函數來說

- MZ1. `mzero flatMap f ≡ mzero`

翻譯蒟蒻：本來無一物，何處惹塵埃。

這條定律衍伸另一個 Zero 定律。

```scala
mzero map f ≡ mzero map f                     // 相等性
mzero map f ≡ mzero flatMap {x => unit(f(x))  // 根據 FM1
mzero map f ≡ mzero                           // 根據 MZ1
```

- MZ1b. `mzero map f ≡ mzero` // by MZ1

所以把 Zero 那來 `map` 任何函數結果得到 Zero。這個定律澄清 Zero 不同於 `unit(null)` 或其他看起來像是空的卻又不夠空的建構函式。以下說明為何

```scala
unit(null) map {x => "Nope, not empty enough to be a zero"}
≡
unit ("Nope, not empty enough to be a zero")
```

## zero 第二定律：M 到 Zero 沒啥好攤平的  (The Second Zero Law: M to Zero in Nothing Flat)

Zero 同等性的反面看起來像這樣。

- MZ2. `m flatMap {x => mzero} ≡ mzero`

基本來說，用空空如也取代任何東西，結果當然也是空空如也。這定律只是形式化你對 Zero 怎麼攤平 (`flatten`) 的想像。

## zero 第三、四定律：加法 (The Third and Fourth Zero Laws: Plus)

Monad 擁有 Zero，也能作用像加法的概念。例如 `List` 的加法等於 `:::`，`Option` 的加法是 `orElse`。不管叫什麼，它的簽名 (signature) 看起來會像

```scala
class M[A] {
  ...
  def plus(other: M[B >: A]): M[B] = ...
}
```

`Plus` 有以下兩條定律，任何東西跟 Zero 相加結果就是原來那個東西。

- MZ3. `mzero plus m ≡ m`
- MZ4. `m plus mzero ≡ m`

關於 "m plus n"，如果兩者都不是 Monadic Zero，加法定律沒有多提。這跟 Monad 有很大的關係，全由你決定。通常，如果對於 Monad 來說串聯有意義，加法才會有意義。反之，他行為會像 "or" 回傳第一個非零的值。

## 再論 filter (Filtering Revisited)

先前文章，我簡單提過 `filter` 可以單純視為 Monod 術語 (monadic term)，Monadic Zero 只是一個怎麼看的訣竅。提醒一下，一個可以過濾的 Monad 看起來像這樣。

```scala
class M[A] {
  def map[B](f: A => B): M[B] = ...
  def flatMap[B](f: A => M[B]): M[B] = ...
  def filter(p: A => Boolean): M[A] = ...
}
```

`filter` 有個簡單的方式描述如下

- FIL1. `m filter p ≡ m flatMap {x => if(p(x)) unit(x) else mzero}`

產生一個接收 `x` 根據判斷式結果回傳 `unit(x)` 或 `mzero` 的匿名函數。然後匿名函數用來做 `flatMap`。這裏有兩個從這個得到的結果。

```scala
m filter {x => true} ≡ m filter {x => true}                             // 同等性
m filter {x => true} ≡ m flatMap {x => if (true) unit(x) else mzero}    // 根據 FIL1
m filter {x => true} ≡ m flatMap {x => unit(x)}                         // 根據 if 定義
m filter {x => true} ≡ m                                                // 根據 M1
```
- FIL1a. `m filter {x => true} ≡ m`

以上是過濾 "true" 得到原來物件的結果。反之，

```
m filter {x => false} ≡ m filter {x => false}                           // 同等性
m filter {x => false} ≡ m flatMap {x => if (false) unit(x) else mzero}  // 根據 FIL1
m filter {x => false} ≡ m flatMap {x => mzero}                          // 根據 if 定義
m filter {x => false} ≡ mzero                                           // 根據 MZ1
```
- FIL1b.`m filter {x => false} ≡ mzero`

以上是過濾 "false" 得到 Monadic Zero 的結果。

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
