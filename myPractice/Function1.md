# Function1

refer to [Functional Programming Principles in Scala](https://class.coursera.org/progfun-005/lecture) in Coursera

## Definition of Variance

Say ```C[T]``` is a parameterized type and ```A```, ```B``` are types such that ```A <: B```.
In genreal, there are three possible relationships between ```C[A]``` and ```C[B]```:

| Relationship | Variance Type |
|--------------|---------------|
| ```C[A] <: C[B]``` | C is covariant |
| ```C[A] >: C[B]``` | C is contravariant |
| neither ```C[A]``` or ```C[B]``` is a subtype of the other | C is nonvariant |

Scala lets you declare the variance of a type by annotating the type parameter.

| Type Parameter | Variance Type |
|----------------|---------------|
| ```class C[+T]{}``` | C is covariant |
| ```class C[-T]{}``` | C is contravariant |
| ```class C[T]{}``` | C is nonvariant |

## Covariant type parameters can only appear in method results.
```scala
class Super
class Sub extends Super

val fun1: () => Sub = () => new Super       // error: type mismatch;
val fun2: () => Super = () => new Sub       //> fun1: () => Super = <function0>
```
- ```fun1```：匿名函式產生```Super```，不符合```fun1```接收者預期得到```Sub```的要求，違反LSP原則
- ```fun2```：匿名函式產生```Sub```，符合```fun2```接收者預期得到```Super```的要求，合乎LSP原則

## Contravariant type parameter can only appear in method parameters.
```scala
class Super
class Sub extends Super

val fun3: Sub => Unit = (s: Super) => {}    //> fun3  : Sub => Unit = <function1>
val fun4: Super => Unit = (s: Sub) => {}    // error: type mismatch;
```
- ```fun3()```：```fun3```接收```Sub```，符合匿名函式接收```Super```的條件，合乎LSP原則
- ```fun3()```：```fun4```接收```Super```，不符合匿名函式接收```Sub```的條件，違反LSP原則

## 練習題
有兩個 function types，根據LSP (Liskov Substitution Principle)，誰是誰的子型別？

```
class Super
class Sub extends Super

type A = Super => Sub
type B = Sub => Super
```
