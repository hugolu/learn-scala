# Collections

## List
```scala
val list = List(1,2,3)
// list: List[Int] = List(1, 2, 3)
```

## Set
```scala
val set = Set(1,2,2,3)
// set: scala.collection.immutable.Set[Int] = Set(1, 2, 3)
```

## Tuple
```scala
val tuple = ("localhost", 80)
// tuple: (String, Int) = (localhost,80)
tuple._1
// res0: String = localhost
tupel._2
// res2: Int = 80

tuple match {
  case ("localhost", 80) => "localhost:80"
  case _ => "others"
}
// res3: String = localhost:80
```

## Map
```scala
1 -> 2
// res4: (Int, Int) = (1,2)
(1, 2)
// res5: (Int, Int) = (1,2)
Map((1,2), (3,4))
// res6: scala.collection.immutable.Map[Int,Int] = Map(1 -> 2, 3 -> 4)
```
