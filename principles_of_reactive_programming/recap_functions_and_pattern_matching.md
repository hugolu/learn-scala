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
