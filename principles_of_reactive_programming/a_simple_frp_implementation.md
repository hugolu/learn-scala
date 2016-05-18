# A Simple FRP Implementation

### 一個簡單的 FRP 實作


承續[上一堂課](https://github.com/hugolu/learn-scala/blob/master/principles_of_reactive_programming/functional_reactive_programming.md)，現在來實作 `Signal` 跟 `Var`，成為函數式響應編程 (FRP) 的基礎。

這類別被包裝在 `frp` 中，接下來介紹使用介面 API。

### Summary: The Signal API

```scala
class Signal[T](expr: => T) {
  def apply(): T = ???
}

object Signal {
  def apply[T](expr: => T) = new Signal(expr)
}
```
- `class Signal` 接受 `expr` 參數，提供 `apply` 方法。`expr` 可以用在未來的時間點上求值
- `object Signal` 提供 `apply` 方法，用來創建 `Signal`，例如 `Signal(expr)`

### Summary: The Var API

```scala
class Var[T](expr: => T) extends Signal[T](expr) {
  def update(expr: => T): Unit = ???
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}
```
- `class Var` 是 `class Signal` 子類，多了 `update` 方法，這方法接受 `expr` 參數，從現在開始可以用來求值
- `object Var` 提供 `apply` 方法，用來創建 `Var`，例如 `Var(expr)`

### 實作的想法

每個 Signal 維護三個意圖
- 當下 `Signal` 的值
- 當下用來定義 `Signal` 值的表示式
- 一組觀察者 (observers): 相依於它的值的其他 `Signal`

然後，如果它的值改變了，所有觀察者需要被重新求值 (re-evaluated)

### 相依性維護

如何紀錄觀察者的相依性？

- 當訊號求值，需要知道哪些訊號呼叫者(signal caller)在表示式中被定義或被更新。
- 如果能知道這些，當執行 `sig()` 時就表示把呼叫者加入 `sig` 的觀察者名單。
- 當訊號值產生變化，所有先前觀察的訊號都要重新求值，然後清除 `sig.observers`。
- 重新求值會再重新把呼叫訊號呼叫者加入 `sig.observers`，只要呼叫者的值仍然相依於 `sig`。

### 誰在呼叫?

如何發現「訊號表示式被求值」？
(How do we find out on whose behalf a signal expression is evaluated?)

一個簡化的方法是維護一個目前呼叫者的全域資料結構 (稍後討論優化)。

資料結構以堆疊方式存取，因為訊號求值可能會觸發其他訊號。

### Stackable Variables

這是 `StackableVariable` 類別：
```scala
class StackableVariable[T](init: T) {
  private var values: List[T] = List(init)
  def value: T = values.head
  def withValue[R](newValue: T)(op: => R): R = {
    values = newValue :: values
    try op finally values = values.tail
  }
}
```

可以像這樣存取
```scala
val caller = new StackableVariable(initialSig)
call.withValue(otherSig){...}
... caller.value ...
```

### Set Up in Object Signal

we also evaluate signal expression at the top-level when there is no other signal that's defined or updated.

We use the "sentinel" object NoSignal as the caller for these expressions.

Together:
```scala
object NoSignal extends Signal[Nothing](???) { ... }

object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal)
  def apply[T](expr: => T) = new Signal(expr)
}
```

### The Signal Class

```scala
class Signal[T](expr: => T) {
  import signal._
  private var myExpr: () => T = _
  private var myValue: T = _
  private var observers: Set[Signal[_]] = Set()
  update(expr)
  
  protected def update(expr: T): Unit = {
    myExpr = () => expr
    computeValue()
  }

  protected def computeValue(): Unit = {
    myValue = caller.withValue(this)(myExpr())
  }
  
  def apply() = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }
}
```

### Exercise

The Signal class still lacks an essential part. Which is it?

- Error handling
- Reevaluating callers
- Constructing observers

### Reevaluating Callers

A signal's current value can change when
- somebody calls an update operation on a Var, or
- the value of a dependent signal changes
 
Propagating requires a more refined implementation of computeValue:

```scala
protected def computeValue(): Unit =
  myValue = caller.withValue(this)(myExpr())
```

```scala
protected def computeValue(): Unit = {
  newValue = caller.withValue(this)(myExpr())
  if (myValue != newValue) {
    myValue = newValue
    val obs = observers
    observers = Set()
    obs.foreach(_.computeValue())
  }
}
```

### Handling NoSignal

computeValue needs to be disabled for NoSignal because we cannnot eveluate an expression of type Nothing:

```scala
object NoSignal extends Signal[Nothing](???) {
  override def computeValue() = ()
}
```

### Handling Vars

Recall the Var is a Signal which can be updated by the client program.

In face, all necessary functionality is already present in class Signal; we just need to expose it:

```scala
class Var[T](expr: => T) extends Signal[T](expr) {
  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}
```

### The Signal Class

```scala
class Signal[T](expr: => T) {
  import signal._
  private var myExpr: () => T = _
  private var myValue: T = _
  private var observers: Set[Signal[_]] = Set()
  update(expr)
  
  protected def update(expr: => T): Unit = {
    myExpr = () => expr
    computeValue()
  }
  
  protected def computeValue(): Unit = {
    myValue = caller.withValue(this)(myExpr())
  }
}
```

### Discussion

Our implementation of FRP is quite stunning in its simplicity.

But you might argue that it is too simplistic.

In particular, it makes use of the worst kind of state: global state.

```scala
object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal)
  ...
}
```

Our immediate problem is: What happens if we try to evaluate serval signal expression in parallel?

### Thread-Local State

Our way to get around the problem of concurrent accesses to global state is to use synchronization.

But this block threads, can be slow, and can lead to deadlocks.

Another solution is to replace global state by thead-local state.
- Thread-local state means that each thread accesses a separate copy of a variable.
- It is supported in Scala through class `scala.util.DynamicVariable`.

### Using Thread-Local State

The API of DynamicVariable matches the one of StackableVariable.

So we can simply swap it into our Signal implementation:

```scala
object Signal {
  private var call = new DynamicVariable[Signal[_]](NoSignal)
  ...
}
```

### Another Solution: Implicit Parameters

Thread-local state still comes with a number of disadvantages:
- It's imperative nature often produce hidden dependencies which are hard to manage.
- Its implementation on the JDK involves a global hash table lookup, which can be a performance problem.
- It does not play well in situations where threads are multiplexed between serval tasks.

A cleaner solution involves implicit parameters.
- Instead of maintaining a thread-local variable, pass its current value into a signal expression as an implicit parameter.
- This is purely functional. But it currently requires more boilerplate that the thread-local solution.
- Future version of Scala might solve that problem.

### Summary

We have given a quick tour of functional reactive programming, with some usage examples and an implementation.

This is a just a taster, there's much more to be discovered.

In particular, we only covered one particular style of FRP: Discrete signals changed by events.

Some variants of FRP also treat continuous signals.

Values in these systems are often computed by sampling instead of event propagation. 
