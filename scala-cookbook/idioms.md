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

## Prefer Immutable Objects
## Think “Expression-Oriented Programming”
## Use Match Expressions and Pattern Matching
## Eliminate null Values from Your Code
## Using the Option/Some/None Pattern
