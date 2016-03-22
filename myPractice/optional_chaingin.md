# Optional Chaining

```scala
  case class X(value: Option[Y])
  case class Y(value: Option[Z])
  case class Z(value: Int)

  val x0: Option[X] = None                        //> x0  : Option[myTest.test90.X] = None
  val x1: Option[X] = Some(X(None))               //> x1  : Option[myTest.test90.X] = Some(X(None))
  val x2: Option[X] = Some(X(Some(Y(None))))      //> x2  : Option[myTest.test90.X] = Some(X(Some(Y(None))))
  val x3: Option[X] = Some(X(Some(Y(Some(Z(123))))))
                                                  //> x3  : Option[myTest.test90.X] = Some(X(Some(Y(Some(Z(123))))))

  def get(x: Option[X]): Option[Int] = {
    if (x.isDefined) {
      val y = x.get.value
      if (y.isDefined) {
        val z = y.get.value
        if (z.isDefined) {
          val v = z.get.value
          Some(v)
        } else None
      } else None
    } else None
  }                                               //> get: (x: Option[myTest.test90.X])Option[Int]

  get(x0)                                         //> res0: Option[Int] = None
  get(x1)                                         //> res1: Option[Int] = None
  get(x2)                                         //> res2: Option[Int] = None
  get(x3)                                         //> res3: Option[Int] = Some(123)
```

`get()` is so guly, which looks like "Pyramid of doom".
I wish it could be refactored to a optional chaining as swift can do. 
It's a open question to me. Can anyone answer it?

## First Try
```scala
case class X(value: Option[Y])
case class Y(value: Option[Z])
case class Z(value: Int)

val x0: Option[X] = None                        //> x0  : Option[myTest.test92.X] = None
val x1 = Some(X(None))                          //> x1  : Some[myTest.test92.X] = Some(X(None))
val x2 = Some(X(Some(Y(None))))                 //> x2  : Some[myTest.test92.X] = Some(X(Some(Y(None))))
val x3 = Some(X(Some(Y(Some(Z(123))))))         //> x3  : Some[myTest.test92.X] = Some(X(Some(Y(Some(Z(123))))))

def ifSomeDo[T, U](f: T => Option[U], x: Option[T]): Option[U] = if (x.isDefined) f(x.get) else None
                                                //> ifSomeDo: [T, U](f: T => Option[U], x: Option[T])Option[U]

def zGet(z: Z) = Some(z.value)                  //> zGet: (z: myTest.test92.Z)Some[Int]
def yGet(y: Y) = ifSomeDo(zGet, y.value)        //> yGet: (y: myTest.test92.Y)Option[Int]
def xGet(x: X) = ifSomeDo(yGet, x.value)        //> xGet: (x: myTest.test92.X)Option[Int]
def get(x: Option[X]) = ifSomeDo(xGet, x)       //> get: (x: Option[myTest.test92.X])Option[Int]

get(x0)                                         //> res0: Option[Int] = None
get(x1)                                         //> res1: Option[Int] = None
get(x2)                                         //> res2: Option[Int] = None
get(x3)                                         //> res3: Option[Int] = Some(123)
```
- How to chain them??
