# Covariance

定義 `A <: B` 對於 `C[T]` 而言，`C[A]`與`C[B]`並沒有subtype的關係
```scala
class C[T](val o: T) { override def toString = o.toString }
class A { override def toString = "A" }
class B extends A { override def toString = "B" }

val a = new C(new A)                            //> a  : myTest.test27.C[myTest.test27.A] = A
val b = new C(new B)                            //> b  : myTest.test27.C[myTest.test27.B] = B

val c1: C[A] = a                                //> c1  : myTest.test27.C[myTest.test27.A] = A
val c2: C[A] = b                                //type mismatch;
```

完整的錯誤訊息如下:
```
type mismatch;  
found   : myTest.test27.C[myTest.test27.B]  
required: myTest.test27.C[myTest.test27.A] 
Note: myTest.test27.B <: myTest.test27.A, but class C is invariant in type T. 
You may wish to define T as +T instead. (SLS 4.5)
```
- 編譯器還貼心的建議使用`Node[+T]`

修改定義後，果然成功了
```scala
class C[+T](val o: T) { override def toString = o.toString }
class A { override def toString = "A" }
class B extends A { override def toString = "B" }

val a = new C(new A)                            //> a  : myTest.test27.C[myTest.test27.A] = A
val b = new C(new B)                            //> b  : myTest.test27.C[myTest.test27.B] = B

val c1: C[A] = a                                //> c1  : myTest.test27.C[myTest.test27.A] = A
val c2: C[A] = b                                //> c2  : myTest.test27.C[myTest.test27.A] = B
```
- `A <: B`，對於`C[+T]`而言，`C[A] <: C[B]`

___
學習過程中發現一個矛盾點
```scala
class C[T](val o: T) { override def toString = o.toString }
class A { override def toString = "A" }
class B extends A { override def toString = "B" }

val c2: C[A] = new C(new B)                     //> c2  : myTest.test27.C[myTest.test27.A] = B
```
- 為什麼將 `new C(new B)` 丟給 `val c2: C[A]` 沒有出現編譯錯誤？
- 可能的原因是編譯器將 `new C(new B)` 的型別當成 `C[A]`，所以才不沒出現錯誤訊息
