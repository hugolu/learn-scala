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
