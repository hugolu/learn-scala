# Function1

參考 Coursera [Functional Programming Principles in Scala](https://class.coursera.org/progfun-005/lecture)

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
class A
class B extends A

val fun1: () => B = () => new A       // error: type mismatch;
val fun2: () => A = () => new B       //> fun1: () => A = <function0>
```
- ```fun1```：匿名函式產生```A```，違背函式呼叫者預期得到```B```的要求 
- ```fun2```：匿名函式產生```B```，符合函式呼叫者預期得到```A```的要求 (```B```符合```A```的型別要求)
- 

## Contravariant type parameter can only appear in method parameters.
```scala
class A
class B extends A

val fun3: B => Unit = (s: A) => {}    //> fun3  : B => Unit = <function1>
val fun4: A => Unit = (s: B) => {}    // error: type mismatch;
```
- ```fun3```：函式呼叫者輸入```B```，符合匿名函式接收```A```的要求 (```B```符合```A```的型別要求)
- ```fun4```：函式呼叫者輸入```A```，違背匿名函式接收```B```的要求

## 練習題
有兩個 function types，根據LSP (Liskov Substitution Principle)，誰是誰的子型別？

```
class A
class B extends A

type X = A => B
type Y = B => A
```
- ```X```可以輸入```B```，並且回傳```A```嗎？ Yes
- ```Y```可以輸入```A```，並且回傳```B```嗎？ No
- ```X```滿足```Y```的限制，所以```X```是```Y```的subtype (```X <: Y```)

## Function Trait Declaration
Functions are covariant in arguments type(s) and covariant in result type.

```scala
trait Function1[-T, +U] {
  def apply(x: T): U
}
```
```scala
class A
class B extends A

val fun: B => A = (x: A) => new B               //> fun  : B => A = <function1>
fun(new B)                                      //> res0: A = $B@5b013dc8
```
