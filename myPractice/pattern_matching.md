# Pattern Matching

## Object-oriented way
```scala
trait Expr {
  def eval: Int
}

class Number(n: Int) extends Expr {
  def eval: Int = n
}

class Sum(e1: Expr, e2: Expr) extends Expr {
  def eval: Int = e1.eval + e2.eval
}

val sum = new Sum(new Number(1), new Number(2)) //> sum  : Sum = $Sum@5d641f43
sum.eval                                        //> res0: Int = 3
```

## Pattern-matching way
```scala
trait Expr
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr

def eval(e: Expr): Int = e match {
	case Number(n) => n
	case Sum(e1: Expr, e2: Expr) => eval(e1) + eval(e2)
}                                               //> eval: (e: Expr)Int

eval (Sum(Number(1), Number(2)))                //> res0: Int = 3
```

```scala
trait Expr {
  def eval: Int = this match {
    case Number(n)               => n
    case Sum(e1: Expr, e2: Expr) => e1.eval + e2.eval   
  }
}
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr

Sum(Number(1), Number(2)).eval                  //> res0: Int = 3
```

___
## Pattern Matching vs If statement

```scala
def f0(n: Int): Boolean =
  if (n == 1) true else false                   //> f0: (n: Int)Boolean
f0(1)                                           //> res0: Boolean = true
f0(2)                                           //> res1: Boolean = false

def f1: Int => Boolean = n =>
  if (n == 1) true else false                   //> f1: => Int => Boolean
f1(1)                                           //> res2: Boolean = true
f1(2)                                           //> res3: Boolean = false

def f2(n: Int): Boolean = n match {
  case 1 => true
  case _ => false
}                                               //> f2: (n: Int)Boolean
f2(1)                                           //> res4: Boolean = true
f2(2)                                           //> res5: Boolean = false

def f3: Int => Boolean = n =>
  n match {
    case 1 => true
    case _ => false
  }                                             //> f3: => Int => Boolean
f3(1)                                           //> res6: Boolean = true
f3(2)                                           //> res7: Boolean = false

def f4: Int => Boolean = {
  case 1 => true
  case _ => false
}                                               //> f4: => Int => Boolean
f4(1)                                           //> res8: Boolean = true
f4(2)                                           //> res9: Boolean = false
```
