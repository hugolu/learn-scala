# Monad Design Pattern in Scala

來源：https://ingramchen.io/blog/2014/11/monad-design-pattern-in-java.html

文章開頭先講一下我的企圖。這陣子一直在看 Scala Monad 的文章，昨天 [Ian Chiu](https://www.facebook.com/ian.chiu621) 丟給我這篇文章，剛好解答了我對 Scala Monad 的某些疑惑，引起我的興趣，雖然看得懂 Java code 但骨子裡還是有點排斥這個囉唆的語言 (個人觀感，不討戰)，索性來把這篇文章的範例翻譯成 Scala 版本，文字解說請參考原文。

以下正文。
___
## Example 1: Optional

有個關於帳號的資料結構如下：
```scala
case class City(name: String)
case class Address(city: City)
case class Account(address: Address)
```

想從 `Account` 逐一取得 `City` 名稱，為避免發生 `NullPointerException` ，要用 `if` 檢查是否為 `null`。
```scala
def getCityName(account: Account): String = {
	if (account != null) {
		if (account.address != null) {
			if (account.address.city != null) {
				return account.address.city.name
			}
		}
	}
	return "Unknown"
}
```

`getCityName` 執行結果如下：
```scala
val acc1 = Account(Address(City("Taipei")))     //> acc1  : myTest.Account = Account(Address(City(Taipei)))
val acc2 = Account(null)                        //> acc2  : myTest.Account = Account(null)

getCityName(acc1)                               //> res0: String = Taipei
getCityName(acc2)                               //> res1: String = Unknown
```

問題來了，這段程式碼不但重複一堆 `if != null`，也出現了 [Message Chains](https://sourcemaking.com/refactoring/smells/message-chains) 的壞味道，更糟糕的是當你忘記檢查 `null`，程式就會爆炸。
```scala
acc2.address.city                               //> java.lang.NullPointerException
                                                //| 	at myTest.test01$$anonfun$main$1.apply$mcV$sp(myTest.test01.scala:23)
                                                //| 	at ...
```

### 解法一
用 Extract Method，把檢查 `null` 的差事都給 `map` 來做
```scala
def map[T, R](value: T, transform: T => R) = {
  if (value != null) transform(value) else null
}                                               //> map: [T, R](value: T, transform: T => R)Any
```

這邊遇到 Java 不會遇到的問題，因為回傳值型別可能是 `R` 也可能是 `Null` (`null` 的型別)，取其共同的父型別就是 `Any`，也就是說 `map` 回傳值型別為 `Any`。這會導致以下程式碼無法成功編譯。
```scala
def getCityName(account: Account) = {
  val address = map(account, { account: Account => account.address })
  val city = map(address, { address: Address => address.city }) //won't compile: type mismatch; found : myTest.Address ⇒ myTest.City required: Any ⇒ myTest.City
  val name = map(city, { city: City => city.name })             //won't compile: type mismatch; found : myTest.City ⇒ String required: Any ⇒ String
  if (name != null) name else "Unknown"
}
```

ㄟ～ 先 pass 好了，我真的不知道怎麼硬套 😓

### 解法二
使用 `Option` 來包裝反覆出現的 `null`。

繼續之前，要先提一下 Scala `Option` 的一些特性。

#### `Option` 定義
- [`Option`](http://www.scala-lang.org/api/current/#scala.Option) 是個抽象類別
- [`Some`](http://www.scala-lang.org/api/current/#scala.Some) 繼承 `Option`，表示裡面有東西
- [`Noen`](http://www.scala-lang.org/api/current/#scala.None$) 也是繼承 `Option`，但裡面放 [`Nothing`](http://www.scala-lang.org/api/current/#scala.Nothing) 空空如也

```scala
val opt1 = Option("1234")                       //> opt1  : Option[String] = Some(1234)
val opt2 = Option(null)                         //> opt2  : Option[Null] = None
val opt3 = Some(null)                           //> opt3  : Some[Null] = Some(null)
```
- `Option("1234")` 等於 `Some("1234")`
- `Option(null)` 會被解釋成 `None`
- `Some(null)` 不是 `None`，它表示 `Some` 裡面的值是 `null`

#### `Option` 的 `map`

嘗試使用 `parseInt` 轉換 `Option` 的內容。[`Integer.parseInt`](http://www.tutorialspoint.com/java/lang/integer_parseint.htm) 接受字串，回傳數字。如果不能解析，會丟出 `NumberFormatException` 例外。

```scala
opt1.map(Integer.parseInt)                      //> res0: Option[Int] = Some(1234)
opt2.map(Integer.parseInt)                      //> res1: Option[Int] = None
opt3.map(Integer.parseInt)                      //> java.lang.NumberFormatException: null
                                                //| 	at java.lang.Integer.parseInt(Integer.java:454)
                                                //| 	at ...
```
- `opt1 = Some(1234): Option[String]` 經過 `map` 得到 `Some(1234): Option[Int]`
- `opt2 = None: Option[Null]` 經過 `map` 得到 `None: Option[Null]` (`Null` 再怎麼 `map` 還是 `Null`)
- `opt3 = Some(null): Some[Null]` 進行 `map` 發生例外 (`parseInt` 無法解析 `null`)

#### `Option` 的 `flatMap`
- `Option` 的 `map` 定義：`final def map[B](f: (A) ⇒ B): Option[B]`
- `Option` 的 `flatMap` 定義：`final def flatMap[B](f: (A) ⇒ Option[B]): Option[B]`

仔細觀察 `Option` 的 `map` 與 `flatMap` 會發現，雖然兩者都用 `f` 函數轉換 `Option` 的內容，但是 `flatMap` 的 `f` 回傳 `Option[B]`，因為 `flatMap` 等於 `map` 後再 `flatten`，所以結果還是 `Option[B]`。`flatMap` 用法範例如下

```scala
opt1.flatMap(x => Option(Integer.parseInt(x)))  //> res0: Option[Int] = Some(1234)
opt2.flatMap(x => Option(Integer.parseInt(x)))  //> res1: Option[Int] = None
opt3.flatMap(x => Option(Integer.parseInt(x)))  //> java.lang.NumberFormatException: null
                                                //| 	at java.lang.Integer.parseInt(Integer.java:454)
                                                //| 	at ...
```

#### `Option` 版本的 `getCityName`
先來保守一點的做法：
```scala
def getCityName(account: Account): String = {
  val optAccount = Option(account)
  val optAddress = optAccount.flatMap(x => Option(x.address))
  val optCity = optAddress.flatMap(x => Option(x.city))
  val optName = optCity.flatMap(x => Option(x.name))
  optName.getOrElse("Unknown")
}

getCityName(acc1)                               //> res0: String = Taipei
getCityName(acc2)                               //> res1: String = Unknown
```

簡潔一點的做法：
```scala
def getCityName(account: Account): String = Option(account).
  flatMap(x => Option(x.address)).
  flatMap(x => Option(x.city)).
  flatMap(x => Option(x.name)).
  getOrElse("Unknown")                          //> getCityName: (account: myTest.test05.Account)String
```

#### 跟原文不一樣，怎麼不用 `map` 就好
原本我也想用 `map` 交差了事，但事情不是憨人所想那麼簡單
```scala
def getCityName(account: Account): String = {
  val optAccount = Option(account)
  val optAddress = optAccount.map(_.address)
  val optCity = optAddress.map(_.city)
  val optName = optCity.map(_.name)
  optName.getOrElse("Unknown")
}                                               //> getCityName: (account: myTest.test03.Account)String

val acc1 = Account(Address(City("Taipei")))     //> acc1  : myTest.test03.Account = Account(Address(City(Taipei)))
val acc2 = Account(null)                        //> acc2  : myTest.test03.Account = Account(null)

getCityName(acc1)                               //> res0: String = Taipei
getCityName(acc2)                               //> java.lang.NullPointerException
                                                //| 	at myTest.test03$$anonfun$main$1$$anonfun$2.apply(myTest.test03.scala:16)
                                                //| 	at ...
```
- `getCityName` 在處理 `acc2` 的時候，因為 `acc2.address` 是 `null`，想取 `acc2.address.city` 的值發生 `NullPointerException` 

參照原文寫法也有問題
```scala
class Option[+T](value: T) {
  def map[R](transform: T => R): Option[_] = {
    if (value != null) new Option(transform(value)) else new Option(null)
  }
  def orElse[R >: T](default: R) = {
    if (value != null) value else default
  }
}

def getCityName(account: Account): String = {
  val optAccount = new Option(account)
  val optAddress = optAccount.map(_.address)
  val optCity = optAddress.map(_.city)          //won't compile: value city is not a member of Any
  val optName = optCity.map(_.name)             //won't compile: value name is not a member of Any
  optName.orElse("Unknown")                     //won't compile: value getOrElse is not a member of myTest.test03.Option[Any]
}
```
- `optAddress` 是 `optAccount.map(_.address)` 的產物，其值的型別可能是 `Option[Address]` 也可能是 `Option[Null]`，因為 [Variance](http://docs.scala-lang.org/tutorials/tour/variances.html) 的關係，最後型別會是 `Optioin[Any]`，這沒有問題。
- 問題出在接下來的部分，`optAddress` 型別既然是 `Option[Any]` 那就沒辦法解釋 `_.city`，所以編譯失敗。
- 接下來編譯不過，都是一樣的原因。

___
## Example 2: Transactional
不囉唆，直接上 code。

先定義資料庫 (`Database`) 、帳戶 (`Account`)，還有一些例外
```scala
  class Database {
    var isRollback: Boolean = false
    def beginTransaction() = println("beginTransaction")
    def rollback() = { isRollback = true; println("rollback") }
    def commit() = println("commit")
  }
  val database = new Database 
```
```scala
class Account(var value: Int) {
	override def toString = s"Account($value)"
	def withdraw(num: Int) = if (value < num) throw new InsufficientBalanceException else value -= num
	def deposit(num: Int) = if (num > 100) throw new DepositTooFastException else value += num
}
```
```scala
class DepositTooFastException extends Exception
class InsufficientBalanceException extends Exception
```

再定義轉帳函數，基本上跟 Java code 沒什麼不同
```scala
def transfer(account1: Account, account2: Account) {
  database.beginTransaction()

  try {
    account1.withdraw(100)
    try {
      account2.deposit(100)
    } catch {
      case e: DepositTooFastException => database.rollback()
    }
  } catch {
    case e: InsufficientBalanceException => database.rollback()
  }

  if (database.isRollback == false) {
    database.commit()
  }
}
```

程式寫得不怎麼樣，跑跑範例結果如下
```scala
  val account1 = new Account(200)                 //> account1  : myTest.test11.Account = Account(200)
  val account2 = new Account(200)                 //> account2  : myTest.test11.Account = Account(200)

  println(account1, account2)                     //> (Account(200),Account(200))
  transfer(account1, account2)                    //> beginTransaction
                                                  //| commit
  println(account1, account2)                     //> (Account(100),Account(300))
```

### 套用 Example 1: Option 的做法

實作一個 `Transactional` 的容器，可以根據交易內容操作資料庫、改變自身狀態
```scala
class TransactionException extends Exception

object TxState extends Enumeration {
  type TxState = Value
  val BEGIN, ROLLBACK, COMMIT = Value
}

import TxState._
class Transactional(txState: TxState) {
  def map(transform: TxState => TxState): Transactional = {
    if (txState != BEGIN) return this

    try {
      val result = transform(txState)
      new Transactional(result)
    } catch {
      case e: TransactionException =>
        database.rollback()
        new Transactional(ROLLBACK)
    }
  }

  def commit() = {
    map { state =>
      database.commit()
      COMMIT
    }
  }
}

object Transcational {
  def begin() = {
    database.beginTransaction()
    new Transactional(BEGIN)
  }
}
```

使用 `Transaction` 隱藏 `try/catch` 細節並管理交易裝態。執行結果與前一版本雷同，不再重複。
```scala
def transfer(account1: Account, account2: Account) = Transcational.begin().
  map({ txState => account1.withdraw(100); txState }).
  map({ txState => account2.deposit(100); txState }).
  commit()
```

> 這個範例讓我修改得很痛苦，一來 `transfer` 與 `Transactional` 可以任意存取 `database`，二來 `Transactional.map` 居然把內部的狀態 `txState` 傳給外面的匿名函數處理，這都讓我深感不安，但目前 Scala 範例看得還不夠多，沒能力修改成理想的樣子只能依樣畫葫蘆 :(

### flatMap －展開轉換

誤打誤撞，`Option` 的 `flatMap` 在 Example 1 就玩過了，這個部分蛋糕一塊
```scala
case class City(name: String)
case class Address(city: City)
case class Account(address: Address) {
  def city() = Option(address).flatMap(x => Option(x.city))
}

def getCityName(account: Account): String = Option(account).
  flatMap(x => x.city()).
  flatMap(x => Option(x.name)).
  getOrElse("Unknown")                          //> getCityName: (account: myTest.test13.Account)String

val acc1 = Account(Address(City("Taipei")))     //> acc1  : myTest.test13.Account = Account(Address(City(Taipei)))
val acc2 = Account(null)                        //> acc2  : myTest.test13.Account = Account(null)

getCityName(acc1)                               //> res0: String = Taipei
getCityName(acc2)                               //> res1: String = Unknown
```

