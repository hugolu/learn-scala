# Quick Sort

## Reference
- [快速排序](https://zh.wikipedia.org/wiki/%E5%BF%AB%E9%80%9F%E6%8E%92%E5%BA%8F)

## 演算法
快速排序使用分治法（Divide and conquer）策略來把一個序列（list）分為兩個子序列（sub-lists）。步驟為：

1. 從數列中挑出一個元素，稱為"基準"（pivot），
2. 重新排序數列，所有元素比基準值小的擺放在基準前面，所有元素比基準值大的擺在基準的後面（相同的數可以到任一邊）。在這個分割結束之後，該基準就處於數列的中間位置。這個稱為分割（partition）操作。
3. 遞迴地（recursive）把小於基準值元素的子數列和大於基準值元素的子數列排序。

```
function quicksort(q)
  var list less, pivotList, greater
  if length(q) ≤ 1 {
    return q
  } else {
    select a pivot value pivot from q
    for each x in q except the pivot element
      if x < pivot then add x to less
      if x ≥ pivot then add x to greater
    add pivot to pivotList
    return concatenate(quicksort(less), pivotList, quicksort(greater))
  }
}
```

### Scala code
```scala
val nums = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)  //> nums  : List[Int] = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)

def qsort(list: List[Int]): List[Int] = {
  if (list.length <= 1) list
  else {
    val pivot = list.head
    val small = list.filter(_ < pivot)
    val large = list.filter(_ > pivot)

    qsort(small) ::: List(pivot) ::: qsort(large)
  }
}                                               //> qsort: (list: List[Int])List[Int]

qsort(nums)                                     //> res0: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
- 效能？有更好的解法嗎

### 更優雅的解法
內容來自 https://github.com/hugolu/learn-scala/blob/master/myPractice/qsort.md

```scala
val nums = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)  //> nums  : List[Int] = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)

def qsort(list: List[Int]): List[Int] = list match {
	case Nil => Nil
	case x :: xs =>
		val (small, large) = xs.partition(_ < x)
		qsort(small) ::: (x :: qsort(large))
}                                               //> qsort: (list: List[Int])List[Int]

qsort(nums)                                     //> res0: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
