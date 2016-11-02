# implicitly

參考：Programming Scala, 2nd Edition

[object Predef](http://www.scala-lang.org/api/current/#scala.Predef$) 定義了一個名為 `implicitly` 的方法。

```scala
import math.Ordering

case class MyList[A](list: List[A]) {
  def sortBy1[B](f: A => B)(implicit ord: Ordering[B]): List[A] = list.sortBy(f)(ord)
  def sortBy2[B: Ordering](f: A => B): List[A] = list.sortBy(f)(implicitly[Ordering[B]])
}

val list = MyList(List(1,3,5,2,4))

list.sortBy1(i => -i)   //> List(5, 4, 3, 2, 1)
list.sortBy2(i => -i)   //> List(5, 4, 3, 2, 1)
```

`sortBy1` 接受一個額外的類型為 `Ordering[B]` 的隱式值作為其輸入。調用方法時，在當前作用域中一定存在某一個 `Ordering[B]` 的對象實例，該實例清楚知道如何對我們所需要的 `B` 類型對象進行排序。我們認為 `B` 的界限被上下文所限定，上下文限定了 `B` 對實力進行排序的能力。

Scala 提供了一個簡化版的語法 `sortBy2`。類型參數 `B: Ordering` 被稱為上下文定界 上下文定界，他暗指第二個參數列表 (也就是哪個隱式參數列表) 將接受 `Ordering[B]` 實例。

不過。我們能需要在方法中訪問 `Ordering` 對象實例。由於在 source code 中不再明確聲明 `Ordering` 對象實例，因次這個實例沒有自己的名稱。針對這個現象提供 `Predef.implicitly` 方法傳給函數所有標記為隱式參數的實例進行解析。`implicitly` 方法接受的是 `Ordering[B]` 的類型簽名。

當我們需要類型為**參數化類型的隱式參數**時，當類型參數屬於當前作用域的其他一些類型時 (例如：`[B: Ordering]` 代表了類型為 `Ordering[B]` 的隱式參數)，可以將上下文定界 (context bound) 與 `implicitly` 方法結合其來，以簡潔的方式解決這個問題。

```scala
case class Box(size: Int)

implicit object BoxOrdering extends Ordering[Box] {
  override def compare(x: Box, y: Box) = x.size - y.size
  override def toString = "BoxOrdering"
}

val list = List(Box(1), Box(3), Box(5), Box(4), Box(2))

list.sortBy(b => b)
list.sortBy(b => b)(BoxOrdering)
list.sortBy(b => b)(implicitly[Ordering[Box]])
```
