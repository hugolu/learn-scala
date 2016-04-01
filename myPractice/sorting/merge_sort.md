# Merge Sort

## Reference
- [合併排序](https://zh.wikipedia.org/wiki/%E5%BD%92%E5%B9%B6%E6%8E%92%E5%BA%8F)

## 遞歸法
原理如下（假設序列共有n個元素）：

1. 將序列每相鄰兩個數字進行歸併操作，形成floor(n/2)個序列，排序後每個序列包含兩個元素
2. 將上述序列再次歸併，形成floor(n/4)個序列，每個序列包含四個元素
3. 重複步驟2，直到所有元素排序完畢

```scala
val list = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)  //> list  : List[Int] = List(2, 4, 6, 8, 10, 9, 7, 5, 3, 1)

def msort(list: List[Int]): List[Int] = {
  val n = list.length / 2
  if (n == 0) list
  else {
    val (fst, snd) = list.splitAt(n)
    merge(msort(fst), msort(snd))
  }
}                                               //> msort: (list: List[Int])List[Int]
def merge(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match {
  case (xs, Nil)            => xs
  case (Nil, ys)            => ys
  case (x :: xs1, y :: ys1) => if (x < y) x :: merge(xs1, ys) else y :: merge(xs, ys1)
}                                               //> merge: (xs: List[Int], ys: List[Int])List[Int]

msort(list)                                     //> res0: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
