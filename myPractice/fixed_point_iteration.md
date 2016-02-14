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
```x=sqrt(value)``` ⇒ ```x^2 = value``` ⇒ ```x = value/x```

x<sub>n+1</sub> = (x<sub>n</sub> + value/x<sub>n</sub>)/2
- when value = 2, x<sub>n+1</sub> = (x<sub>n</sub> + 2/x<sub>n</sub>)/2

| x<sub>n</sub> | x<sub>n+1</sub> | Diff |
|-----------------|---------------|------|
| 1.0 | 1.5 | 0.5 |
| 1.5 | 1.4166666666666665 | 0.08333333333333348 |
| 1.4166666666666665 | 1.4142156862745097 | 0.002450980392156854 |
| 1.4142156862745097 | 1.4142135623746899 | 2.1238998197947723E-6 |

```scala
def sqrt(value: Double): Double = fixedPoint(x => (x + value / x) / 2)(1)
                                                //> sqrt: (x: Double)Double
sqrt(2)                                         //> guess = 1.0
                                                //| guess = 1.5
                                                //| guess = 1.4166666666666665
                                                //| guess = 1.4142156862745097
                                                //| res0: Double = 1.4142135623746899
```
___
## averageDump
extract the average computation:
```scala
def averageDump(f: Double => Double): Double => Double = x => (f(x) + x) / 2
                                                //> averageDump: (f: Double => Double)Double => Double

def sqrt(value: Double): Double = fixedPoint(averageDump(x => value / x))(1)
                                                //> sqrt: (value: Double)Double
sqrt(2)                                         //> guess = 1.0
                                                //| guess = 1.5
                                                //| guess = 1.4166666666666665
                                                //| guess = 1.4142156862745097
                                                //| res0: Double = 1.4142135623746899
```
