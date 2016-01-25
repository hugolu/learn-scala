# Pattern Matching

Scala has a built-in general pattern matching mechanism. It allows to match on any sort of data with a first-match policy. 

```scala
def matchTest(x: Int): String = x match {
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}
println(matchTest(3))
//many
```

```scala
def matchTest(x: Any): Any = x match {
  case 1 => "one"
  case "two" => 2
  case y: Int => "scala.Int"
}

println(matchTest(1))
//one
println(matchTest("two"))
//2
println(matchTest(3))
//scala.Int
```
