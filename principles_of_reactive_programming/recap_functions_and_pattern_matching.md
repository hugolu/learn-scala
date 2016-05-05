# Recap: Functions and Pattern Matching

### Case Classes

Scala 偏好用 case classes 來定義複合的資料

例如 JSON 資料
```json
{ "firstName" : "John",
  "lastName" : "Smith",
  "address": {
    "streetAddress": "21 2nd Street",
    "state": "NY",
    "postalCode": 10021
  },
  "phoneNumbers": [
    { "type": "home", "number": "212 555-1234" },
    { "type": "fax", "number": "646 555-4567" }
  ]
}
```

使用以下資料結構表示
```scala
abstract class JSON
case class JSeq (elems: List[JSON])           extends JSON
case class JObj (bindings: Map[String, JSON]) extends JSON
case class JNum (num: Double)                 extends JSON
case class JStr (str: String)                 extends JSON
case class JBool(b: Boolean)                  extends JSON
case object JNull                             extends JSON
```
- `JSeq` is a sequence of JSON objects.
- `JObj` contains the bindings which are a map from strings. (field name to JSON object)
- `JNum` captures a number, which is a double in Scala.
- `JStr` captures a string.
- `JBool` captures a Boolean.
- `JNull`represents the null value.

```scala
val data = JObj(Map(
  "firstName" -> JStr("John"),
  "lastName" -> JStr("Smith"),
  "address" -> JObj(Map(
    "streetAddress" -> JStr("21 2nd Street"),
    "state" -> JStr("NY"),
    "postalCode" -> JNum(10021)
  )),
  "phoneNumbers" -> JSeq(List(
    JObj(Map(
      "type" -> JStr("home"), "number" -> JStr("212 555-1234")
    )),
    JObj(Map(
      "type" -> JStr("fax"), "number" -> JStr("646 555-4567")
    )) )) ))
```

`show` 回傳表示 `JSON` 物件的字串
```scala
def show(json: JSON): String = json match {
  case JSeq(elems)    => "[" + (elems map show mkString ", ") + "]"
  case JObj(bindings) =>
    val assocs = bindings map {
      case (key, value) => "\"" + key + "\": " + show(value)
    }
    "{" + (assocs mkString ", ") + "}"
  case JNum(num)      => num.toString
  case JStr(str)      => '\"' + str + '\"'
  case JBool(b)       => b.toString
  case JNull          => "null"
}

show(data)                                      //> res0: String = {"firstName": "John", "lastName": "Smith", "address": {"stre
                                                //| etAddress": "21 2nd Street", "state": "NY", "postalCode": 10021.0}, "phoneN
                                                //| umbers": [{"type": "home", "number": "212 555-1234"}, {"type": "fax", "numb
                                                //| er": "646 555-4567"}]}
```

### Case Blocks

`{ case (key, value) => key + ": " + value }` 的型別是什麼？

如果單獨嘗試這段程式，會得到錯誤訊息：“missing parameter type for expanded function The argument types of an anonymous function must be fully known. (SLS 8.5) Expected type was: ?”

把相關的程式碼拉出來看
```scala
abstract class JSON
case class JObj (bindings: Map[String, JSON]) extends JSON

case JObj(bindings) =>
  val assocs = bindings map {
    case (key, value) => "\"" + key + "\": " + show(value)
  }
  "{" + (assocs mkString ", ") + "}"
```

`map` 預期型別是 `JBinding => String`，匿名函數的型別是輸入 `(key: String, value: JSON)`、輸出 `String`。在此 `JBinding` 定義為 `type JBinding = (String, JSON)`。

### Functions are Objects

在 Scala 中，函數是一級函數，也是物件。所以 `JBbinding => String` 的型別事實上是 `Function1[JBinding, String]`。

`Function1` 是 trait
```scala
trait Function1[-A, +R] {
  def apply(x: A): R
}
```

`JBinding` 跟 `String` 是 type parameters，所以`{ case (key, value) => "\"" + key + "\": " + show(value) }` 被展開成
```scala
new Function1[JBinding, String] {
  def apply(x: JBinding) = x match {
    case (key, value) => key + ": " + show(value)
  }
}
```

### Subclassing Functions

剛剛提過函數是一級函數，所以函數型別也可以繼承。

例如，Map 是從 key 得到 value 的函數
```scala
val nums = Map((1, "one"), (2, "two"), (3, "three"))

nums(1)                                         //> res0: String = one
```

看起來 Map 的定義就像 (我認為 scala 不是真的這麼做的)
```scala
trait Map[Key, Value] extends (Key => Value)
```

例如，seq 是從 index 得到 value 的函數
```scala
val nums = Seq("one", "two", "three")

nums(0)                                         //> res0: String = one
```

看起來 Seq 的定義就像
```scala
trait Seq[Elem] extends (Int => Elem)
```

### Partial Matches

`{ case "ping" => "pong" }` 這個 pattern matching 區塊，加上型別定義被解釋成
```scala
val f: String => String = { case "ping" => "pong" }
```

事實上，完整展開會是長這樣
```scala
val f: String => String = new Function1[String, String] {
	def apply(x: String): String = x match {
		case "ping" => "pong"
	}
}
```

所以 `f` 也可以定義成
```scala
val f: Function1[String, String] = { case "ping" => "pong" }
```

使用情況如下
```scala
f("ping")                                       //> res0: String = pong

f("abc")                                        //> scala.MatchError: abc (of class java.lang.String)
                                                //| 	at myTest.test17$$anonfun$main$1$$anonfun$1.apply(myTest.test17.scala:8)
                                                //| 
                                                //| 	at ...
```
- 因為 pattern matching 沒有處理 `"abc"`，所以發生 `MatchError`

### Partial Function

使用 Partial Function 判斷給定的參數是否定義在函數中，修改剛剛 `f` 的定義
```
val f: PartialFunction[String, String] = { case "ping" => "pong" }
```

`PartialFunction` 定義如下
```scala
trait PartialFunction[-A, +R] extends Function1[-A, +R] {
  def apply(x: A): R
  def isDefinedAt(x: A): Boolean
}
```

呼叫 `isDefinedAt` 確定傳入參數有沒有定義
```scala
f.isDefinedAt("ping")                           //> res0: Boolean = true
f.isDefinedAt("abc")                            //> res1: Boolean = false
```

### Partial Function Objects

`val f: PartialFunction[String, String] = { case "ping" => "pong" }` 是 `PartialFunction` 的物件，Scala compiler 幫忙展開成
```scala
val f: PartialFunction[String, String] = new PartialFunction[String, String] {
  def apply(x: String) = x match {
    case "ping" => "pong"
  }

  def isDefinedAt(x: String) = x match {
    case "ping" => true
    case _      => false
  }
}
```

### Exercise(1)

給定以下函數
```scala
val f: PartialFunction[List[Int], String] = {
  case Nil            => "one"
  case x :: y :: rest => "two"
}
```

`f.isDefinedAt(List(1,2,3))` 結果為何？
1. true
2. false

答案是 `true`

修改程式驗證一下
```scala
val f: PartialFunction[List[Int], String] = {
  case Nil            => "Nil"
  case x :: y :: rest => s"$x :: $y :: $rest"
}                                               //> f  : PartialFunction[List[Int],String] = <function1>

f.isDefinedAt(List(1, 2, 3))                    //> res0: Boolean = true
f(List(1,2,3))                                  //> res1: String = 1 :: 2 :: List(3)
```

### Exercise(2)

給定以下函數
```scala
val g: PartialFunction[List[Int], String] = {
  case Nil => "one"
  case x :: rest =>
    rest match {
      case Nil => "two"
    }
}
```

`g.isDefinedAt(List(1,2,3))` 結果為何？
1. true
2. false

答案還是是 `true`

但是呼叫 `apply` 的時候，會發生 `MatchError`，問題出在第二層 match 沒有定義處理 `List(2, 3)`
```scala
g.isDefinedAt(List(1, 2, 3))                    //> res0: Boolean = true
g(List(1, 2, 3))                                //> scala.MatchError: List(2, 3) (of class scala.collection.immutable.$colon$co
                                                //| lon)
                                                //| 	at myTest.test17$$anonfun$main$1$$anonfun$1.applyOrElse(myTest.test17.sc
                                                //| ala:43)
                                                //| 	at ...
```

___
回過頭來看一開始解析 JSON 資料結構的函數 `show`，有關 `binding map` 這段
```scala
case JObj(bindings) =>
  val assocs = bindings map {
    case (key, value) => "\"" + key + "\": " + show(value)
  }
  "{" + (assocs mkString ", ") + "}"
```

用更單純的範例來示範取值：你可以用 pattern matching 的方式，也可以把 map 元素當成 tuple 自行解釋
```scala
val nums = Map((1, "one"), (2, "two"), (3, "three"))

nums.map { case (k, v) => k + ": " + v }        //> res0: scala.collection.immutable.Iterable[String] = List(1: one, 2: two, 3: three)
nums.map { kv => kv._1 + ": " + kv._2 }         //> res1: scala.collection.immutable.Iterable[String] = List(1: one, 2: two, 3: three)
```
