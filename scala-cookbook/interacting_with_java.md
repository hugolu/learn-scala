# Interacting with Java

## Going to and from Java Collections
```scala
scala> def nums = {
     |   var list = new java.util.ArrayList[Int]()
     |   list.add(1)
     |   list.add(2)
     |   list
     | }
nums: java.util.ArrayList[Int]

scala> val list = nums
list: java.util.ArrayList[Int] = [1, 2]

scala> list.foreach(println)
<console>:13: error: value foreach is not a member of java.util.ArrayList[Int]
       list.foreach(println)
            ^

scala> import scala.collection.JavaConversions._
import scala.collection.JavaConversions._

scala> list.foreach(println)
1
2
```
- Use the methods of Scala’s `JavaConversions` object to make the conversions work.
- This “magic” comes from the power of Scala’s implicit conversions.

## Add Exception Annotations to Scala Methods to Work with Java
## Using @SerialVersionUID and Other Annotations
## Using the Spring Framework
## Annotating varargs Methods
## When Java Code Requires JavaBeans
## Wrapping Traits with Implementations
