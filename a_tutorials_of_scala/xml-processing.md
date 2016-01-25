# XML Processing

It is possible to mix Scala expressions and XML:
```scala
import scala.xml._

val df = java.text.DateFormat.getDateInstance()
val dateString = df.format(new java.util.Date())

def theDate(name: String) = 
<dateMsg addressedTo={ name }>
  Hello, { name }! Today is { dateString }
</dateMsg>;

println(theDate("John Doe").toString())
//<dateMsg addressedTo="John Doe">
//  Hello, John Doe! Today is Jan 25, 2016
//</dateMsg>
```
