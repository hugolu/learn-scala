sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
	def sum(ints: List[Int]): Int = ints match {
		case Nil => 0
		case Cons(x, xs) => x + sum(xs)
	}

	def product(ints: List[Int]): Int = ints match {
		case Nil => 1
		case Cons(x, xs) => x * product(xs)
	}

	def apply[A](as: A*): List[A] = if (as.isEmpty) Nil else Cons(as.head, apply(as.tail: _*))
}

import List._
List(1,2,3,4,5)

// ex 3.1
List(1,2,3,4,5) match {
	case Cons(x, Cons(2, Cons(4, _))) => x
	case Nil => 42
	case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x+y
	case Cons(h, t) => h + sum(t)
	case _ => 101
}

// ex 3.2
def tail[A](l: List[A]): List[A] = l match {
	case Nil => Nil
	case Cons(_, tail) => tail
}

tail(Nil)
tail(List(1,2,3,4,5))

// ex 3.3
def setHead[A](l: List[A], a: A): List[A] = l match {
	case Nil => Cons(a, Nil)
	case Cons(x, xs) => Cons(a, xs)
}

setHead(Nil, 0)
setHead(List(1,2,3,4,5), 0)

// ex 3.4
def drop[A](l: List[A], n: Int): List[A] = if (n == 0) l else l match {
	case Nil => Nil
	case Cons(x, xs) => drop(xs, n-1)
}

drop(Nil, 3)
drop(List(1,2,3,4,5), 3)

// ex 3.5
def dropWhile[A](l : List[A], f: A=>Boolean): List[A] = l match {
	case Nil => Nil
	case Cons(x, xs) => if (f(x)) dropWhile(xs, f) else Cons(x, dropWhile(xs, f))
}

dropWhile(Nil, (x:Int) => x % 2 == 0)
dropWhile(List(1,2,3,4,5), (x:Int) => x % 2 == 0)


// ex 3.6
def init[A](l: List[A]): List[A] = l match {
	case Nil => Nil
	case Cons(x, xs) => if (xs == Nil) Nil else Cons(x, init(xs))
}

init(Nil)
init(List(1,2,3,4,5))

//
def foldRight[A, B](l: List[A], z: B)(f: (A, B)=>B): B = l match {
	case Nil => z
	case Cons(x, xs) => f(x, foldRight(xs, z)(f))
}

def sum2(l: List[Int]) = foldRight(l, 0)(_ + _)
sum2(Nil)
sum2(List(1,2,3,4,5))

def product2(l: List[Int]) = foldRight(l, 1)(_ * _)
product2(Nil)
product2(List(1,2,3,4,5))

// ex 3.8
foldRight(List(1,2,3,4), Nil:List[Int])(Cons(_,_))

// ex 3.9
def length[A](l: List[A]): Int = foldRight(l, 0)((a,b)=>b+1)

length(Nil)
length(List(1,2,3,4,5))

// ex 3.10
def foldLeft[B, A](l: List[A], z: B)(f: (B, A)=>B): B = l match {
    case Nil => z
    case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
}

// ex 3.11
def sum3(l: List[Int]) = foldLeft(l, 0)(_ + _)
sum3(Nil)
sum3(List(1,2,3,4,5))

def product3(l: List[Int]) = foldLeft(l, 1)(_ * _)
product3(Nil)
product3(List(1,2,3,4,5))

def length3[A](l: List[A]): Int = foldLeft(l, 0)((b,a)=>b+1)
length3(Nil)
length3(List(1,2,3,4,5))

// ex 3.12
def reverse[A](l: List[A]): List[A] = foldLeft(l, Nil: List[A])((b,a)=>Cons(a,b))

reverse(Nil)
reverse(List(1,2,3,4,5))
