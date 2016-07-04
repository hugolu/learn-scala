# Chapter 6 型別系統

“型別系統是 Scala 語言非常重要的組成部分。它使得編譯器能進行很多編譯時優化和約束，從而提高運行速度與避免程序錯誤。型別系統讓我們可以在我們自身周圍創建各種有用的**牆**，也就是所謂的**型別**。通過讓編譯器來跟蹤變量、方法與類的訊息，這些**牆**能幫助我們避免不小心寫出不正確的程式碼。你對 Scala 的型別系統所知越多，這能給編譯器更多的信息，讓型別的**牆**變得不那麼束縛，而同時仍然提供相同的保護。”

## 6.1 型別 (Types)

要理解 Scala 的型別系統，要先理解什麼是型別、如何建立。
- 型別就是編譯器需要知道的一組訊息，可以由使用者提供、或是編譯器檢查代碼時推斷
- 定義型別的方式
  - 定義 class, trait, object 
  - 用 `type` 關鍵字定義型別

```scala
scala> class ClassName
defined class ClassName

scala> trait TraitName
defined trait TraitName

scala> object ObjectName
defined object ObjectName

scala> def foo(x: ClassName) = x
foo: (x: ClassName)ClassName

scala> def bar(x: TraitName) = x
bar: (x: TraitName)TraitName

scala> def baz(x: ObjectName.type) = x
baz: (x: ObjectName.type)ObjectName.type
```

### 6.1.1 型別與路徑 (Types and paths)

Scala 型別透過綁定 (binding) 或路徑 (path) 來引用
- 綁定：某個實體的名字
- 路徑：某種位置，讓編譯器找尋型別。以下幾種
  - 空路徑，直接使用型別名字，前面隱含空路徑
  - `C.this`，`C` 指向一個 class。在 class 裡面用 `this`，完整路徑為 `C.this`
  - `p.x`，`p` 是路徑，`x` 是穩定標示符號
    - 穩定標示符 (stable identifier)：編譯器明確知道在路徑 `p` 之下總是可見的標示符
    - 穩定成員 (stable member) 指在非易變形別裡引入的 packages, objects, value definitions
    - 易變形別 (volatile type) 只編譯器不能確保成員是永遠不變的型別，例如抽象型別，其類型定義依賴於 subtype
  - `C.super` 或 `C.super[P]`，`C` 指向一個 class，`P` 指向 `C` 的父類型

兩種引用 (refer) 機制
- `.`
  - 路徑依賴型別 (path-dependent type)
  - 引用「綁定特定物件實例的型別」(It refers to a type found on a specific object instance.)
- `#`
  - 類型投影 (type projection)
  - 引用巢狀型別 (nestd type)，卻不需引用物件實例的路徑

#### Listing 6.2 Path-dependent types and type projection examples
```scala
class Outer {
    trait Inner
    def y = new Inner {}
    def foo(x: this.Inner) = null
    def bar(x: Outer#Inner) = null
}

scala> val x = new Outer
x: Outer = Outer@7a7b0070

scala> val y = new Outer
y: Outer = Outer@59690aa4

scala> x.foo(x.y)
res0: Null = null

scala> x.foo(y.y)
<console>:14: error: type mismatch;
 found   : y.Inner
 required: x.Inner
       x.foo(y.y)
               ^

scala> x.bar(y.y)
res2: Null = null
```

### 6.1.2 型別關鍵字 (The type keyword)

`type`可以用來構造具體型別 (concrete type) 或抽象型別 (abstract type)
- 具體型別：引用已存在的型別 (existing type) 或使用結構化型別(structural type)
  - 提供型別定義
- 抽象型別：構造用來作為佔位符，以便以後由子型別重新定義
  - 沒有提供約束 (constraints) 或賦值 (assignments)

`type` 關鍵字只能在某種形式的上下文定義型別，在 class, trait, object 或前者之一的子上下文 (subcontext)

```scala
type AbstractType
type ConcreteType = SomeFooType
type ConcreteType = SomeFooType with SomeBarType // a compound type 
```

### 6.1.3 結構化型別 (Structural types)

- 建構結構化型別使用 `type` 關鍵字，同時定義期望型別裡所具有的方法簽名 (method signature) 與變量簽名 (variable signature)
- 開發者可以定義一種抽象介面 (abstract interface)，而不需要擴充 trait 或 class 以合乎此介面
- 結構化型別通常用在資源管理的程式碼

#### Listing 6.3 Resource handling utility
```scala
object Resources {
    type Resource = {
        def close() : Unit
    }
    def closeResource(r: Resource) = r.close()
}

class Foo {
    def close() = println("Foo is closing")
}
```
- `Resource`: 把資源定義成任何有 `close` 方法的東西
- `Foo` 剛好有 `close` 方法

```scala
scala> val foo = new Foo()
foo: Foo = Foo@47f37ef1

scala> Resources.closeResource(foo)
Foo is closing
```

#### Listing 6.4 Nested structural typing
```scala
type T = {
    type X = Int
    def x : X
    type Y
    def y : Y
}

object Foo {
    type X = Int
    def x : X = 5
    type Y = String
    def y : Y = "hello, world!"
}
```

```scala
scala> def test(t : T) = t.x
test: (t: T)t.X
scala> test(Foo)
java.lang.AssertionError: assertion failed: Foo.type

scala> def test(t : T) : t.X = t.x
test: (t: T)t.X
scala> test(Foo)
java.lang.AssertionError: assertion failed: Foo.type
```
- scala 不允許方法使用的型別路徑相依於方法其他參數 (Scala doesn’t allow a method to be defined such that the types used are path-dependent on other arguments to the method.)

```scala
scala> def test(t : T) : T#X = t.x
test: (t: T)Int

scala> test(Foo)
res1: Int = 5
```
- `T#X` 是個合法型別，且編譯器明確知道它是個 `Int` 型別

```scala
scala> def test2(t : T) : T#Y = t.y
test2: (t: T)AnyRef{type X = Int; def x: this.X; type Y; def y: this.Y}#Y

scala> test2(Foo)
res2: AnyRef{type X = Int; def x: this.X; type Y; def y: this.Y}#Y = hello, world!
```
- `T#Y` 編譯器無法判斷 `Y` 的型別，所以把它當成絕對最小型別來用，也就是 `Any`

#### Listing 6.5 Path-dependent and structural types
```scala
object Foo {
    type T = {
        type U
        def bar : U
    }

    val baz : T = new {
        type U = String
        def bar : U = "hello, world!"
    }
}
```
- `val baz` 在城市整個生命週期中都不會改變，是穩定的

```scala
scala> def test(f : Foo.baz.U) = f
test: (f: Foo.baz.U)Foo.baz.U

scala> test(Foo.baz.bar)
res1: Foo.baz.U = hello, world!
```
- `test` 可以接受 `Foo.baz.U` 作為參數，因為路徑依賴型別 `U` 定義在穩定的路徑上

當遇到路徑依賴故障，想辦法讓編譯器知道依賴的型別是“穩定的”

#### `Observer`
通過路徑依賴類型，強制這個引用只能對元 `Observer` 實例有效

Observable.scala:
```scala
trait Observable {
    type Handle

    protected var callbacks = Map[Handle, this.type => Unit]()

    def observe(callback: this.type => Unit): Handle = {
        val handle = createHandle(callback)
        callbacks += (handle -> callback)
        handle
    }

    def unobserve(handle: Handle): Unit = {
        callbacks -= handle
    }

    protected def createHandle(callback: this.type => Unit): Handle 

    protected def notifyListeners(): Unit =
        for(callback <- callbacks.values) callback(this)
}
```
- `Handle` 抽象型別：用來引用被註冊的觀察者 callback 函數 (`handle -> callback`)
    - `callbacks` 是個 `Map`，key 是 `Handle`, value 是 callback type
- `observe` 方法接受型別為 `this.type => Unit` 的參數，返回 `Handle` 型別
    - `this.type` 指向當前物件的型別
    - 與直接引用當前型別不同，`this.type` 會隨繼承而變化
- `unobserve` 從 `callbacks` 移除 handle 指向的 callback function
    - `handle` 是路徑依賴，只能與當前物件註冊過的
    - 不同 `Observable` 的 handle 不可互換
- `createHandle` 方法由子型別定義，讓觀察模式的實現者提供自己的 handle 機制

DefaultHandles.scala:
```scala
trait DefaultHandles extends Observable {
    type Handle = (this.type => Unit)

    protected def createHandle(callback: this.type => Unit): Handle = callback
}
```
- `DefaultHandlees` 繼承 `Observable`
- 定義 `Handle` 與 callback 的型別一樣

IntStore.scala:
```scala
class IntStore(private var value: Int) extends Observable with DefaultHandles {
    def get: Int = value
    def set(newValue: Int): Unit = {
        value = newValue
        notifyListeners()
    }

     override def toString: String = "IntStore(" + value + ")"
}
```
- 當 `IntStore` 內部值發生變化，`notifyListeners()` 就會通知觀察者
- 光 `extends DefaultHandles` 即可，不懂為什麼要 `extends Observable with DefaultHandles`

```scala
scalascala> :load Observable.scala
Loading Observable.scala...
defined trait Observable

scala> :load DefaultHandles.scala
Loading DefaultHandles.scala...
defined trait DefaultHandles

scala> :load IntStore.scala
Loading IntStore.scala...
defined class IntStore

scala> val x = new IntStore(5)
x: IntStore = IntStore(5)

scala> val handle = x.observe(println)
handle: x.Handle = <function1>

scala> x.set(2)
IntStore(2)

scala> x.unobserve(handle)

scala> x.set(4)
```
```scala
scala> val x = new IntStore(5)
x: IntStore = IntStore(5)

scala> val y = new IntStore(5)
y: IntStore = IntStore(5)

scala> val callback = println(_ : Any)
callback: Any => Unit = <function1>

scala> val handle1 = x.observe(callback)
handle1: x.Handle = <function1>

scala> val handle2 = y.observe(callback)
handle2: y.Handle = <function1>

scala> handle1 == handle2
res6: Boolean = true

scala> x.unobserve(handle2)
<console>:18: error: type mismatch;
 found   : y.Handle
    (which expands to)  y.type => Unit
 required: x.Handle
    (which expands to)  x.type => Unit
       x.unobserve(handle2)
                   ^
```
- 路徑依賴類型限制 handle 必須是從同個方法生成的 (The path-dependent typing restricts our handles from being generated from the same method.)
- 儘管兩個 handle 在運行時是相等的，型別系統仍然阻止用錯誤的 handle 來取消觀察者 (Even though the handles are equal at runtime, the type system has prevented us from using the wrong handle to unregister an observer.)

## 6.2 型別約束 (Type constraints)

型別約束是與型別相關的規則，一個變量要匹配一個型別必須符合的規則。有兩種形式
- 下界 (子型別約束)
- 上界 (超型別約束)

### 下界 (Lower bound)

所選擇的型別必須等於下界或是下界的超型別。 (be equal to or a supertype of the lower bound restriction)

```scala
class A {
    type B >: List[Int]
    def foo(x : B) = x
}
```
- `type B` 下界為 `List[Int]`
  - 限定型別 `B` 編譯時期信息必須是來自 `List` 或是 `List` 的超類 

```scala
scala> val x = new A { type B = Traversable[Int] }
x: A{type B = Traversable[Int]} = $anon$1@458ad742

scala> x.foo(Set(1))
res0: x.B = Set(1)
```
- `x` 為 `class A` 的匿名子類
    - `type B` 明確定義為 `Traversable[Int]`
- `Traversable` 是 `List` 父類
- `Set` 不是 `List` 的超類而是 `Traversable` 的子類，`Set` 可以多態形式當成 `Iterable` 或 `Traversable` 來引用。 (a Set could be polymorphically referred to as Iterable or Traversable.)
  - 多態意味當需要編譯時型別為 `Traversable` 時，可以使用 `Set` 的實例，因為 `Set` 繼承自 `Traversable` 
  - 當這麼做，沒有拋棄物件的任何行為，只是丟棄對該型別一部分編譯時訊息

```scala
scala> val y = new A { type B = Set[Int] }
<console>:11: error: overriding type B in class A with bounds >: List[Int];
 type B has incompatible type
       val y = new A { type B = Set[Int] }
```
- 不能將 `type B` 設置為 `Set[Int]`，去創建一個 `A` 的子類別

### 上界 (Upper bound)

所選的型別必須等於或低於上界約束的型別。(be equal to or a lower than the upper bound type)
- 對 class 或 trait 來說，選中的型別必須是上界的 class 或 trait 的 subclass
- 對 結構化型別 來說，選中的型別必須符合 結構化型別 的要求，可以帶更多訊息 (只能多、不能少)

## 6.3 型別參數與高階型別 (Type parameters and higher-kinded types)

### 6.3.1 型別參數約束 (Type parameter constraints)

### 6.3.2 高階型別 (Higher-kinded types)

## 6.4 變異性 (Variance)

### 6.4.1 進階變異性注解 (Advanced variance annotations)

## 6.5 存在型別 (Existential types)

### 6.5.1 存在型別的正式句法 (The formal syntax of existential types)

## 6.6 結論 (Summary)
