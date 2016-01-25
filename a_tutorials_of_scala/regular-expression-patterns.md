# Regular Expression Patterns

Scala allows patterns having a wildcard-star ```_*``` in the rightmost position to stand for arbitrary long sequences.

The following example demostrate a pattern match which matches a prefix of a sequence and binds the rest to the variable ```rest```.
```scala
def containsScala(x: String): Boolean = {
  val z: Seq[Char] = x
  z match {
    case Seq('s','c','a','l','a', rest @ _*) =>
      println("rest is "+rest)
      true
    case Seq(rest @ _*) =>
      println("rest is "+rest)
      false
  }
}
containsScala("scala's regExp")
//rest is 's regExp
//res0: Boolean = true

containsScala("hello world")
//rest is hello world
//res1: Boolean = false
```
