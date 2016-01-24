# Unified Type

The superclass of all classes ```scala.Any``` has two direct subclasses ```scala.AnyVal``` and ```scala.AnyRef```
- ```scala.AnyVal```
  - Value classes
  - All value classes are predefined; they correspond to the primitive types of Java-like languages.
- ```scala.AnyRef```
  - Reference classes
  - All other classes define reference types. User-defined classes define reference types by default; i.e. they always (indirectly) subclass ```scala.AnyRef```.
  
Every user-defined class in Scala implicitly extends the trait ```scala.ScalaObject```. Classes from the infrastructure on which Scala is running (e.g. the Java runtime environment) do not extend ```scala.ScalaObject```. If Scala is used in the context of a Java runtime environment, then ```scala.AnyRef``` corresponds to ```java.lang.Object```. 
- ```scala.ScalaObject``` is subtype of ```scala.AnyRef```

```scala
val set = new scala.collection.mutable.LinkedHashSet[Any]
set += "This is a string"  // add a string
set += 732                 // add a number
set += 'c'                 // add a character
set += true                // add a boolean value

val iter: Iterator[Any] = set.iterator

while (iter.hasNext) {
  println(iter.next.toString())
}
//This is a string
//732
//c
//true
```
