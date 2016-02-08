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
