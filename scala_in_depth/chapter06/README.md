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

### 6.1.3 結構化型別 (Structural types)

## 6.2 型別限制 (Type constraints)

## 6.3 型別參數與高階型別 (Type parameters and higher-kinded types)

### 6.3.1 型別參數限制 (Type parameter constraints)

### 6.3.2 高階型別 (Higher-kinded types)

## 6.4 變異性 (Variance)

### 6.4.1 進階變異性注解 (Advanced variance annotations)

## 6.5 存在型別 (Existential types)

### 6.5.1 存在型別的正式句法 (The formal syntax of existential types)

## 6.6 結論 (Summary)
