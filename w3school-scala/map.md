# Map (映射)

參考連結 https://wizardforcel.gitbooks.io/w3school-scala/content/17.html

- Map(映射)是一种可迭代的键值对（key/value）结构。
- 所有的值都可以通过键来获取。
- Map 中的键都是唯一的。
- Map 也叫哈希表（Hash tables）。

```scala
// empty map, key: Char, value: Int
scala> var A:Map[Char,Int] = Map()
A: Map[Char,Int] = Map()

scala> A += ('I' -> 1)
scala> A += ('J' -> 5)
scala> A += ('K' -> 10)
scala> A += ('L' -> 100)

scala> A
res4: Map[Char,Int] = Map(I -> 1, J -> 5, K -> 10, L -> 100)

// get keys
scala> A.keys
res5: Iterable[Char] = Set(I, J, K, L)

// get values
scala> A.values
res6: Iterable[Int] = MapLike(1, 5, 10, 100)

// isEmpty
scala> A.isEmpty
res7: Boolean = false

scala> Map().isEmpty
res8: Boolean = true
```
```
