## GCD

```scala
def gcd(a: Int, b:Int): Int = if (b == 0) a else gcd(b, a % b)

gcd(14, 21)
// if (21 == 0) 14 else gcd(21, 14%21)
// if (false) 14 else gcd(21, 14%21)
// gcd(21, 14%21)
// gcd(21, 14)
// if (14 == 0) 21 else gcd(14, 21%14)
// if (false) 21 else gcd(14, 21%14)
// gcd(14, 21%14)
// gcd(14, 7)
// if (7 == 0) 14 else gcd(7, 14%7)
// if (false) 14 else gcd(7, 14%7)
// gcd(7, 14%7)
// gcd(7, 0)
// if (0 == 0) 7 else gcd(0, 7%0)
// 7
```
- a tail recursive function
-  the compiler will optimize a tail recursive function. 

## Factorial
```scala
def factorial(n: Int): Int = if (n == 0) 1 else n*factorial(n-1)

factorial(4)
// if (4 == 0) 1 else 4 * factorial(4-1)
// if (false) 1 else 4 * factorial(4-1)
// if (false) 1 else 4 * factorial(3)
// 4 * factorial(3)
// 4 * (3 * factorial(2))
// 4 * (3 * (2 * factorial(1))
// 4 * (3 * (2 * (1 * factorial(0))))
// 4 * (3 * (2 * (1 * 1)))
// 4 * (3 * (2 * 1))
// 4 * (3 * 2)
// 4 * 6
// 24
```
- not a tail recursive function

```scala
def factorial(n:Int): Int = {
  def loop(acc: Int, n: Int): Int = if (n == 0) acc else loop(acc * n, n - 1)
  loop(1, n)
}
factorial(4)
```
- a tail recursive function
