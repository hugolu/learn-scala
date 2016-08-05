# 異變與邊界

## 參考連結
- [scala-协变、逆变、上界、下界](http://www.cnblogs.com/jacksu-tencent/p/4979666.html)
- [Scala中的协变，逆变，上界，下界等](http://www.tuicool.com/articles/uYvyAbB)
- [Scala的协变(+)，逆变(-)，上界(<:)，下界(>:)](http://my.oschina.net/xinxingegeya/blog/486671)

Scala 是一種靜態類型語言，它的型別系統可能是所有程式語言中最複雜的。

Scala 型別系統企圖透過型別推斷讓程式碼更精簡與更有表達力，並防止程序在執行時期處於無效狀態。在編譯時期強加限制，使得運行時不會發生失敗，這需要編寫程式時額外遵守一些規則，而這些規則使得一般人覺得 Scala 很複雜。

## Invarient 不變
```scala
class A {}
class B extends A {}

class C[T](t: T) {}

val c1: C[A] = new C[A](new A)
val c2: C[A] = new C[B](new B)  // won't compile
val c3: C[B] = new C[A](new A)  // won't compile
val c4: C[B] = new C[B](new B)
```

- 型別 `B` 是型別 `A` 的子型別
- 定義參數化型別 `C[T]` 為不變 (invarient)

## Covarient 協變
```scala
class A {}
class B extends A {}

class C[+T](t: T) {}

val cb: C[B] = new C[B](new B)
val ca: C[A] = cb
```

- 型別 `B` 是型別 `A` 的子型別
- 定義參數化型別 `C[+T]` 為協變 (covarient)
- 型別 `C[B]` 是型別 `C[A]` 的子型別
- `cb` 可以賦值給 `ca`

## Contravarient 逆變
```scala
class A {}
class B extends A {}

class C[-T](t: T) {}

val ca: C[A] = new C[A](new A)
val cb: C[B] = ca
```

- 型別 `B` 是型別 `A` 的子型別
- 定義參數化型別 `C[-T]` 為逆變 (contravarient)
- 型別 `C[A]` 是型別 `C[B]` 的子型別
- `ca` 可以賦值給 `cb`

## Lower Bound 下界
如果協變型別包含型別參數的方法
```scala
class A {}
class B extends A {}

class C[+T](t: T) {
	def foo[T](t: T) = {} // won't compile: covariant type T occurs in contravariant position in type T of value t
}
```

為了在協變中使用型別參數，須定義下界。使用 `[U >: T]`，其中 `T` 為下界，`U` 為 `T` 或 `T` 的超類
```scala
class A {}
class B extends A {}

class C[+T](t: T) {
	def foo[U >: T](u: U) = {}
}

val cb = new C[B](new B)  //#1
val ca: C[A] = cb         //#2
ca.foo(new A)             //#3
```
- #1: `C[B].foo` 接受型別參數 `B` 或 `B` 的超類 (包含 `A`)
- #2: 將 `cb` 賦值給 `ca`
- #3: 傳遞型別 `A` 的參數給 `ca.foo()`，呼叫實作 `cb.foo()` 處理，因為 #1，`cb.foo()` 可以接受型別 `A` 的參數

## Upper Bound 上界
為了在逆變中使用型別參數，須定義上界。使用 `[S <: T]`，其中 `T` 為上界，`S` 為 `T` 或 `T` 的子類
```scala
class A {}
class B extends A {}

class C[-T](t: T) {
	def foo[S <: T](s: S) = {}
}

val ca = new C[A](new A)	//#1
val cb: C[B] = ca					//#2
cb.foo(new B)							//#3
```
- #1: `C[A].foo` 接受型別參數 `A` 或 `A` 的子類 (包含 `B`)
- #2: 將 `ca` 賦值給 `cb`
- #3: 傳遞型別 `B` 的參數給 `cb.foo()`，呼叫實作 `ca.foo()` 處理，因為 #1，`ca.foo()` 可以接受型別 `B` 的參數

## View Bound 視界

## Context Bound 
