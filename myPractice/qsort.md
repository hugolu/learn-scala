# Quick Sort

演算法：http://notepad.yehyeh.net/Content/Algorithm/Sort/Quick/Quick.php

快速排序作法：

1. 選定一個基準值(Pivot)
2. 將比基準值(Pivot)小的數值移到基準值左邊，形成左子串列
3. 將比基準值(Pivot)大的數值移到基準值右邊，形成右子串列
4. 分別對左子串列、右子串列作上述三個步驟 ⇒ 遞迴(Recursive)
  - 直到左子串列或右子串列只剩一個數值或沒有數值

分割(Partition)：將數列依基準值分成三部份(快速排序作法中，第2,3步驟)

- 左子數列：比基準值小的數值
- 中子數列：基準值
- 右子數列：比基準值大的數值

## 基本功能

```scala
def qsort(list: List[Int]): List[Int] = list match {
  case Nil => Nil
  case x :: xs =>
    val (before, after) = xs.partition(_ < x)
    qsort(before) ++ (x :: qsort(after))
}                                               //> qsort: (list: List[Int])List[Int]

val ints = List(1, 3, 5, 6, 4, 2)               //> ints  : List[Int] = List(1, 3, 5, 6, 4, 2)
qsort(ints)                                     //> res0: List[Int] = List(1, 2, 3, 4, 5, 6)
```
- 只能針對`List[Int]`排序


## 擴充功能

```scala
def qsort[T <: Ordered[T]](list: List[T]): List[T] = list match {
case Nil => Nil
case x :: xs =>
val (before, after) = xs.partition(_ < x)
qsort(before) ++ (x :: qsort(after))
}                                               //> qsort: [T <: Ordered[T]](list: List[T])List[T]

class Num(val x: Int) extends Ordered[Num] {
override def toString = x.toString
def compare(that: Num) = this.x - that.x
}
object Num {
def apply(x: Int) = new Num(x)
}

val nums = List(Num(1), Num(3), Num(5), Num(6), Num(4), Num(2))
                                          //> nums  : List[week8.test6.Num] = List(1, 3, 5, 6, 4, 2)
qsort(nums)                                     //> res0: List[week8.test6.Num] = List(1, 2, 3, 4, 5, 6)

val ints = List(1, 3, 5, 6, 4, 2)
qsort(ints)
//error: inferred type arguments [Int] do not conform to method qsort's type parameter bounds [T <: Ordered[T]]
```
- `qsort`可以針對實作`Ordered[T]`的物件進行排序
- `qsort`無法針對`Int`進行排序

## 使用 View


```scala
def qsort[T <% Ordered[T]](list: List[T]): List[T] = list match {
  case Nil => Nil
  case x :: xs =>
    val (before, after) = xs.partition(_ < x)
    qsort(before) ++ (x :: qsort(after))
}                                               //> qsort: [T](list: List[T])(implicit evidence$1: T => Ordered[T])List[T]

class Num(val x: Int) extends Ordered[Num] {
  override def toString = x.toString
  def compare(that: Num) = this.x - that.x
}
object Num {
  def apply(x: Int) = new Num(x)
}

val nums = List(Num(1), Num(3), Num(5), Num(6), Num(4), Num(2))
                                                //> nums  : List[week8.test6.Num] = List(1, 3, 5, 6, 4, 2)
qsort(nums)                                     //> res1: List[week8.test6.Num] = List(1, 2, 3, 4, 5, 6)

val ints = List(1, 3, 5, 6, 4, 2)               //> ints  : List[Int] = List(1, 3, 5, 6, 4, 2)
qsort(ints)                                     //> res1: List[Int] = List(1, 2, 3, 4, 5, 6)
```
- 使用View Bound ```[T <% Ordered[T]]```，將```T```限制為可以比較大小的型別```Ordered[T]```
- 使用`T<% Ordered[T]`的qsort，其型態參數讀法是：T可以是任何可被視為Ordered[T]型態的物件
- 因為`Int`並沒有`scala.Ordered [A]`特徵。當試圖使用如`1 < 2`這樣的比較時，會透過隱式轉換（Implicit conversion）將`Int`的1轉換為`RichInt`來包裹1，然後使用其`<`方法來取得比較結果。
