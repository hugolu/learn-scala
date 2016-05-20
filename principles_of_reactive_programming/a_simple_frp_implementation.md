# A Simple FRP Implementation

### 一個簡單的 FRP 實作


承續[上一堂課](https://github.com/hugolu/learn-scala/blob/master/principles_of_reactive_programming/functional_reactive_programming.md)，現在來實作 `Signal` 跟 `Var`，成為函數式響應編程 (FRP) 的基礎。

這類別被包裝在 `frp` 中，接下來介紹使用介面 API。

### `Signal` API

```scala
class Signal[T](expr: => T) {
  def apply(): T = ???
}

object Signal {
  def apply[T](expr: => T) = new Signal(expr)
}
```
- `class Signal` 接受 `expr` 參數，提供 `apply` 方法。`expr` 可以用在未來的時間點上求值
- `object Signal` 提供 `apply` 方法，用來創建 `Signal`，用法如 `Signal(expr)`

### `Var` API

```scala
class Var[T](expr: => T) extends Signal[T](expr) {
  def update(expr: => T): Unit = ???
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}
```
- `class Var` 是 `class Signal` 子類別，多了 `update` 方法，這方法接受 `expr` 參數用來求值
- `object Var` 提供 `apply` 方法，用來創建 `Var`，用法如 `Var(expr)`

### 實作的想法

每個 Signal 維護三個資料意圖
- 當下 `Signal` 的值
- 當下用來定義 `Signal` 值的表示式
- 一組觀察者 (observers): 相依於它的值的其他 `Signal`

然後，如果它的值改變了，所有觀察者需要被重新求值 (re-evaluated)

### 相依性維護

```scala
val a = Var(0)    //> 0
val b = Var(0)    //> 0
b() = a() + 1     //> 1
a() = 2           //> 2
b()               //> 3
```

如何紀錄觀察者的相依性？

- 當訊號求值，需要知道哪些訊號呼叫者(signal caller)要被定義或根據表示式被更新。
- 如果能知道這些，當執行 `sig()` 時就表示把呼叫者加入 `sig` 的觀察者名單。
  - `b() = a() + 1`: 把 `b` 加入 `a` 的觀察者名單
- 當訊號值產生變化，所有先前觀察的訊號都要重新求值，並清除 `sig.observers`。
  - `a() = 2`，要通知 `b` 重新求值 (`a() + 1`)
- 只要呼叫者的值仍然相依於 `sig`，重新求值會再把呼叫訊號的呼叫者加入 `sig.observers`。
  - `b() = a() + 1`: 把 `b` 加入 `a` 的觀察者名單

### 誰在呼叫?

如何發現「訊號表示式被求值」？
(How do we find out on whose behalf a signal expression is evaluated?)

一個簡化的方法是維護一個目前呼叫者的全域資料結構 (稍後討論優化)。

資料結構以堆疊方式存取，因為訊號求值可能會觸發其他訊號。

### `StackableVariable`

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
- `values = newValue :: values` 把新值 (新訊號) 加到 `values` 開頭
- `try op finally values = values.tail` 執行 `op` 後把剛加入的新值移走

可以像這樣存取
```scala
val caller = new StackableVariable(initialSig)
caller.withValue(otherSig){ ... }
... caller.value ...
```
- `caller` 是個有某初始訊號的 `StackableVariable` 
- 使用 `withValue(otherSig){ ... }` 更新 `caller`，藉由傳入某些訊號與某些表示式

範例：
```scala
val a = Var(0)
val b = Var(0)

b() = a() + 1
```
- `b() = a() + 1`
  - `caller.values` = `b :: NoSignal`
  - `try op` 對 `a() + 1` 求值，得到 `1`
  - `finally values = values.tail`，得到 `values = NoSignal` 結果

### `Signal` 伴生物件的設定

對最上層對訊號表示式求值，因為那裡沒有訊號被定義或更新，所以使用 `NoSignal` 當作這些表示式的呼叫者。

```scala
object NoSignal extends Signal[Nothing](???) { ... }

object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal)
  def apply[T](expr: => T) = new Signal(expr)
}
```
- `NoSignal` 當作初始值，這是個特別的訊號，因為沒有值所以擴充自 `Signal[Nothing]`，也因為沒有實作所以使用 `(???)`
- `caller` 放在 `object Signal` 裡面作為一個全域變數，用來維護呼叫者名單
- `StackableVariable[Signal[_]]` 表示 `StackableVariable` 可以接受任何類型的 `Signal` 

### `Signal` 類別

```scala
class Signal[T](expr: => T) {
  import Signal._
  private var myExpr: () => T = _
  private var myValue: T = _
  private var observers: Set[Signal[_]] = Set()
  update(expr)
```
- `import Signal._` 這樣才看得見 `caller` 這個全域變數
- `myExpr`存放表示式, `myValue`存放表示式求得的值, `observers` 存放觀察者清單 (用 `Set` 理由很簡單，不希望記錄到重複的觀察者)
- 初始化時，這些尚未確定，需透過 `update(expr)` 給值

```scala
  protected def update(expr: => T): Unit = {
    myExpr = () => expr
    computeValue()
  }
```
- `update(expr)` 設定 `myExpr` 表示式，然後呼叫 `computeValue()` 求值

```scala
  protected def computeValue(): Unit = {
    myValue = caller.withValue(this)(myExpr())
  }
```
- 簡單用目前訊號當作呼叫者計算目前表示式，並把結果寫到 `myValue`
- 例如，操作 `a()=expr` 等同於 `a.update(expr)`，然後 `computeValue` 呼叫 `caller.withValue(this)(expr)` 把自己跟求值式傳給 `caller`

```scala
  def apply() = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }
}
```
- `apply()` 回傳當前訊號的值
- 把 `caller` 的值 (signal) 加到觀察者名單
- 避免 `observers` 包含自己造成循環呼叫
  - 例如 `S() = S() + 1`，左邊的 `S()` 是呼叫者，加入右邊 `S()` 的觀察者清單，當右邊表示式求值更新到左邊 `S()` ，然後右邊 `S()` 又發生變化導致表示式重新求值，如此循環下去沒完沒了
- 最後回傳 `myValue`

範例一：
```scala
val a = Var(0)
val b = Var(0)
val c = Var(0)
 
b() = a() + 1
c() = b() + 1
c()                                             //> res1: Int = 2
 
a() = 1
c()                                             //> res1: Int = 3
```
| Expression | Val | myExpr | myValue | observers |
|------------|-----|--------|---------|-----------|
| `val a = Var(0)` | `a` | `0` | `0` | |
| `val b = Var(0)` | `b` | `0` | `0` | |
| `val c = Var(0)` | `c` | `0` | `0` | |
| `b() = a() + 1`  | `a` | `0` | `0` | `b` |
|                  | `b` | `a() + 1` | 1 | |
| `c() = b() + 1`  | `b` | `a() + 1` | 1 | `c` |
|                  | `c` | `b() + 1` | 2 | |
| `c()`            | `c` | `b() + 1` | 2 | |
| `a() = 1`        | `a` | `1` | `1` | `b` |
|                  | `b` | `a() + 1` | 2 | `c` |
|                  | `c` | `b() + 1` | 3 | |
| `c()`            | `c` | `b() + 1` | 3 | |

範例二：
```scala
S() = S() + 1                                   //> java.lang.AssertionError: assertion failed: cyclic signal definition
```
- `S() = ` 把 `S` 加到 caller.value
- `S()` 檢查 `!caller.value.observers.contains(this)`，caller 不能不包含自己，所以 assert 拉起

### 練習

`Signal` 類別還缺少哪個重要的部分？

- [ ] 錯誤處理 (Error handling)
- [x] 對呼叫者重新求值 (Reevaluating callers)
- [ ] 建構觀察者 (Constructing observers)

### 對呼叫者重新求值

當以下情形發生，訊號當下的值可被改變
- 有人對 `Var` 執行 `update` 操作
- 相依訊號的值發生變化

為了傳播變化要再精進 `computeValue` 的實作：

這是舊的，
```scala
protected def computeValue(): Unit =
  myValue = caller.withValue(this)(myExpr())
```

這是新的，
```scala
protected def computeValue(): Unit = {
  val newValue = caller.withValue(this)(myExpr())
  if (myValue != newValue) {
    myValue = newValue
    val obs = observers
    observers = Set()
    obs.foreach(_.computeValue())
  }
}
```
- 先求值計算出 `newValue`
- 如果 `myValue` 與 `newValue` 不同
  - 更新 `myValue`
  - 取出觀察者清單暫存到 `obs`，清空觀察者清單，然後呼叫每個觀察者求值

### 處理 `NoSignal`

要讓 `computeValue` 對 `NoSignal` 沒有反應，因為無法對 `Nothing` 的表示式求值

```scala
object NoSignal extends Signal[Nothing](???) {
  override def computeValue() = ()
}
```

### 處理 `Var`

回憶一下，`Var` 是個可以被更新的訊號

事實上，所有需要的功能都在 `class Signal` 了，只要顯露出來即可

```scala
class Var[T](expr: => T) extends Signal[T](expr) {
  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}
```
- `Var` 父類別的 `update()` 使用 `protected` 保護，子類可存取 `super.update(expr)`，但外界不能直接使用 `Signal` 的 `update`。所以覆寫父類別的 `update`，提供一個外界可存取的 `update` 方法

### 討論

這個 FRP 實作簡單的讓人驚嘆，但你可能會爭論太簡單了。事實上它使用了最糟糕的「全域」狀態

```scala
object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal)
  ...
}
```

當前的問題是：如果嘗試平行地對信號的表達求值會發生什麼？

### 執行緒局部狀態 (Thread-Local State)


使用同步方法繞過這個存取全域狀態的問題。但阻斷執行緒運行會讓程式變慢，也會導致死鎖(deadlocks)。

另一個解法是用執行緒局部狀態取代全域狀態
- 執行緒局部狀態表示每個執行緒存取變數的複製品
- Scala 透過類別 `scala.util.DynamicVariable` 支援執行緒局部變數

### 使用執行緒局部狀態

`DynamicVariable` API 吻合 `StackableVariable` API，所以簡單地替換剛剛 Signal 上的 `StackableVariable` 即可。

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

### 總結

我們用一些範例與實作，快速瀏覽了函數式響應程式設計。

這只是嚐鮮，還有很多東西沒有挖掘。事實上，我們只涵蓋了 FRP 的特定風格：根據事件改變的離散訊號。

某些 FRP 的變形也處理連續訊號。這些系統的值通常根據取樣來計算而不是事件的傳播。
___

完整程式如下：
```scala
package myTest

object myTest {
  val a = Var(0)                                  //> a  : myTest.Var[Int] = myTest.Var@4ee6da7b
  val b = Var(0)                                  //> b  : myTest.Var[Int] = myTest.Var@71419cf7
  a() = b() + 1
  a()                                             //> res0: Int = 1
  b() = 2
  a()                                             //> res1: Int = 3
}

class Signal[T](expr: => T) {
  import Signal._
  private var myExpr: () => T = _
  private var myValue: T = _
  private var observers: Set[Signal[_]] = Set()
  update(expr)

  protected def update(expr: => T): Unit = {
    myExpr = () => expr
    computeValue()
  }

  protected def computeValue(): Unit = {
    val newValue = caller.withValue(this)(myExpr())
    if (myValue != newValue) {
      myValue = newValue
      val obs = observers
      observers = Set()
      obs.foreach(_.computeValue())
    }
  }

  def apply() = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }
}

object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal)
  def apply[T](expr: => T) = new Signal(expr)
}

class StackableVariable[T](init: T) {
  private var values: List[T] = List(init)
  def value: T = values.head
  def withValue[R](newValue: T)(op: => R): R = {
    values = newValue :: values
    try op finally values = values.tail
  }
}

class Var[T](expr: => T) extends Signal[T](expr) {
  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}

object NoSignal extends Signal[Nothing](???) {
  override def computeValue() = ()
}
```
