# Primitive as Class

reference: Lecture 4.2 - Objects Everywhere from [Functional Programming Principles in Scala](https://class.coursera.org/progfun-005/)

```scala
abstract class Nat {
	def isZero: Boolean
	def predecessor: Nat
	def successor: Nat = new Succ(this)
	def +(that: Nat): Nat
	def -(that: Nat): Nat
	
	val num: Int
	override def toString = num.toString
}

object Zero extends Nat {
	def isZero = true
	def predecessor: Nat = throw new Error("0.predecessor")
	def +(that: Nat) = that
	def -(that: Nat) = if (that.isZero) this else throw new Error("negative")
	val num = 0
}

class Succ(n: Nat) extends Nat {
	def isZero = false
	def predecessor: Nat = n
	def +(that: Nat) = new Succ(n + that)
	def -(that: Nat) = if (that.isZero) this else n - that.predecessor
	val num = n.num + 1
}
```

```scala
val n0 = Zero                                   //> n0  : myTest.Zero.type = 0
val n1 = new Succ(n0)                           //> n1  : myTest.Succ = 1
val n2 = new Succ(n1)                           //> n2  : myTest.Succ = 2
val n3 = new Succ(n2)                           //> n3  : myTest.Succ = 3
```

```scala
n0 + n1                                         //> res0: myTest.Nat = 1
```
- = `Zero.+(n1)`
- = `n1`

```scala
n1 + n0                                         //> res1: myTest.Succ = 1
```
- = `new Succ(n1.predecessor + n0)`
- = `new Succ(n0 + n0)`
- = `new Succ(n0)`
- = `n1`

```scala
n2 + n1                                         //> res2: myTest.Succ = 3
```
- = `new Succ(n2.predecessor + n1)` = `new Succ(n1 + n1)`
- = `new Succ(new Succ(n1.predecessor + n1))` = `new Succ(new Succ(n0 + n1))`
- = `new Succ(new Succ(n1))`
- = `new Succ(n2)`
- = `n3`

```scala
n1 - n0                                         //> res3: myTest.Nat = 1
```
- = `n1.-(n0)`
- = `n1.-(Zero)`
- = `n1`

```scala
n3 - n2                                         //> res4: myTest.Nat = 1
```
- = `n3 - n2` = `n3.predecessor - n2.predecessor`
- = `n2 - n1` = `n2.predecessor - n1.predecessor`
- = `n1 - n0` = `n1`
