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
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                //| orksheetSupport.scala:65)
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                //| ksheetSupport.scala:75)
                                                //| 	at myTest.test01$.main(myTest.test01.scala:3)
                                                //| 	at myTest.test01.main(myTest.test01.scala)
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
