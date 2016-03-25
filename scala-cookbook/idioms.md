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
## Think “Expression-Oriented Programming”
## Use Match Expressions and Pattern Matching
## Eliminate null Values from Your Code
## Using the Option/Some/None Pattern
