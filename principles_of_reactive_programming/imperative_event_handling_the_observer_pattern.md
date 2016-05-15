# Imperative Event Handling: The Observer Pattern

觀察者模式 (Observer Pattern) 廣泛應用在 `view` 需要對 `model` 的改變做出反應。

有些變形叫做
- publish/subscribe
- model/view/controller (MVC)

![The Observer and Publish-Subscribe Patterns](http://assets.devx.com/articlefigs/18879.jpg)

### Publisher Trait

```scala
trait Publisher {
  private var subscribers: Set[Subscriber] = Set()

  def subscribe(subscriber: Subscriber): Unit =
    subscribers += subscriber

  def unsubscribe(subscriber: Subscriber): Unit =
    subscribers -= subscriber

  def publish(): Unit =
    subscribers.foreach(_.handler(this))
}
```

###  Subscriber Trait
```scala
trait Subscriber {
  def handler(pub: Publisher)
}
```

### 讓 `BankAccount` 成為一個 `Publisher`
```scala
class BankAccount extends Publisher {
  private var balance = 0
  def currentBalance: Int = balance
  def deposit(amount: Int): Unit = {
    if (amount > 0) {
      balance = balance + amount
      publish()
    }
  }
  def withdraw(amount: Int): Unit =
    if (0 < amount && amount <= balance) {
      balance = balance - amount
      publish()
    } else throw new Error("insufficient funds")
}
```
- 因為 `balance` 是私有變數，所以定義 `currentBalance` 讓呼叫的 Subscriber 可以得 `BankAccount` 的狀態
- 在 `deposit()` 與 `withdraw()` 完成後，透過 `publish()` 通知 Subscriber

### 觀察者要維護一組帳戶的存款餘額總和
```scala
class Consolidator(observed: List[BankAccount]) extends Subscriber {
  observed.foreach(_.subscribe(this))

  private var total: Int = _
  compute()

  private def compute() =
    total = observed.map(_.currentBalance).sum

  def handler(pub: Publisher) = compute()

  def totalBalance = total
}
```
- 觀察者接收一組帳戶(`List[BankAccount]`)，並對每個帳戶訂購資訊 (`observed.foreach(_.subscribe(this))`)
- 擁有一個 `total` 的私有變數，初始化時呼叫 `compute()` 總和每個帳戶的餘額
- 一旦有 publisher 呼叫 `handler()` 就重新計算存款餘額總和
- 提供 `totalBalance` 外界存取存款餘額總和 (`total`)

### 運行範例
```scala
c.totalBalance                                  //> res0: Int = 0
a deposit 20
c.totalBalance                                  //> res1: Int = 20
b deposit 30
c.totalBalance                                  //> res2: Int = 50
```

