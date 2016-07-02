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
