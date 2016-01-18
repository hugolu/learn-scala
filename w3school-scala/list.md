# List

## 元素類型 ```List[T]```

```scala
//字串列表
scala> val strings = List("apple", "banana", "carrot")
strings: List[String] = List(apple, banana, carrot)

//數字列表
scala> val numbers = List(1, 2, 3)
numbers: List[Int] = List(1, 2, 3)

// 空列表
scala> val empty = List()
empty: List[Nothing] = List()

// 2維陣列
scala> val matrix = List(List(1,2,3), List(4,5,6), List(7,8,9))
matrix: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))
```

## 建構列表

```scala
// 空列表也可用```Nil```表示
scala> val empty = Nil
empty: scala.collection.immutable.Nil.type = List()

// 元素連接列表使用 ```::```
scala> val strings = "apple" :: ("banana" :: ("carrot" :: Nil))
strings: List[String] = List(apple, banana, carrot)

// 混合內容的列表型態為```Any```
scala> val list = "apple" :: (1 :: Nil)
list: List[Any] = List(apple, 1)

scala> list(0).getClass
res0: Class[_] = class java.lang.String

scala> list(1).getClass
res1: Class[_] = class java.lang.Integer
```

## 基本操作

```scala
scala> val list = List("apple", "banana", "carrot")
list: List[String] = List(apple, banana, carrot)

// 取出列表第一個元素
scala> list.head
res5: String = apple

// 取出第一個元素之後的元素，回傳```List[T]```
scala> list.tail
res6: List[String] = List(banana, carrot)

// 判斷是否為空列表
scala> list.isEmpty
res7: Boolean = false

scala> Nil.isEmpty
res8: Boolean = true
```

## 連接列表

```scala
scala> val list1 = List("apple", "banana", "carrot")
list1: List[String] = List(apple, banana, carrot)

scala> val list2 = List("durian", "eggfruit", "fig")
list2: List[String] = List(durian, eggfruit, fig)

// 使用 :::
scala> list1 ::: list2
res11: List[String] = List(apple, banana, carrot, durian, eggfruit, fig)

// 使用 .:::(), 在列表开头添加指定列表的元素 => 不直覺
scala> list1.:::(list2)
res12: List[String] = List(durian, eggfruit, fig, apple, banana, carrot)

scala> list2.:::(list1)
res13: List[String] = List(apple, banana, carrot, durian, eggfruit, fig)

// 使用 List.concat() 方法
scala> List.concat(list1, list2)
res14: List[String] = List(apple, banana, carrot, durian, eggfruit, fig)
```

## 創建列表
```scala
val list = List("apple", "banana", "carrot")

// 使用 List.fill() 創建重複數量的元素列表
scala> List.fill(3)("hello")
res16: List[String] = List(hello, hello, hello)

// 使用 List.tabulate() 給定函式來創建列表
// 一維列表
scala> List.tabulate(6)(n => n * n)
res17: List[Int] = List(0, 1, 4, 9, 16, 25)

// 二為列表
scala> List.tabulate(3,3)((x,y) => (x+1)*(y+1))
res18: List[List[Int]] = List(List(1, 2, 3), List(2, 4, 6), List(3, 6, 9))
```
___
# 進階內容

參考[scala.collection.immutable List](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List)

| def | explanation |
|-----|-------------|
|```+:(elem: A): List[A]```| A copy of the list with an element prepended. |
|```:+(elem: A): List[A]```| A copy of this list with an element appended. |
```scala
scala> val list = List(1,2,3)
list: List[Int] = List(1, 2, 3)

scala> 0 +: list
res0: List[Int] = List(0, 1, 2, 3)

scala> list :+ 4
res1: List[Int] = List(1, 2, 3, 4)
```

| def | explanation |
|-----|-------------|
|```++[B](that: GenTraversableOnce[B]): List[B]```| Returns a new list containing the elements from the left hand operand followed by the elements from the right hand operand. |
|```++:[B >: A, That](that: collection.Traversable[B]): That```|As with ++, returns a new collection containing the elements from the left operand followed by the elements from the right operand.|
|```++:[B](that: TraversableOnce[B]): List[B]```|As with ++, returns a new collection containing the elements from the left operand followed by the elements from the right operand.|
```scala
scala> List(1,2) ++ List(3,4)
res8: List[Int] = List(1, 2, 3, 4)

scala> List(1,2) ++: Array(3,4)
res9: Array[Int] = Array(1, 2, 3, 4)

scala> Array(1,2) ++: List(3,4)
res10: List[Int] = List(1, 2, 3, 4)
```

| def | explanation |
|-----|-------------|
|```/:[B](z: B)(op: (B, A) ⇒ B): B```|Applies a binary operator to a start value and all elements of this traversable or iterator, going left to right. Process: ```op(...op(op(z, x_1), x_2), ..., x_n)```|
|```:\[B](z: B)(op: (A, B) ⇒ B): B```|Applies a binary operator to all elements of this traversable or iterator and a start value, going right to left. Process: ```op(x_1, op(x_2, ... op(x_n, z)...))```|
```scala
// initValue=0, (((0 + 1) + 2) + 3) = 6
scala> (0 /: List(1,2,3))(_+_)
res19: Int = 6

// initValue=4, (1 + (2 + (3 + 4))) = 10
scala> (List(1,2,3) :\ 4)(_+_)
res20: Int = 10
```

| def | explanation |
|-----|-------------|
|```addString(b: StringBuilder): StringBuilder``` | Appends all elements of this traversable or iterator to a string builder. |
|```addString(b: StringBuilder, sep: String): StringBuilder```|Appends all elements of this traversable or iterator to a string builder using a separator string.|
|```addString(b: StringBuilder, start: String, sep: String, end: String): StringBuilder```|Appends all elements of this traversable or iterator to a string builder using start, end, and separator strings.|
```
scala> val a = List(1,2,3,4)
a: List[Int] = List(1, 2, 3, 4)

scala> val b = new StringBuilder()
b: StringBuilder =

scala> val c = a.addString(b)
c: StringBuilder = 1234

scala> c.toString
res0: String = 1234

scala> val d = a.addString(b, ", ")
d: StringBuilder = 1, 2, 3, 4

scala> d.toString
res1: String = 1, 2, 3, 4

scala> val e = a.addString(b, "List(", ", ", ")")
e: StringBuilder = List(1, 2, 3, 4)

scala> e.toString
res2: String = List(1, 2, 3, 4)
```
