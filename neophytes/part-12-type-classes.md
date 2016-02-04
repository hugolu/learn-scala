# Type Classes

## 問題

觀察以下物件
```scala
object Statistics {
  def median(xs: Vector[Double]): Double = xs(xs.size/2)
  def quartiles(xs: Vector[Double]): (Double, Double, Double) = (xs(xs.size/4), median(xs), xs(xs.size/4*3))
  def iqr(xs: Vector[Double]): Double = quartiles(xs) match {
    case (firstQuartile, _, thirdQuartile) => thirdQuartile - firstQuartile
  }
  def mean(xs: Vector[Double]): Double = {xs.reduce(_+_)/xs.size}
}
```

這個物件的方法只能接受```Vector[Double]```型別的參數，如果想要產生一個類似的物件處理```Vector[Int]```，最蠢的方式是copy&past，弄一個類似的物件，但是程式碼重複性太高，也不利將來維護的工作。

Ruby 怎麼做？他使用 *monkey patching* 的方式來擴充類別，但是會污染global namespace。

Java 開發者可能會用 *Adapter pattern* 來擴出類別。

現在弄一個 ```trait NumberLike[A]``` 來擴充對 ```Number``` 類別的支援： ```NumberLikeDouble``` 或 ```NumberLikeInt```。
```scala
trait NumberLike[A] {
  def get: A
  def plus(y: NumberLike[A]): NumberLike[A]
  def minus(y: NumberLike[A]): NumberLike[A]
  def divide(y: Int): NumberLike[A]
}

case class NumberLikeDouble(x: Double) extends NumberLike[Double] {
  def get: Double = x
  def minus(y: NumberLike[Double]) = NumberLikeDouble(x - y.get)
  def plus(y: NumberLike[Double]) = NumberLikeDouble(x + y.get)
  def divide(y: Int) = NumberLikeDouble(x / y)
}

case class NumberLikeInt(x: Int) extends NumberLike[Int] {
  def get: Int = x
  def minus(y: NumberLike[Int]) = NumberLikeInt(x - y.get)
  def plus(y: NumberLike[Int]) = NumberLikeInt(x + y.get)
  def divide(y: Int) = NumberLikeInt(x / y)
}

val d1 = NumberLikeDouble(3.14)
val d2 = NumberLikeDouble(2.11)
d1.minus(d2)
d1.plus(d2)
d1.divide(2)

val i1 = NumberLikeInt(3)
val i2 = NumberLikeInt(2)
i1.minus(i2)
i1.plus(i2)
i1.divide(2)
```

然後再讓 ```Statistics``` 操作 ```NumberLike``` 的子類別。
```scala
object Statistics {
  type Quartile[A] = (NumberLike[A], NumberLike[A], NumberLike[A])
  def median[A](xs: Vector[NumberLike[A]]): NumberLike[A] = xs(xs.size / 2)
  def quartiles[A](xs: Vector[NumberLike[A]]): Quartile[A] =
    (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
  def iqr[A](xs: Vector[NumberLike[A]]): NumberLike[A] = quartiles(xs) match {
    case (lowerQuartile, _, upperQuartile) => upperQuartile.minus(lowerQuartile)
  }
  def mean[A](xs: Vector[NumberLike[A]]): NumberLike[A] =
    xs.reduce(_.plus(_)).divide(xs.size)
}
```

將來要是有其他 ```Number``` 衍生的子類別，只要用```NumberLikeXXX``` 把這個子類別包裝起來(adapter pattern)，就能用個這個 ```Statistics``` 操作。

雖然這個 adapter 沒做什麼事，但是每次有新的延伸子類別還是要做一次工，實在太瑣碎了 (摔筆

## 讓 Type classes 來拯救你！

type class ```C``` 定義一些能夠操作成員變數型別為 ```T``` 的方法，這個成員變數型別 ```T``` 不必繼承 ```C``` 這個類別。
一旦 ```T``` 成為 ```C``` 的成員，某些類別方法勢必要能接受 ```T``` 做為參數。

使用 *type classes* 的程式碼對擴充開放 (open to extension)，不需要用 adapter。

```scala
object Math {
  import annotation.implicitNotFound
  @implicitNotFound("No member of type class NumberLike in scope for ${T}")
  trait NumberLike[T] {
    def plus(x: T, y: T): T
    def divide(x: T, y: Int): T
    def minus(x: T, y: T): T
  }
  object NumberLike {
    implicit object NumberLikeDouble extends NumberLike[Double] {
      def plus(x: Double, y: Double): Double = x + y
      def divide(x: Double, y: Int): Double = x / y
      def minus(x: Double, y: Double): Double = x - y
    }
    implicit object NumberLikeInt extends NumberLike[Int] {
      def plus(x: Int, y: Int): Int = x + y
      def divide(x: Int, y: Int): Int = x / y
      def minus(x: Int, y: Int): Int = x - y
    }
  }
}

object Statistics {
  import Math.NumberLike
  def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
    ev.divide(xs.reduce(ev.plus(_, _)), xs.size)
}

val ints = Vector[Int](1,2,3,4,5)
Statistics.mean(ints)
//res12: Int = 3

val doubles = Vector[Double](1.0, 2.0, 3.0, 4.0, 5.0)
Statistics.mean(doubles)
//res13: Double = 3.0

val strings = Vector[String]("1", "2", "3", "4", "5")
Statistics.mean(strings)
//<console>:18: error: No member of type class NumberLike in scope for String
```
