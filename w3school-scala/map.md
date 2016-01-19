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

## 合併
```scala
scala> val colors1 = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")
scala> val colors2 = Map("blue" -> "#0033FF", "yellow" -> "#FFFF00", "red" -> "#FF0000")

// ++ as an operator
scala> color1 ++ color2
res9: scala.collection.immutable.Map[String,String] = Map(blue -> #0033FF, azure -> #F0FFFF, peru -> #CD853F, yellow -> #FFFF00, red -> #FF0000)

// ++ as a method
scala> color1.++(color2)
res10: scala.collection.immutable.Map[String,String] = Map(blue -> #0033FF, azure -> #F0FFFF, peru -> #CD853F, yellow -> #FFFF00, red -> #FF0000)
```

## ```foreach```
```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")

scala> colors.foreach(println)
(red,#FF0000)
(azure,#F0FFFF)
(peru,#CD853F)

scala> colors.foreach(color => println("colors[" + color._1 + "]:" + color._2))
colors[red]:#FF0000
colors[azure]:#F0FFFF
colors[peru]:#CD853F
```

## ```contains```
```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")

scala> colors.contains("red")
res16: Boolean = true

scala> colors.contains("black")
res17: Boolean = false
```
