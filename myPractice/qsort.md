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

```scala
def qsort[T <% Ordered[T]](list: List[T]): List[T] = list match {
  case Nil => Nil
  case x :: xs =>
    val (before, after) = xs partition (_ < x)
    qsort(before) ++ (x :: qsort(after));
}                                               //> qsort: [T](list: List[T])(implicit evidence$2: T => Ordered[T])List[T]

val list = List(5, 7, 9, 2, 4, 6, 3, 1, 10, 8)  //> list  : List[Int] = List(5, 7, 9, 2, 4, 6, 3, 1, 10, 8)
qsort(list)                                     //> res0: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
- 使用View Bound ```[T <% Ordered[T]]```，將```T```限制為可以比較大小的型別```Ordered[T]```
