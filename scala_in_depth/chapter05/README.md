# Chapter 5 用隱喻方式寫出有表達力的程式碼

Scala 編譯器會在編譯期間推測使用者沒有明確寫出來的訊息

- missing parameter: 方法或建構函數遺漏參數
- missing conversion: 型別間沒有明確指出如何轉換，或是呼叫方法接收的物件需要轉換

## 5.1 簡介隱喻 (implicit)

`implicit` 關鍵字有兩種用法

- 方法與參數定義：告訴編譯器解析隱喻時，可以使用這些方法或參數定義
- 方法參數列表：告訴編譯器參數列表可能遺缺，編譯器應該在解析隱喻 (implicit resolution) 時把他們找出來

```scala
scala> def findAnInt(implicit x : Int) = x
findAnInt: (implicit x: Int)Int
```
定義 `findAnInt` 方法，用 `implicit` 標示 `x` 參數，表示如果呼叫者沒提供，編譯器要在隱喻範圍 (implicit scope) 搜尋 `Int` 的變數

```scala
scala> findAnInt
<console>:12: error: could not find implicit value for parameter x: Int
       findAnInt
       ^
```
在沒有提供隱喻值情況下，呼叫沒提供參數會發生錯誤

```scala
scala> implicit val test = 5
test: Int = 5
```
使用 `implicit` 宣告 `test` (名字隨便取)，所以將來解析隱喻就能找到整數的隱喻值

```scala
scala> findAnInt
res1: Int = 5
```
呼叫 `findAnInt` 沒有提供參數，編譯器使用隱喻值

```scala
scala> findAnInt(2)
res2: Int = 2
```
當然，在有提供參數的情況下，就會使用提供的參數

### 5.1.1 識別符 (identifier)

探索隱喻解析機制前，先理解編譯器如何在特定範圍解析識別符。
