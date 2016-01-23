# Advanced types

## View bounds (“type classes”)

Implicit functions allow automatic conversion. 
```scala
implicit def strToInt(x: String) = x.toInt
// strToInt: (x: String)Int

math.max("123", 111)
// res7: Int = 123
```

### ```<%```
```scala
class Container[A <% Int] { def addIt(x: A) = 123 + x }
// defined class Container

(new Container[String]).addIt("123")
// res16: Int = 246

(new Container[Int]).addIt(123)
// res17: Int = 246
```
- This says that ```A``` has to be “viewable” as ```Int```.
 
