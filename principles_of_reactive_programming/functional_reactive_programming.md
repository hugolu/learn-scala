# Functional Reactive Programming

Reactive programming 是針對時間上一系列的事件的反應。

從函數觀點來看：聚合事件序列成為一個訊號 (signal)
- 一個訊號是隨著時間改變的值
- 表示從時間域對應到值域的函數
- 根據現有的訊號定義新的訊號，而不是逐一傳播更新可變狀態 (Instead of propagating updates to mutable stete, we define new signals in terms of ones.)

### 範例：滑鼠位置

事件為主的觀點 (Event-based view):
- 當滑鼠移動，事件 `MouseMoved(toPos: Position)` 被發出。

函數式響應編程 (FRP):
- 一個訊號 `mousePosition: Signal[Position]` 表示任何時間點當下滑鼠的位置。

### FRP 的起源

FRP started in 1997 with the paper Functional Reactive Animation by Conal Elliott and Paul Hudak and the Fran library.

There have been many FRP systems since, both standalone languages and embedded libraries.

Some examples are: Flapjax, Elm, Bacon.js, React4J.

Event streaming dataflow programming system such as Rx (which we will see in two weeks), are related but the term FRP is not commonly used for them.

We will introduce FRP by means of a minimal class, `frp.Signal` whose implementation if explained at the end of this module.

`frp.Signal` is modelled after `Scala.react`, which is described in the paper Deeprecating the Observer Pattern.

### 基本的訊號操作

有兩個基本的訊號操作
1. 得到目前訊號的值。在函式庫中表示為`()`，例如 `mousePosition()` 取得目前滑鼠位置
2. 用其他訊號定義訊號。在函式庫中表示為建構函數 (constructor)

```scala
def isReactangle(LL: Position, UR: Position): Signal[Booleam] = 
  Signal {
    val pos = mousePosition()
    LL <= pos && pos <= UR
  }
}
```
- LL(lower left), UR(upper right) 定義一個矩形的左下、右上的點
- 如果滑鼠的點落在範圍之內 (LL <= pos <= UR)，`inReactangle()`的訊號為 `true`。反之，則為 `false`

### 常數訊號 (constant signals)
```scala
val sig = Signal(3)
```
表示訊號恆為3

### 隨時間變動的訊號 (time-varying signals)

怎麼定義隨時間變化的訊號呢？

- 可以外在定義訊號，像是 `mousePosition` 然後在做 `map`
- 或是使用 `Var`

### 可變訊號

訊號的值不可變

但是函式庫定義 `Signal` 子類別 `Var` 可以被改變。

`Var` 提供 `update` 操作，重新定義訊號的值。

```scala
val sig = Signal(3)
sig.update(5)
```
- 完成更新後，`sig` 將回傳5而非3。

### 旁白：更新訊號

在 scala，更新的動作可以用賦值 (assignment) 表示。例如陣列 `arr`
```scala
arr(i) = 0
```
被翻譯成
```scala
arr.update(i, 0)
```
呼叫的更新函式可被想成
```scala
class Array[T] {
  def update(idx: Int, value: T): Unit
  ...
}
```

一般來說，indexed assignment 像是 `f(E1, ..., En) = E` 被翻譯成 `f.update(E1, ..., En, E)`。如果`n=0`:`f.update()=E` 簡寫為`f()=E`。因此 `sig.update(5)` 被縮寫為 `sig() = 5`。

### 訊號與變數

`Var` 型別的訊號看起來有點像變數，`sig()` 被取值 (dereferencing)，`sig() = newValue` 被更新。

但有個關鍵的差異：訊號間的關係可以自動維護，在未來所有時間點上。但變數沒有這樣的機制，要手動傳遞更新的訊息。

變數：
```scala
a = 2
b = 2 * a
a = a + 1
b               // 不會自動變成6
b = 2 * a       // 必須手動更新
```

訊號：
```scala
a() = 2
b() = 2 * a()
a() = 3
b()             // 會得到6
```

### 範例

用訊號重寫上一節的範例 `BandAccount`，用訊號維護 `balance`，定義 `consolidate` 函數產生給定帳戶的餘額總和。然後跟 publish/subscribe 實作比較，有什麼可節省的東西？

