# Web Services

## Creating a JSON String from a Scala Object

### Lift-JSON solution
LiftJson.scala:
```scala
import scala.collection.mutable._
import net.liftweb.json._
import net.liftweb.json.Serialization.write

case class Person(name: String, address: Address)
case class Address(city: String, state: String)

object LiftJsonTest extends App {
  val p = Person("Alvin Alenander", Address("Talkeetna", "AK"))

  implicit val formats = DefaultFormats
  val jsonString = write(p)
  println(jsonString)
}
```
```shell
$ sbt run
[info] Running LiftJsonTest
{"name":"Alvin Alenander","address":{"city":"Talkeetna","state":"AK"}}
```

### Gson solution
```scala
import com.google.gson.Gson

case class Person(name: String, address: Address)
case class Address(city: String, state: String)

object GsonTest extends App {
  val p = Person("Alvin Alexander", Address("Talkeetan", "AK"))

  val gson = new Gson
  val jsonString = gson.toJson(p)
  println(jsonString)
}
```
```shell
$ sbt run
[info] Running GsonTest
{"name":"Alvin Alexander","address":{"city":"Talkeetan","state":"AK"}}
```

## Creating a JSON String from Classes That Have Collections
### Lift-JSON version 1
```scala
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

case class Person(name: String, address: Address) {
  var friends = List[Person]()
}

case class Address(city: String, state: String)

object LiftJsonListsVersion1 extends App {
  implicit val formats = DefaultFormats

  val merc = Person("Mercedes", Address("Somewhere", "KY"))
  val mel = Person("Mel", Address("Lake Zurich", "IL"))
  val friends = List(merc, mel)
  val p = Person("Alvin Alexander", Address("Talkeetna", "AK"))
  p.friends = friends

  val json =
    ("person" ->
      ("name" -> p.name) ~
      ("address" ->
        ("city" -> p.address.city) ~
        ("state" -> p.address.state)) ~
      ("friends" ->
        friends.map { f =>
          ("name" -> f.name) ~
          ("address" ->
            ("city" -> f.address.city) ~
            ("state" -> f.address.state))
        })
  )

  println(pretty(render(json)))
}
```
```shell
[info] Running LiftJsonListsVersion1
{
  "person":{
    "name":"Alvin Alexander",
    "address":{
      "city":"Talkeetna",
      "state":"AK"
    },
    "friends":[{
      "name":"Mercedes",
      "address":{
        "city":"Somewhere",
        "state":"KY"
      }
    },{
      "name":"Mel",
      "address":{
        "city":"Lake Zurich",
        "state":"IL"
      }
    }]
  }
}
```
- Lift uses a custom DSL to let you generate the JSON, and also have control over how the JSON is generated
  - `Tuple2` generates a JSON field: `("name" -> p.name)` >>> `"name":"Alvin Alexander"`
  - the `~` operator lets you join fields

### Lift-JSON Version 2
```scala
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

case class Person(name: String, address: Address) {
  var friends = List[Person]()
}

case class Address(city: String, state: String)

object LiftJsonListsVersion2 extends App {
  implicit val formats = DefaultFormats

  val merc = Person("Mercedes", Address("Somewhere", "KY"))
  val mel = Person("Mel", Address("Lake Zurich", "IL"))
  val friends = List(merc, mel)
  val p = Person("Alvin Alexander", Address("Talkeetna", "AK"))
  p.friends = friends

  val json =
    ("person" ->
      ("name" -> p.name) ~
      getAddress(p.address) ~
      getFirends(p)
    )

  println(pretty(render(json)))

  def getFirends(p: Person) = {
    ("friends" ->
      p.friends.map { f =>
        ("name" -> f.name) ~
        getAddress(f.address)
      })
  }

  def getAddress(a: Address) = {
    ("address" ->
      ("city" -> a.city) ~
      ("state" -> a.state))
  }
}
```
- to create methods that can be reused

### Gson
```scala
import com.google.gson.Gson
import com.google.gson.GsonBuilder

case class Person(name: String, address: Address) {
  var friends: List[Person] = _
}

case class Address(city: String, state: String)

object GsonWithArray extends App {

  val merc = Person("Mercedes", Address("Somewhere", "KY"))
  val mel = Person("Mel", Address("Lake Zurich", "IL"))
  val friends = List(merc, mel)
  val p = Person("Alvin Alexander", Address("Talkeetna", "AK"))
  p.friends = friends

  val gson = (new GsonBuilder()).setPrettyPrinting.create
  println(gson.toJson(p))
}
```
- Gson works via **reflection**, and it works well for simple classes. However, I’ve found it to be harder to use when your classes have certain collections.
  - If you change the `Array[Person]` to `List[Person]`, Gson removes the list of friends from the output.
  - An `ArrayBuffer` begins with 16 elements, and when Gson generates the JSON for the list of friends, it correctly includes the two friends, but then outputs the word `null` 14 times, along with including the other output shown.

## Creating a Simple Scala Object from a JSON String
## Parsing JSON Data into an Array of Objects
## Creating Web Services with Scalatra
## Replacing XML Servlet Mappings with Scalatra Mounts
## Accessing Scalatra Web Service GET Parameters
## Accessing POST Request Data with Scalatra
## Creating a Simple GET Request Client
## Sending JSON Data to a POST URL
## Getting URL Headers
## Setting URL Headers When Sending a Request
## Creating a GET Request Web Service with the Play Framework
## POSTing JSON Data to a Play Framework Web Service
