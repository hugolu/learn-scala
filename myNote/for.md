# For Expression

內容來自 [Principles of Reactive Programming](https://class.coursera.org/reactive-002) week1 Recap: collections

## Translation of For

The Scala compiler translates for-expressions in terms of `map`, `flatMap` and a lazy variant of `filter`.

| for-expression | translated to |
|----------------|---------------|
| `for (x <- e1) yield e2` | `e1.map(x => e2)` |
| `for (x <- e1 if f; s) yield e2` | `for (x <- e1.withFilter(x => f); s) yield e2` |
| `for (x <- e1; y <- e2; s) yield e3` | `e1.flatMap(x => for (y <- e2; s) yield e3`) |

## Exercise
```scala
val N = 5                                       //> N  : Int = 5

for {
  x <- 2 to N
  y <- 2 to x
  if (x % y == 0)
} yield (x, y)                                  //> res0: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
(2 to N).flatMap(x =>
  for {
    y <- 2 to x
    if (x % y == 0)
  } yield (x, y))                               //> res1: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))

(2 to N) flatMap (x =>
  (2 to x) withFilter (y =>
    x % y == 0) map (y => (x, y)))              //> res2: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
(2 to N) flatMap (x =>
  (2 to x) filter (y =>
    x % y == 0) map (y => (x, y)))              //> res3: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((2,2), (3,3
                                                //| ), (4,2), (4,4), (5,5))
```

----
## `filter` 與 `withFilter` 的差別

http://docs.scala-lang.org/tutorials/FAQ/yield.html

Scala 2.8 introduced a method called `withFilter`, whose main difference is that, instead of returning a new, filtered, collection, it filters on-demand.

```scala
val list = List.range(0, 3)                     //> list  : List[Int] = List(0, 1, 2)

def isEven(n: Int): Boolean = {
  println(s"$n is even?")
  n % 2 == 0
}                                               //> isEven: (n: Int)Boolean
```

```scala
val list2 = list filter isEven                  //> 0 is even?
                                                //| 1 is even?
                                                //| 2 is even?
                                                //| list2  : List[Int] = List(0, 2)
list2 foreach println                           //> 0
                                                //| 2
```
- `list2` 是透過 `filter` 得到的新的 collection

```scala
val list3 = list withFilter isEven              //> list3  : scala.collection.generic.FilterMonadic[Int,List[Int]] = scala.collection.TraversableLike$WithFilter@661c28c8

list3 foreach println                           //> 0 is even?
                                                //| 0
                                                //| 1 is even?
                                                //| 2 is even?
                                                //| 2
```
- `list3` 只是一個 `FilterMonadic`，只有透過 `foreach` 取值的時候才會真正去執行 `withFilter`

----
## for 擴展作用域與值定義
```scala
val fruits = List("Apple", "Banana", "Cherry")
//fruits: List[String] = List(Apple, Banana, Cherry)

for {
  fruit <- fruits
  upcasedFruit = fruit.toUpperCase()
} println(upcasedFruit)
//APPLE
//BANANA
//CHERRY
```

```scala
val fruits = List(Some("Apple"), None, Some("Banana"), None, Some("Cherry"))
//fruits: List[Option[String]] = List(Some(Apple), None, Some(Banana), None, Some(Cherry))

// first for:
for {
  fruitOption <- fruits
  if fruitOption != None
  fruit <- fruitOption
  upcasedFruit = fruit.toUpperCase()
} println(upcasedFruit)

// second for:
for {
  fruitOption <- fruits
  fruit <- fruitOption
  upcasedFruit = fruit.toUpperCase()
} println(upcasedFruit)

// third for:
for {
  Some(fruit) <- fruits
  upcasedFruit = fruit.toUpperCase()
} println(upcasedFruit)
```
- 這三個 for 推導式同義，第三個比前兩個優雅：只有當 `fruitOption` 是 `Some` 類型時，`Some(fruit) <- fruits` 才會成功執行並提取 `fruit`；所有操作一次完成，`None` 不再被處理。
