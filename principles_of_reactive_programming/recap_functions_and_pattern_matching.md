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

可以用以下資料結構表示
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

`{ case (key, value) => key + ”: ” + value }` 的型別是什麼？

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

`JBinding` 跟 `String` 是 type parameters，所以`{ case (key, value) => key + ”: ” + value }` 被展開成
```scala
new Function1[JBinding, String] {
  def apply(x: JBinding) = x match {
    case (key, value) => key + ": " + show(value)
  }
}
```

