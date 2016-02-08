# Rational

使用```class Rational```表示有理數，裡面包含分子```numerator```、分母```denominator```，含有相關的方法
```scala
object session {
  val x = new Rational(1, 3)                      //> x  : week1.Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : week1.Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : week1.Rational = 3/2
  x.numer                                         //> res0: Int = 1
  x.denom                                         //> res1: Int = 3
  x.add(y)                                        //> res2: week1.Rational = 22/21
  x.neg                                           //> res3: week1.Rational = -1/3
  x.sub(y).sub(z)                                 //> res4: week1.Rational = -79/42
  y.add(y)                                        //> res5: week1.Rational = 70/49
  x.less(y)                                       //> res6: Boolean = true
  x.max(y)                                        //> res7: week1.Rational = 5/7
}

class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y
  override def toString = numer + "/" + denom

  def add(that: Rational) = new Rational(this.numer * that.denom + this.denom * that.numer, this.denom * that.denom)
  def neg: Rational = new Rational(-this.numer, this.denom)
  def sub(that: Rational) = add(that.neg)
  def less(that: Rational) = this.numer * that.denom < this.denom * that.numer
  def max(that: Rational) = if (this.less(that)) that else this
}
```

## Infix Notation
It is therefore possible to write
- ```r.add(s)``` 可以寫成 ```r add s```
- ```r.less(s)``` 可以寫成 ```r less s```
- ```r.max(s)``` 可以寫成 ```r max s```
```scala
object session {
  val x = new Rational(1, 3)                      //> x  : week1.Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : week1.Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : week1.Rational = 3/2
  x.numer                                         //> res0: Int = 1
  x.denom                                         //> res1: Int = 3
  x add y                                         //> res2: week1.Rational = 22/21
  x.neg                                           //> res3: week1.Rational = -1/3
  x sub y sub z                                   //> res4: week1.Rational = -79/42
  y add y                                         //> res5: week1.Rational = 70/49
  x less y                                        //> res6: Boolean = true
  x max y                                         //> res7: week1.Rational = 5/7
}
```

## Operators for Rationals
- infix operator: ```add``` 可以用 ```+``` 取代，```sub```可以用 ```-``` 取代...
- prefix operator: 要加上```unary_```，且跟後面的```:```要用空格隔開
```scala
object session {
  val x = new Rational(1, 3)                      //> x  : week1.Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : week1.Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : week1.Rational = 3/2
  x.numer                                         //> res0: Int = 1
  x.denom                                         //> res1: Int = 3
  x + y                                           //> res2: week1.Rational = 22/21
  -x                                              //> res3: week1.Rational = -1/3
  x - y - z                                       //> res4: week1.Rational = -79/42
  y + y                                           //> res5: week1.Rational = 70/49
  x < y                                           //> res6: Boolean = true
  x max y                                         //> res7: week1.Rational = 5/7
}

class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y
  override def toString = numer + "/" + denom

  def +(that: Rational) = new Rational(this.numer * that.denom + this.denom * that.numer, this.denom * that.denom)
  def unary_- : Rational = new Rational(-this.numer, this.denom)
  def -(that: Rational) = this + -that
  def <(that: Rational) = this.numer * that.denom < this.denom * that.numer
  def max(that: Rational) = if (this < that) that else this
}
```

## 化簡分子、分母
```scala
object session {
  val x = new Rational(1, 3)                      //> x  : week1.Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : week1.Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : week1.Rational = 3/2
  x.numer                                         //> res0: Int = 1
  x.denom                                         //> res1: Int = 3
  x + y                                           //> res2: week1.Rational = 22/21
  -x                                              //> res3: week1.Rational = -1/3
  x - y - z                                       //> res4: week1.Rational = -79/42
  y + y                                           //> res5: week1.Rational = 10/7
  x < y                                           //> res6: Boolean = true
  x max y                                         //> res7: week1.Rational = 5/7
}

class Rational(x: Int, y: Int) {
  def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  def abs(n: Int): Int = if (n < 0) -n else n
  val g = abs(gcd(x, y))

  val numer = x / g
  val denom = y / g

  override def toString = numer + "/" + denom

  def +(that: Rational) = new Rational(this.numer * that.denom + this.denom * that.numer, this.denom * that.denom)
  def unary_- : Rational = new Rational(-this.numer, this.denom)
  def -(that: Rational) = this + -that
  def <(that: Rational) = this.numer * that.denom < this.denom * that.numer
  def max(that: Rational) = if (this < that) that else this
}
```
