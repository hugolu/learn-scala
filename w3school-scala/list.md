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
