# Option

```scala
val x = Some(3): Option[Int]                  //> x  : Option[Int] = Some(123)
val y = None: Option[Int]                       //> y  : Option[Int] = None
```

## get the value in `Option`
```scala
def fun1(o: Option[Int]): Int = {
  o.getOrElse(0)
}                                               //> fun1: (o: Option[Int])Int
fun1(x)                                         //> res0: Int = 3
fun1(y)                                         //> res1: Int = 0

def fun2(o: Option[Int]): Unit = {
  o.foreach(println)
}                                               //> fun2: (o: Option[Int])Unit
fun2(x)                                         //> 3
fun2(y)

def fun3(o: Option[Int]): Int = o match {
  case Some(i) => i
  case None    => 0
}                                               //> fun3: (o: Option[Int])Int
fun3(x)                                         //> res2: Int = 3
fun3(y)                                         //> res3: Int = 0
```

## treat `Option` as a collection
```scala
def fun4(o: Option[Int]): Option[Array[Int]] = {
  o.map(i => Array.range(0, i))
}                                               //> fun4: (o: Option[Int])Option[Array[Int]]
fun4(x)                                         //> res4: Option[Array[Int]] = Some([I@75be8fe4)
fun4(y)                                         //> res5: Option[Array[Int]] = None

def fun5(o: Option[Int]): Array[Int] = {
  o.map(i => Array.range(0, i)).getOrElse(Array[Int]())
}                                               //> fun5: (o: Option[Int])Array[Int]
fun5(x)                                         //> res6: Array[Int] = Array(0, 1, 2)
fun5(y)                                         //> res7: Array[Int] = Array()

def fun6(o: Option[Int]): Option[Array[Int]] = {
  o.flatMap(i => Some(Array.range(0, i)))
}                                               //> fun6: (o: Option[Int])Option[Array[Int]]
fun6(x)                                         //> res8: Option[Array[Int]] = Some([I@2a5c5879)
fun6(y)                                         //> res9: Option[Array[Int]] = None

def fun7(o: Option[Int]): Option[Int] = {
  o.filter(_ > 2)
}                                               //> fun7: (o: Option[Int])Option[Int]
fun7(x)                                         //> res10: Option[Int] = Some(3)
fun7(y)                                         //> res11: Option[Int] = None

def fun8(o: Option[Int]): Option[Int] = {
  o.filter(_ < 2)
}                                               //> fun8: (o: Option[Int])Option[Int]
fun8(x)                                         //> res12: Option[Int] = None
fun8(y)                                         //> res13: Option[Int] = None
```

## for comprehension
```scala
val o1 = Some(3): Option[Int]                   //> o1  : Option[Int] = Some(3)
val o2 = Some(7): Option[Int]                   //> o2  : Option[Int] = Some(7)
val o3 = None: Option[Int]                      //> o3  : Option[Int] = None

def fun9(ox: Option[Int], oy: Option[Int]): Option[Int] =
  for {
    x <- ox
    y <- oy
  } yield x * y                                 //> fun9: (ox: Option[Int], oy: Option[Int])Option[Int]
fun9(o1, o2)                                    //> res14: Option[Int] = Some(21)
fun9(o1, o3)                                    //> res15: Option[Int] = None
```
