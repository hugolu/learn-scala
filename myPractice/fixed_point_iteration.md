# Fixed Point Iteration

```scala
def fixedPoint(f: Double => Double)(firstGuess: Double): Double = {
  val tolerance: Double = 0.001

  def abs(x: Double): Double = if (x > 0) x else -x
  def isCloseEnough(x: Double, y: Double) = abs(x - y) / x < tolerance

  def iterate(guess: Double): Double = {
    println("guess = " + guess)
    val next = f(guess)
    if (isCloseEnough(guess, next)) next else iterate(next)
  }

  iterate(firstGuess)
}                                               //> fixedPoint: (f: Double => Double)(firstGuess: Double)Double
```

## find the squart root
```y=sqrt(x)``` ⇒ ```y^2 = x``` ⇒ ```y = x/y```

```scala
def sqrt(x: Double): Double = fixedPoint(y => (y + x/y)/2)(1)
                                                //> sqrt: (x: Double)Double
sqrt(2)                                         //> guess = 1.0
                                                //| guess = 1.5
                                                //| guess = 1.4166666666666665
                                                //| guess = 1.4142156862745097
                                                //| res0: Double = 1.4142135623746899
```
