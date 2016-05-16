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
