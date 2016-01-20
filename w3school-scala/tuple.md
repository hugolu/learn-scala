# Tuple

參考資料
 - https://wizardforcel.gitbooks.io/w3school-scala/content/18.html

与列表一样，元组也是不可变的，但与列表不同的是元组可以包含不同类型的元素。

```scala
// construction
scala> val tuple = (1, 3.14, "scala")
tuple: (Int, Double, String) = (1,3.14,scala)

scala> val tuple = Tuple3(1, 3.14, "scala")
tuple: (Int, Double, String) = (1,3.14,scala)

scala> val tuple = new Tuple3(1, 3.14, "scala")
tuple: (Int, Double, String) = (1,3.14,scala)

// access each element
scala> println(tuple._1, tuple._2, tuple._3)
(1,3.14,scala)

// iteration
scala> tuple.productIterator.foreach(println)
1
3.14
scala

// toSting
scala> tuple.toString
res7: String = (1,3.14,scala)

// swap two elements of Tuple2
scala> val tuple2 = ("hello", "world")
tuple2: (String, String) = (hello,world)

scala> tuple2.swap
res9: (String, String) = (world,hello)
```
