# Function1

參考 Coursera [Functional Programming Principles in Scala](https://class.coursera.org/progfun-005/lecture)

## Subtype
```scala
class A
class B extends A

def show[T <: A](x: T) = println(x)             //> show: [T <: A](x: T)Unit

show(new A)                                     //> $A@4ec6948c
show(new B)                                     //> $B@4f429bbb
```
> 其實 ```def show[T <: A](x: T)``` 應該定義成 ```def show(x: A)```，為了說明 ```<:```，用了一個不是很恰當的範例。

- ```T <: A``` 表示參數型別 (type parameter) ```T``` 必須是 ```A``` 或是其子型別 (subtype)，這樣的限制也稱作 [Upper Type Bound](http://www.scala-lang.org/old/node/136)。

定義 Upper Type Bound 有什麼作用？因為透過這樣的**限制**，明確宣告物件使用者只能接收```A```或其子型別，這樣就能在 compile-time 檢找出型別錯誤，避免 run-time type error 或要在 run-time 執行類似 Java Reflection 的轉換。

```B``` 是 ```A``` 的子型別 ⇔ 使用 ```A``` 的地方能用 ```B``` 取代。

## Definition of Variance

Say ```C[T]``` is a parameterized type and ```A```, ```B``` are types such that ```A <: B``` (```A``` is a subtype of ```B```).
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

def returnA(): A = new A          //> returnA: ()A
def returnB(): B = new B          //> returnB: ()B

val fun1: () => A = returnA       //> fun1: () => A = <function0>
val fun2: () => A = returnB       //> fun2: () => A = <function0>
val fun3: () => B = returnA       // error: type mismatch;
val fun4: () => B = returnB       //> fun4: () => B = <function0>
```
- ```fun1```呼叫者預期得到```A```，```returnA```回傳值```A```合乎預期
- ```fun2```呼叫者預期得到```A```，```returnB```回傳值```B```合乎預期
  - ```B <: A```，```B```是```A```的子型別 ⇔ ```B```符合```A```的型別要求
  - ```()=>A```可以用```()=>B```取代 ⇔ ```()=>B```是```()=>A```的子型別，```()=>B <: ()=>A```
  - ```B <: A``` 且 ```C[B] <: C[A]```，這就是 **Covariance**
- ```fun3```呼叫者預期得到```B```，```returnA```回傳值```A```違反預期
  - ```B <: A```，```B```是```A```的子型別 ⇔ ```A```不符合```B```的型別要求
  - ```()=>B```不能用```()=>A```取代 ⇔ ```()=>A```不是```()=>B```的子型別
- ```fun4```呼叫者預期得到```B```，```returnB```回傳值```B```合乎預期

用```class```來表示function，看起來像...
```scala
class A
class B extends A

trait Function[+T] { def apply(): T }
class ReturnA extends Function[A] { def apply() = new A }
class ReturnB extends Function[B] { def apply() = new B }

val returnA = new ReturnA                       //> returnA  : ReturnA = $ReturnA@8854a21a
val returnB = new ReturnB                       //> returnB  : ReturnB = $ReturnB@1a7811df

val fun1: Function[A] = returnA                 //> fun1  : Function[A] = $ReturnA@8854a21a
val fun2: Function[A] = returnB                 //> fun2  : Function[A] = $ReturnB@1a7811df
val fun3: Function[B] = returnA                 // error: type mismatch;
val fun4: Function[B] = returnB                 //> fun4  : Function[B] = $ReturnB@1a7811df
```
- 檢查 return type：returnA/B 回傳值的型別要能滿足 fun# 對回傳值型別的要求 ⇔ returnA/B 是 fun# 的子型別
- ```B <: A``` 且 ```Function[B] <: Function[A]``` (```Function[B]```可以取代```Function[A]```) ⇔ ```Function[+T]```

## Contravariant type parameter can only appear in method parameters.
```scala
class A
class B extends A

def acceptA(x: A): Unit = {}      //> acceptA: (x: A)Unit
def acceptB(x: B): Unit = {}      //> acceptB: (x: B)Unit

val fun1: A => Unit = acceptA     //> fun1: A => Unit = <function1>
val fun2: A => Unit = acceptB     // error: type mismatch;
val fun3: B => Unit = acceptA     //> fun3: B => Unit = <function1>
val fun4: B => Unit = acceptB     //> fun4: B => Unit = <function1>
```
- ```fun1```呼叫者輸入```A```，合乎```acceptA```參數型別的要求
- ```fun2```呼叫者輸入```A```，違反```acceptB```參數型別的要求
  - ```B <: A```，```B```是```A```的子型別 ⇔ ```A```不符合```B```的型別要求
  - ```(x:A)=>Unit```不可用 ```(x:B)=>Unit```取代 ⇔ ```(x:B)=>Unit```不是```(x:A)=>Unit```的子型別
- ```fun3```呼叫者輸入```B```，合乎```acceptA```參數型別的要求
  - ```B <: A```，```B```是```A```的子型別 ⇔ ```B```符合```A```的型別要求
  - ```(x:B)=>Unit```可以用 ```(x:A)=>Unit```取代 ⇔ ```(x:A)=>Unit```是```(x:B)=>Unit```的子型別，```(x:A)=>Unit <: (x:B)=>Unit```
  - ```B <: A``` 且 ```C[A] <: C[B]```，這就是 **Contravariance**
- ```fun4```呼叫者輸入```B```，合乎```acceptB```參數型別的要求

用```class```來表示function，看起來像...
```scala
class A
class B extends A

trait Function[-T] { def apply(x: T): Unit }
class AcceptA extends Function[A] { def apply(x: A) = {} }
class AcceptB extends Function[B] { def apply(x: B) = {} }

val acceptA = new AcceptA                       //> acceptA  : AcceptA = $AcceptA@40fb2f19
val acceptB = new AcceptB                       //> acceptB  : AcceptB = $AcceptB@163202d6

val fun1: Function[A] = acceptA                 //> fun1  : Function[A] = $AcceptA@40fb2f19
val fun2: Function[A] = acceptB                 // error: type mismatch;
val fun3: Function[B] = acceptA                 //> fun3  : Function[B] = $AcceptA@40fb2f19
val fun4: Function[B] = acceptB                 //> fun4  : Function[B] = $AcceptB@163202d6
```
- 檢查 parameter type：fun# 的參數型別要能滿足 acceptA/B 對參數型別的要求 ⇔ acceptA/B 是 fun# 的子型別
- ```B <: A``` 且 ```Function[A] <: Function[B]``` (```Function[A]```可以取代```Function[B]```) ⇔ ```Function[-T]```

## 練習題
有兩個 function types，根據LSP (Liskov Substitution Principle)，誰是誰的子型別？

```
class A
class B extends A

type X = A => B
type Y = B => A
```
- ```B <: A```，```B```是```A```的子型別
- ```X```可以輸入```B```，並且回傳值符合```A```的限制嗎？ Yes
- ```Y```可以輸入```A```，並且回傳值符合```B```的限制嗎？ No
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
