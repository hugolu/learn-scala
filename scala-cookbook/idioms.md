# Idioms

## Create Methods with No Side Effects (Pure Functions)

### Referential transparency
An expression is referentially transparent (RT) if it can be replaced by its resulting value without changing the behavior of the program.

### Pure functions
1. The function always evaluates to the same result value given the same argument value(s). It cannot depend on any hidden state or value, and it cannot depend on any I/O.
2. Evaluation of the result does not cause any semantically observable side effect or output, such as mutation of mutable objects or output to I/O devices.

### 80/20 rule
The best advice I can share about FP is to follow the 80/20 rule: write 80% of your program using pure functions (the “cake”), then create a 20% layer of other code on top of the functional base (the “icing”) to handle the user interface, printing, database interactions, and other methods that have “side effects”.

### The Java approach (a poorly written class)
```scala
class Stock (var symbol: String, var company: String, var price: BigDecimal, var volume: Long) {
  var html: String = _
  def buildUrl(stockSymbol: String): String = { ... }
  def getUrlContent(url: String):String = { ... }

  def setPriceFromHtml(html: String) { this.price = ... }
  def setVolumeFromHtml(html: String) { this.volume = ... }
  def setHighFromHtml(html: String) { this.high = ... }
  def setLowFromHtml(html: String) { this.low = ... }

  // some dao-like functionality
  private val _history: ArrayBuffer[Stock] = { ... }
  val getHistory = _history
}
```
- All of its fields are mutable.
- All of the set methods mutate the class fields.
- The getHistory method returns a mutable data structure.

### Fixing the problems
```scala
case class Stock(symbol: String, company: String)
case class StockInstance(symbol: String, datetime: String, price: BigDecimal, volume: Long)

object NetworkUtils {
  def getUrlContent(url: String): String = { ... }
}

object StockUtils {
  def buildUrl(stockSymbol: String): String = { ... }
  def getPrice(symbol: String, html: String): String = { ... }
  def getVolume(symbol: String, html: String): String = { ... }
  def getHigh(symbol: String, html: String): String = { ... }
  def getLow(symbol: String, html: String): String = { ... }
}

object DateUtils {
  def currentDate: String = { ... }
  def currentTime: String = { ... }
}
```
- separate two concepts that are buried in the class: `Stock`, `StockInstance`
- use general-purpose object: `NetworkUtils`, `StockUtils`, `DateUtils`
- use `get*` methods instead of `set*`: `getPrice`, `getVolume`, `getHigh`, `getLow`

```scala
val stock = new Stock("AAPL", "Apple")
val url = StockUtils.buildUrl(stock.symbol)
val html = NetUtils.getUrlContent(url)

val price = StockUtils.getPrice(html)
val volume = StockUtils.getVolume(html)
val high = StockUtils.getHigh(html)
val low = StockUtils.getLow(html)
val date = DateUtils.currentDate
val stockInstance = StockInstance(symbol, date, price, volume, high, low)
```
- retrieve the HTML that describes the stock from a web page
- extract the desired stock information, get the date, and create the Stock instance

### `set*` methods is harder to test
1. Set the `html` field in the object.
2. Call the current `set` method, such as `setPriceFromHtml`.
3. Internally, this method reads the private `html` class field.
4. When the method runs, it mutates a field in the class (`price`).
5. You have to “get” that field to verify that it was changed.
6. In more complicated classes, it’s possible that the `html` and `price` fields may be mutated by other methods in the class.

### `get*` method is easier to test:
1. Call the function, passing in a known value.
2. Get a result back from the function.
3. Verify that the result is what you expected.

## Prefer Immutable Objects
Prefer immutability:
- Prefer immutable collections. For instance, use immutable sequences like `List` and `Vector` before reaching for the mutable `ArrayBuffer`.
- Prefer immutable variables. That is, prefer `val` to `var`.

There are at least two major benefits to using immutable variables (val) and immutable collections:
- They represent a form of defensive coding, keeping your data from being changed accidentally.
- They’re easier to reason about.

### Using val + mutable, and var + immutable?
```scala
class Pizza {
  private val _toppings = new collection.mutable.ArrayBuffer[Topping]()

  def toppings = _toppings.toList
  def addTopping(t: Topping) { _toppings += t }
  def removeTopping(t: Topping) { _toppings -= t }
}
```
- I made `_toppings` an `ArrayBuffer` because I knew that elements(toppings)would often be added and removed.
- I made`_toppings` a `val` because there was no need for it to ever be reassigned.
- I made it `private` so its accessor wouldn’t be visible outside of my class.
- I created the methods `toppings`, `addTopping`, and `removeTopping` to let other code manipulate the collection.
- When other code calls the `toppings` method, I can give them an immutable copy of the toppings.

#### Don’t use the “val + mutable collection” approach
```scala
val toppings = new collection.mutable.ArrayBuffer[Topping]()
```
- I didn’t want to expose toppings as an immutable collection outside of my `Pizza` class, which would have happened here, because the `val` would have generated an accessor method.
- “Who should be responsible for managing the toppings on the pizza?” and Pizza clearly has the responsibility of maintaining its toppings.

#### Don't choose “var + immutable collection” design
```scala
var toppings = Vector[Topping]()

def addTopping(t: Topping) = toppings :+ t

// bad: other code can mutate 'toppings'
pizza.toppings = Vector(Cheese)
```
- it’s a little cumbersome to remove an element from a Vector (you have to filter the undesired toppings out of the originating Vector while assigning the result to a new Vector)
- it lets toppings be reassigned outside of the Pizza class, which I don’t want

## Think “Expression-Oriented Programming”

To understand EOP, you have to understand the difference between a statement and an expression. 
- “Statements do not return results and are executed solely for their side effects, while expressions always return a result and often do not have side effects at all.”

EOP:
- “An expression-oriented programming language is a programming language where every (or nearly every) construction is an expression, and thus yields a value.”

```scala
// a series of expressions
val url = StockUtils.buildUrl(symbol) val html = NetUtils.getUrlContent(url) val price = StockUtils.getPrice(html) val volume = StockUtils.getVolume(html) val high = StockUtils.getHigh(html)
val low = StockUtils.getLow(html)
val date = DateUtils.getDate
val stockInstance = StockInstance(symbol, date, price, volume, high, low)
```
- The functions don’t mutate the data they’re given, and they don’t have side effects, so they’re easy to read, easy to reason about, and easy to test.

## Use Match Expressions and Pattern Matching

### Replacement for the Java switch statement
```scala
val month = i match {
  case 1  => "January"
  case 2  => "February"
  ...
  case 11 => "November"
  case 12 => "December"
  case _  => "Invalid month"
}
```
### Replacement for the Java unwieldy if/then statements
```scala
i match {
  case 1 | 3 | 5 | 7 | 9  => println("odd")
  case 2 | 4 | 6 | 8 | 10 => println("even"
}
```
### In try/catch expressions
```scala
def readTextFile(filename: String): Option[List[String]] = {
  try {
    Some(Source.fromFile(filename).getLines.toList)
  } catch {
    case e: Exception => None
  }
}
```
### As the body of a function or method
```scala
def is isTrue(a: Any) = a match {
  case 0 | "" => false
  case _      => true
}
```
### Use with Option/Some/None
```scala
def toInt(s: String): Option[Int] = {
  try {
    Some(s.toInt)
  } catch {
    case e: Exception => None
  }
}

toInt(aString) match {
  case Some(i)  => println(i)
  case None     => println("Error")
}
```
### In actors
```scala
case MyActor extends Actor {
  def receive = {
    case Cmd1 => handleCmd1
    case Cmd2 => handleCmd2
    case Cmd3 => handleCmd3
  }
}
```

## Eliminate null Values from Your Code

### Initialize var fields with Option, not null
```scala
case class Address(city: String)
class User {
  var name: String = _
  var address: Address = _
}

val user = new User
println(user.name)                              //> null
println(user.address.city)	                    // java.lang.NullPointerException
```
```scala
case class Address(city: String)
class User {
  var name: Option[String] = None
  var address: Option[Address] = None
}

val user = new User                             //> user  : myTest.test09.User = myTest.test09$$anonfun$main$1$User$1@2e746d6d
println(user.name.getOrElse("<not assigned>"))  //> <not assigned>
user.address.foreach { a => println(a.city) }
```
- In the case of the `address`, if it’s not assigned, the `foreach` loop won’t be executed, so the print statements are never reached. 

### Don’t return null from methods
```scala
def doSomething: Option[String] = { ... }
def toInt(s: String): Option[Int] = { ... }
def lookupPerson(name: String): Option[Person] = { ... }
```
### Converting a null into an Option, or something else
```scala
def getName: Option[String] = {
  var name = javaPerson.getName
  if (name == null) None else Some(name)
}
```

### Benefits
- You’ll eliminate NullPointerExceptions.
- Your code will be safer.
- You won’t have to write if statements to check for null values.
- Adding an `Option[T]`return type declaration to  amethod is a terrific way to indicate that something is happening in the method such that the caller may receive a `None` instead of a `Some[T]`. This is a much better approach than returning null from a method that is expected to return an object.
- You’ll become more comfortable using `Option`, and as aresult, you’ll be able to take advantage of how it’s used in the collection libraries and other frameworks.

## Using the Option/Some/None Pattern

### Returning an Option from a method
```scala
def toInt(s: String): Option[Int] = {
  try {
    Some(s.toInt)
  } catch {
    case e: Exception => None
  }
}
```
```scala
scala> val x = toInt("1")
x: Option[Int] = Some(1)

scala> val y = toInt("?")
y: Option[Int] = None
```
### Getting the value from an Option with `getOrElse`, `foreach`, or `match`
```scala
scala> val x = toInt("1").getOrElse(0)
x: Int = 1

scala> toInt("1").foreach{ i => println(s"Got $i") }
Got 1

scala> toInt("1") match {
     |   case Some(i) => println(s"Got $i")
     |   case None    => println("Got nothing")
     | }
Got 1
```

### Using Option with collections
```scala
scala> val list = List("1", "2", "foo", "3", "bar")
list: List[String] = List(1, 2, foo, 3, bar)

scala> list.map(toInt)
res4: List[Option[Int]] = List(Some(1), Some(2), None, Some(3), None)

scala> list.map(toInt).flatten
res5: List[Int] = List(1, 2, 3)

scala> list.flatMap(toInt)
res6: List[Int] = List(1, 2, 3)

scala> list.map(toInt).collect{case Some(i) => i}
res7: List[Int] = List(1, 2, 3)
```
- `toInt` is defined to return `Option[Int]`.
- Methods like `flatten`, `flatMap`, and others are built to work well with `Option` values.
- You can pass anonymous functions into the `collection` methods.

### Using Option with frameworks
### Using Try/Success/Failure when you need the error message
