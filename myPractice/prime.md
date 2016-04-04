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

## Scala code
```scala
def sieve(s: Stream[Int]): Stream[Int] =
  s.head #:: sieve(s.tail filter (_ % s.head != 0))
                                                //> sieve: (s: Stream[Int])Stream[Int]
val primes = sieve(Stream.from(2))              //> primes  : Stream[Int] = Stream(2, ?)

(primes take 10).toList                         //> res0: List[Int] = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
```
