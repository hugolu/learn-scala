# Map (映射)

參考連結
 - https://wizardforcel.gitbooks.io/w3school-scala/content/17.html
 - http://www.scala-lang.org/api/current/index.html#scala.collection.Map

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

## ```++``` 合併

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

## ```get```
- Optionally returns the value associated with a key.

```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")

scala> colors("red")
res20: String = #FF0000

scala> colors("black")
java.util.NoSuchElementException: key not found: black

scala> colors.get("red")
res22: Option[String] = Some(#FF0000)

scala> colors.get("black")
res23: Option[String] = None
```

## ```iterator```
- Creates a new iterator over all key/value pairs of this map

```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")

scala> val iter = colors.iterator
iter: Iterator[(String, String)] = non-empty iterator

scala> while (iter.hasNext) { println(iter.next) }
(red,#FF0000)
(azure,#F0FFFF)
(peru,#CD853F)
```

## ```+```
- ```+(kvs: (A, B)*): Map[A, B]```
  - Adds key/value pairs to this map, returning a new map. 
- ```+(kv: (A, B)): Map[A, B]```
  - Adds a key/value pair to this map, returning a new map.

```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")
colors: scala.collection.immutable.Map[String,String] = Map(red -> #FF0000, azure -> #F0FFFF, peru -> #CD853F)

// add a k/v pair
scala> colors + ("blue" -> "#0033FF")
res27: scala.collection.immutable.Map[String,String] = Map(red -> #FF0000, azure -> #F0FFFF, peru -> #CD853F, blue -> #0033FF)

// add k/v pairs
scala> colors + (("blue" -> "#0033FF"), ("yellow" -> "#FFFF00"), ("red" -> "#FF0000"))
res29: scala.collection.immutable.Map[String,String] = Map(blue -> #0033FF, azure -> #F0FFFF, peru -> #CD853F, yellow -> #FFFF00, red -> #FF0000)
```

## ```:++```
- ```++:[B >: (A, B), That](that: Traversable[B])(implicit bf: CanBuildFrom[Map[A, B], B, That]): That```
  - As with ++, returns a new collection containing the elements from the left operand followed by the elements from the right operand.
- ```++:[B](that: TraversableOnce[B]): Map[B]```
  - As with ++, returns a new collection containing the elements from the left operand followed by the elements from the right operand.
- 總歸一句，回傳的型態為```:```那邊的型態

```scala
scala> val map1 = Map(1 -> "A", 2 -> "B", 3 -> "C")
map1: scala.collection.immutable.Map[Int,String] = Map(1 -> A, 2 -> B, 3 -> C)

scala> val map2 = Map(4 -> "D", 5 -> "E", 6 -> "F")
map2: scala.collection.immutable.Map[Int,String] = Map(4 -> D, 5 -> E, 6 -> F)

scala> val list1 = List((1, "A"), (2, "B"), (3, "C"))
list1: List[(Int, String)] = List((1,A), (2,B), (3,C))

scala> val list2 = List((4, "D"), (5, "E"), (6, "F"))
list2: List[(Int, String)] = List((4,D), (5,E), (6,F))

scala> map1 ++: list2
res0: List[(Int, String)] = List((1,A), (2,B), (3,C), (4,D), (5,E), (6,F))

scala> list1 ++: map2
res1: scala.collection.immutable.Map[Int,String] = Map(5 -> E, 1 -> A, 6 -> F, 2 -> B, 3 -> C, 4 -> D)
```

## ```-``` & ```--```
- ```-(elem1: A, elem2: A, elems: A*): Map[A, B]```
  - Creates a new collection from this collection with some elements removed.
- ```-(key: A): Map[A, B]```
  - Removes a key from this map, returning a new map.
- ```--(xs: GenTraversableOnce[A]): Map[A, B]```
  - Creates a new collection from this collection by removing all elements of another collection (Traversable).
- 總歸一句，移除 by key

```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")

scala> colors - "red"
res2: scala.collection.immutable.Map[String,String] = Map(azure -> #F0FFFF, peru -> #CD853F)

scala> colors - "blue"
res3: scala.collection.immutable.Map[String,String] = Map(red -> #FF0000, azure -> #F0FFFF, peru -> #CD853F)

scala> colors - ("red", "azure", "black")
res5: scala.collection.immutable.Map[String,String] = Map(peru -> #CD853F)

scala> colors -- List("red", "azure", "black")
res10: scala.collection.immutable.Map[String,String] = Map(peru -> #CD853F)
```

## fold
- ```/:[B](z: B)(op: (B, (A, B)) ⇒ B): B```
  - Applies a binary operator to a start value and all elements of this traversable or iterator, going left to right.
  - ```/:``` is alternate syntax for foldLeft, ```op(...op(op(z, x_1), x_2), ..., x_n)```
- ```:\[B](z: B)(op: ((A, B), B) ⇒ B): B```
  - Applies a binary operator to all elements of this traversable or iterator and a start value, going right to left.
  - ```:\``` is alternate syntax for foldRight, ```op(x_1, op(x_2, ... op(x_n, z)...))```
- 注意 ```op()``` 裡面 A, B 的位置

```scala
scala> val fruits = Map("apple" -> 100, "banana" -> 150, "carrot" -> 50)

// 計算水果總數
scala> (fruits :\ 0)((fruit, total) => fruit._2 + total)
res10: Int = 300

scala> (0 /: fruits)((total, fruit) => total + fruit._2)
res12: Int = 300
```

## ```addString```
```scala
scala> val colors = Map("red" -> "#FF0000",  "azure" -> "#F0FFFF", "peru" -> "#CD853F")
scala> val b = new StringBuilder()
scala> colors.addString(b)
res3: StringBuilder = red -> #FF0000azure -> #F0FFFFperu -> #CD853F
```

## ```aggregate```
- ```aggregate[B](z: ⇒ B)(seqop: (B, (A, B)) ⇒ B, combop: (B, B) ⇒ B): B```
 -  Aggregates the results of applying an operator to subsequent elements.

```scala
scala> val fruits = Map("apple" -> 100, "banana" -> 150, "carrot" -> 50)

// 找出有哪些水果
scala> fruits.aggregate(Set[String]())((set, fruit) => set + fruit._1, _ ++ _)
res7: scala.collection.immutable.Set[String] = Set(apple, banana, carrot)
```
- ```z``` = ```Set[String]()``` 空集合
- ```seqop``` = ```(set, fruit) => set + fruit._1``` set插入fruit的名字
- ```combop``` = ```_ ++ _``` 合併所有partition裡面的set (這個範例中只有一個partition)
- 這個範例很蠢，因為只要用```fruits.keys```就能得到一樣的答案 XD
