出處：[Monads are Elephants Part 2](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-2.html)

# 莫內是頭大象 之二 (Monads are Elephants Part 2)

在第一部分，我透過盲人摸象的寓言介紹 Scala Monad。通常我們認為盲人們不清楚大象的長相，但我提出另一個觀點，假如你聽過所有盲人的經驗你很快就能理解大象究竟是什麼。

第二部分，我將藉由探索 Scala Monad 相關的語法糖："for comprehensions"，戳一戳這頭大象。

## 來點 "for" (A Little "For")

一個很簡單的 "for" 看起來像這樣

```scala
val ns = List(1, 2)
val qs = for (n <- ns) yield n * 2
assert (qs == List(2, 4))
```

這段 "for" 運算可以讀作 "for [each] n [in] ns yield n * 2"。看起來像迴圈，但事實上是用 `map` 偽裝的。

```scala
val qs = ns map {n => n * 2}
```

規則很簡單

```scala
for (x <- expr) yield resultExpr
```

展開成 <sup>[1](#footnote1)</sup>

```scala
expr map {x => resultExpr}
```

提醒一下，這等同於

```scala
expr flatMap {x => unit(resultExpr)}
```

## 更多 "for" (More "For")

一個 "for" 裡面只有一個表示式 (expression) 稍嫌無趣。讓我們再加點東西進去

```scala
val ns = List(1, 2)
val os = List(4, 5)
val qs = for (n <- ns; o <- os) yield n * o
assert (qs == List (1*4, 1*5, 2*4, 2*5))
```

這個 "for" 可以讀作 "for [each] n [in] ns [and each] o [in] os yield n * o"。格式有點巢狀迴圈的感覺，但它是用 `map` 跟 `flatMap` 做出來的。

```scala
val qs = ns flatMap {n => os map {o => n * o }}
```

這值得花些時間理解如何辦到的，以下是推導過程

```scala
val qs = ns flatMap {n => os map {o => n * o}}
```
```scala
val qs = ns flatMap {n => List(n * 4, n * 5)}
```
```scala
val qs = List(1 * 4, 1 * 5, 2 * 4, 2 * 5)
```

## 更多表達式 (Now With More Expression)

讓我們再進階一點。

```scala
val qs = for (n <- ns; o <- os; p <- ps) yield n * o * p
```

這個 "for" 被展開成

```scala
val qs = ns flatMap {n =>
  os flatMap {o =>
    ps map {p => n * o * p}
  }
}
```

看起來頗像之前的 "for"，因為規則是遞歸套用

```scala
for(x1 <- expr1; ...; x <- expr) yield resultExpr
```

被展開成

```scala
expr1 flatMap {x1 =>
  for(...; x <- expr) yield resultExpr
}
```

這規則被重複應用直到剩下一個表示式，然後套用 `map` 在這個表示式上。以下是 "val qs = for..." 被編譯器展開的過程

```scala
val qs = for (n <- ns; o <- os; p <- ps) yield n * o * p
```
```scala
val qs = ns flatMap {n => 
  for(o <- os; p <- ps) yield n * o * p
}
```
```scala
val qs = ns flatMap {n =>
  os flatMap {o => 
    for(p <- ps) yield n * o * p
  }
}
```
```scala
val qs = ns flatMap {n =>
  os flatMap {o =>
    ps map {p => n * o * p}
  }
}
```

## 命令式的 "for" (An Imperative "For")

"for" 也有命令式的版本，讓你在某些情況呼叫有副作用 (side effect) 的函數。使用上只要去掉 `yield` 即可

```scala
val ns = List(1, 2)
val os = List(4, 5)
for (n <- ns; o <- os) println(n * o)
```

這個表示式規則很像有 `yield` 的版本，但是用 `foreach` 取代 `flatMap` 或 `map`。

```scala
ns foreach {n => os foreach {o => println(n * o)}}  
```

如果不需使用命令式的 "for"，你就不必要實作 `foreach` 函數。有沒有 `foreach` 不是那麼重要，因為我們已經有 `map`。

```scala
class M[A] {
  def map[B](f: A=> B) : M[B] = ...
  def flatMap[B](f: A => M[B]) : M[B] = ...
  def foreach[B](f: A=> B) : Unit = {
    map(f)
    ()
  }
}
```

換句話說，使用 `foreach` 可以直接呼叫 `map` 然後把結果丟掉就好，雖然這不是最有效率的做法，所以 Scala 允許你用自己的方式定義 `foreach`。

## 過濾式的 "for" (Filtering "For")

到目前為止，Monad 只建立在少數關鍵的概念上。三個方法 - `map`、`flatMap`、`foreach` - 幾乎辦到所有 "for" 能做的事。

Scala "for" 還有一個特色："if" 看守者 (guard)。例如

```scala
val names = List("Abe", "Beth", "Bob", "Mary")
val bNames = for (bName <- names;
  if bName(0) == 'B'
) yield bName + " is a name starting with B"

assert(bNames == List(
  "Beth is a name starting with B",
  "Bob is a name starting with B"))
```

"if" 看守者對應到 `filter` 方法。`filter` 接受斷言函式 (predicate function 為接受一個參數並傳回 `ture` 或 `false` 的函數) 而後產生一個新的 Monad，這個 Monad 不包含不符合斷言函數的元素。上述的 "for" 陳述 (statement) 被翻譯成下面這樣。

```scala
val bNames = (names filter {bName => bName(0) == 'B'}).map
    {bName => bName + " is a name starting with B"}
```

首先，`list` 裡的名字開頭有 'B' 的被過濾出來，然後被過濾的 `list` 使用一個函數映射，在名字後面加上 " is a name..."

不是所有的 Monad 都能被過濾。用容器類比，過濾可能移除所有元素，而有些容器不能是空的。對這些 Monad 你不需要產生 `filter` 方法，只要你在 "for" 表達式裡沒有使用 "if"，Scala 就不會抱怨。

我會在下個部分多聊點 `filter`，有關如何在 Monad 定義它，哪些 Monad 不能被過濾等等。

## 第二部分結論

"For" is a handy way to work with monads. 
Its syntax is particularly useful for working with Lists and other collections.
But "for" is more general than that. 
It expands into map, flatMap, foreach, and filter. 
Of those, map and flatMap should be defined for any monad. 
The foreach method can be defined if you want the monad to be used imperatively and it's trivial to build. 
Filter can be defined for some monads but not for others.



"m map f" can be implemented as "m flatMap {x => unit(x)}. 
"m foreach f" can be implemented in terms of map, or in terms of flatMap "m flatMap {x => unit(f(x));()}. 
Even "m filter p" can be implemented using flatMap (I'll show how next time). 
flatMap really is the heart of the beast.


Remember, monads are elephants. 
The picture I've painted of monads so far emphasizes collections.
In part 4, I'll present a monad that isn't a collection and only a container in an abstract way.
But before part 4 can happen, part 3 needs to cover some properties that are true of all monads: the monadic laws.

In the mean time, here's a cheat sheet showing how Haskell's do and Scala's for are related.

<table>
  <tr>
    <th>Haskell</th>
    <th>Scala</th>
  </tr>
  <tr>
    <td><pre>do var1<- expn1
   var2 <- expn2
   expn3</pre></td>
    <td><pre>for {var1 <- expn1;
   var2 <- expn2;
   result <- expn3
} yield result</pre></td>
  </tr>
  <tr>
    <td><pre>do var1 <- expn1
   var2 <- expn2
   return expn3</pre></td>
    <td><pre>for {var1 <- expn1;
   var2 <- expn2;
} yield expn3</pre></td>
  </tr>
    <td><pre>do var1 <- expn1 >> expn2
   return expn3</pre></td>
    <td><pre>for {_ <- expn1;
   var1 <- expn2
} yield expn3</pre></td>
  <tr>
  </tr> 
</table>

## 腳注

1. <a name="footnote1"></a> Scala 規格書實際規定 "for" 要使用模式匹配 (pattern matching) 來展開。基本上，真正的規格會把我在這裡講的規則展開，允許模式放在 `<-` 的左邊。這部分講太多會模糊這篇文章的焦點。
