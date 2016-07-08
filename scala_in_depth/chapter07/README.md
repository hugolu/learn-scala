# Chapter 7 - 隱式轉換與型別系統結合應用

型別系統與隱式解析提供編寫有表達力且型別安全的軟件所需的工具。
- 隱式轉換把型別編碼進運行的物件
- 隱式轉換允許創建型別類別用來抽象多個類別的行為
- 隱式轉換能用來直接編碼型別約束，遞歸構造型別
- 結合型別構造器與型別邊界，隱式轉換和型別系統能被用來把複雜的問題直接編碼進型別系統
- 隱式轉換能用來保持信息，在保持抽象接口的同時把行為代理給特定類型的實現
- 編寫在需要時能重用的類別與方法

## 7.1 上下文邊界與視圖邊界 (Context bounds and view bounds)

視圖邊界 (view bound) 用來要求一個可用的隱式視圖來轉換另一個型別為另個型別。
```scala
def foo[A < % B](x : A) = x
```
- 參數 `x` 的型別為 `A`，在呼叫的地方必須存在隱式轉換 `A => B`

上下文邊界 (context bound) 聲明必須要有一個給定的型別的隱士值存在。
```scala
def foo[A : B](x : A) = x
```
- 參數 `x` 的型別為 `A`，且呼叫 `foo` 方法時必須有可用的隱式值 `B[A]` 存在。

### 7.1.1 何時使用隱式型別約束 (When to use implicit type constraints)
隱式視圖常用於擴展已存在的類型。

```scala
scala> def first[T](x : Traversable[T]) = (x.head, x)
first: [T](x: Traversable[T])(T, Traversable[T])

scala> first(Array(1,2))
res0: (Int, Traversable[Int]) = (1,WrappedArray(1, 2))
```
- 方法為集合的元素定義了型別參數 `T`，接受 `Traversable[T]` 型別的參數，返回集合第一個元素與集合本身。
- 呼叫方法時，結果型別是 `Traversable[T]`，但運行時類型卻是 `WrappedArray` ⇒ 方法遺失了陣列初始型別的資訊。

在缺乏泛型與邊界的情況，多態通常造成型別訊息遺失。
使用 scala，能在使用泛型方法的同時保持特定的型別。

上下文邊界與視圖邊界允許用簡單的方式確保複雜的型別約束。應用他們的最佳場合是當方法不需要通過名字訪問捕獲的型別，但又需要在作用域里存在可用的隱式轉換的時候。

```scala
def sendMsgToEach[A : Serializable](receivers : Seq[Receiver[A]], a : A) = {
    receivers foreach (_.send(a))
}
```
- `sendMsgToEach` 接受帶有“可序列化”隱式上下文的類型 `A`，和類型 `A` 的 `Receiver` 的序列
- `sendMsgToEach` 的實現對每個 `receiver` 呼叫 `send`，把 `message` 傳給它們
- `sendMsgToEach` 方法不處理 `message`，但 `Receiver` 的 `send` 方法實現需要參數為 `Serializable` 型別

上下文邊界與視圖邊界用於明確隱式參數的目的。隱式參數能用於從型別系統裡捕捉關係。

## 7.2  使用隱式轉換捕捉型別 (Capturing types with implicits)

Manifest 是由編譯器在必要時生成的型別資訊，用來記錄當時編譯器對該類型所知的全部訊息。

隱式型別約束是型別間的超類和等價關係 (supertype and qeuivalence relationship between types)。

### 7.2.1 捕捉型別用於運行時計算 (capturing types for runtime evaluation)

JVM 中，每個基礎型別都有不同的陣列型別，還有數個陣列型別是給物件的，例如 `Int[]`, `double[]`, `Object[]`。Java 區分這些型別，也要求程序也區分這些型別。Scala 允許編碼中使用泛型的 `Array[T]` 的型別，由於底層實現必須知道初始陣列的型別，因此 Scala 需要一種方法將訊息附加給泛型型別。

Scala 把運行時行為隱藏在 `Array[T]` 類別與相關方法後面，而不是編碼成不同的型別。

| 類型 | 解釋 |
|------|------|
| `Mainfest`        | 保存與型別 `T` 的反射類實例，以及 `T` 所有型別參數的 `Manifest` 值 |
| `OptManifest`     | `OptManifest` 為可選。有提供，實例是 `Manifest` 的子類；如沒有，實例為 `NoManifest` 類 |
| `ClassManifest`   | 只保存給定型別的刪除了的類別 (it only stores the erased class of a given type) |

### 7.2.2 使用 Manifests (Using Manifests)
### 7.2.3 捕捉型別約束 (Capturing type constraints)
### 7.2.4 特定方法 (Specialized methods)

## 7.3 使用型別類別 (Use type classes)
### 7.3.1 作為型別類別的 FileLike (FileLike as a type class)
### 7.3.2 型別類別的好處 (The benefits of type classes)

## 7.4 用型別系統實現條件執行 (Conditional execution using the type system)
### 7.4.1 異構型別 List (Heterogeneous typed list)
### 7.4.2 IndexedView (IndexedView)

## 7.5結論 (Summary)
