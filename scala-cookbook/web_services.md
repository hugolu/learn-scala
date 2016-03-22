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
