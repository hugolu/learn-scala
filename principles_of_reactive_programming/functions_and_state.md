# Functions and State

到目前為止，我們的程式都是沒副作用的 (side-effect free)，因此*時間*的概念不重要。對所有運行結束的程式，任何執行的順序都不會影響結果。這也反映在計算的取代模型 (substitution model)。

## Substitution Model
所謂取代模型，就是程式可以用重寫 (rewriting) 來求值。

如果函數的定義是
```scala
def f(x1, x2, ..., xn) = B;
```

呼叫函數求值
```scala
f(v1, v2, ..., vn)
```

等同於用 `v1` 取代 `x1`，`v2` 取代 `x2`...
```scala
[v1/x1, v2/x2, ..., vn/xn]B
```

例如下面範例
```scala
def iterate(n: Int, f: Int => Int, x: Int): Int = 
  if (n == 0) x else iterate(n-1, f, f(x))

def square(x: Int) = x * x

iterate(1, square, 3)                           //> res0: Int = 9
```

`iterate(1, square, 3)` 可以用 substitution model 重寫
```scala
if (1 == 0) 3 else iterate(1-1, square, square(3))
```
```scala
iterate(1-1, square, square(3))
```
```scala
iterate(0, square, square(3))
```
```scala
iterate(0, square, 3 * 3)
```
```scala
iterate(0, square, 9)
```
```scala
if (0 == 0) 9 else iterate(0-1, square, square(3))
```
```scala
9
```

觀察重寫 `if (1 == 0) 3 else iterate(1 - 1, square, square(3))` 的過程
- 可以寫成 `iterate(0, square, square(3))`
- 也可寫成 `if (1 == 0) 3 else iterate(1 - 1, square 3 * 3)`
- 兩者都會得到 `9` 的答案

## Stateful Objects
這個世界由物件構成，每個物件都有隨著時間變化的狀態。

如果物件會被本身的歷史影響，就是擁有狀態 (state) 的物件。

例如，你的戶頭可以提領100元嗎？這個問題的答案取決於戶頭過去的歷史。

每個可變的狀態由變數構成
```scala
var x: String = "abc"
var count = 111
```

透過分配 (assignment) 給值，改變變數的內容
```scala
x = "hi"
count = count + 1
```

### State in Objects

```scala
class BankAccount {
  private var balance = 0
  def deposit(amount: Int): Unit = {
    if (amount > 0) balance = balance + amount
  }
  def withdraw(amount: Int): Int =
    if (0 < amount && amount <= balance) {
      balance = balance - amount
      balance
    } else throw new Error("insufficient funds")
}
```

`BankAccount` 定義 `balance` 的變數，表示目前帳戶的餘額。`deposit` 與 `withdraw` 方法會透過賦值改變餘額。注意，`balance` 是私有類別的變數不能從外面直接存取。

建立一個帳戶
```scala
val account = new BankAccount
```

以下是對帳戶的操作
```scala
account deposit 50
account withdraw 20                             //> res1: Int = 30
account withdraw 20                             //> res2: Int = 10
account withdraw 15                             //> java.lang.Error: insufficient funds
```
- 同樣操作，得到不同結果。很明顯，`account`是具有狀態的物件。

### 狀態 (Statefulness) 與變數

回憶Functional Programming Principles in Scala第七週介紹 `Stream` 表示 lazy sequence。除了用 lazy val (`tl: => Stream[T]`) 延遲求值，如果也使用變數儲存 non-empty stream，程式像下面這樣：

```scala
def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
  def head = hd
  private var tlOpt: Option[Stream[T]] = None
  def tail: T = tlOpt match {
    case Some(x)  => x
    case None     => tlOpt = Some(tl); tail
  }
}
```
- `tl: => Stream[T]` 使用 call by name，延遲求值
- `tlOpt` 儲存 `Option[Stream[T]]` 變數，預設是 `None`
- 呼叫 `tail` 取值時
  - 如果為 `Some(x)`，表示之前求值過，直接回傳內容
  - 如果為 `None`，表示之前未求過值，使用 `Some()` 包裝 `tl`，遞歸呼叫 `tail` 求值
  - 使用 `Option` 保存 `tl` 求值內容，不需每次都要再重新求值

問題：`cons` 是有狀態的物件嗎？
- No - `Stream` 不是擁有狀態的物件
- Yes - 第二次呼叫 `tail` 時答案來自第一次計算儲存下來的結果，也就是說 `tail` 操作過程取決於發生過的歷史。


### 狀態 (Statefulness) 與變數 (2)

考慮以下類別
```scala
class BankAccountProxy(ba: BankAccount) {
  def deposit(amount: Int): Unit = ba.diposit(amount)
  def withdraw(amount: Int): Unit = ba.withdraw(amount)
}
```

問題：`BandAccountProxy` 的物件是擁有狀態的物件嗎？
- Yes - 雖然 `BandAccountProxy` 沒有包含任何變數，但他的行為卻受 `ba` 的歷史所影響。

___
### [補充] Functional Programming Principles in Scala: Lecture 7.2 - Streams

```scala
trait Stream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: Stream[A]
}

object Stream {
  def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
    def isEmpty = false
    def head = hd
    def tail = tl
  }
  val empty = new Stream[Nothing] {
    def isEmpty = true
    def head = throw new NoSuchElementException("empty.head")
    def tail = throw new NoSuchElementException("empty.tail")
  }
}
```
