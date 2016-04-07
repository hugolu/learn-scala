# Prime

內容來自 [Functional Programming Principles in Scala](https://class.coursera.org/progfun-005) Lecture 7.4

## The Sieve of Eratosthenes
The Sieve of Eratosthenes is an ancient technique to calculate prime numbers. The idea is as follows:

- Start with all integers from 2, the first prime number.
- Eliminate all multiples of 2.
- The first element of the resulting list is 3, a prime number.
- Eliminate all multiples of 3.
- Iterate forever. At each step, the first number in the list is a prime number and we eliminate all its multiples.

```
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 ...
2 3   5   7   9    11    13    15    17    19       // 2 is prime, remove all multiples of 2
2 3   5   7        11    13    15    17    19       // 3 is prime, remove all multiples of 3
2 3   5   7        11    13          17    19       // 5 is prime, remove all multiples of 5
```

## Scala code in Imperative way
```scala
import scala.collection.mutable.ArrayBuffer

val primes = ArrayBuffer[Int]()                 //> primes  : scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer()
def nextPrime(primes: ArrayBuffer[Int]): Int = primes match {
  case ArrayBuffer() => 2
  case _ =>
    def getPrime(n: Int): Int =
      if (primes forall (p => n % p != 0)) n else getPrime(n + 1)
    getPrime(primes.last)
}                                               //> nextPrime: (primes: scala.collection.mutable.ArrayBuffer[Int])Int

for (n <- 1 to 10)
  primes += nextPrime(primes)

primes.toList                                   //> res0: List[Int] = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
```

## Scala code in Functional way
```scala
def sieve(s: Stream[Int]): Stream[Int] =
  s.head #:: sieve(s.tail filter (_ % s.head != 0))
                                                //> sieve: (s: Stream[Int])Stream[Int]

val s = Stream.from(2)                          //> s  : scala.collection.immutable.Stream[Int] = Stream(2, ?)
val primes = sieve(s)                           //> primes  : Stream[Int] = Stream(2, ?)
(primes take 10).toList                         //> res0: List[Int] = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)

s.take(1)                                       //> res1: scala.collection.immutable.Stream[Int] = Stream(2, ?)
s.filter(_ % 2 != 0).take(1)                    //> res2: scala.collection.immutable.Stream[Int] = Stream(3, ?)
s.filter(_ % 2 != 0).filter(_ % 3 != 0).take(1) //> res3: scala.collection.immutable.Stream[Int] = Stream(5, ?)
```
- 1st prime
  - 2 #:: sieve(Stream(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20...) filter (_ % 2 != 0))
  - 2 #:: Stream(3, 5, 7, 9, 11, 13, 15, 17, 19...)
  - s.take(1) = Stream(2, ?)
- 2nd prime
  - 3 #:: sieve(Stream(3, 5, 7, 9, 11, 13, 15, 17, 19...) filter (_ % 3 != 0))
  - 3 #:: Stream(5, 7, 11, 13, 15, 17, 19...)
  - s.filter(_ % 2 != 0).take(1) = Stream(3, ?)
- 3rd prime
  - 5 #:: sieve(Stream(5, 7, 11, 13, 15, 17, 19...) filter (_ % 5 != 0))
  - 5 #:: Stream(7, 11, 13, 17, 19...)
  - s.filter(_ % 2 != 0).filter(_ % 3 != 0).take(1) = Stream(5, ?)
