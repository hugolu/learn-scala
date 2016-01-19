# Function Composition

原文出處 http://twitter.github.io/scala_school/pattern-matching-and-functional-composition.html
```scala
scala> def f(s: String) = "f(" + s + ")"
f: (String)java.lang.String

scala> def g(s: String) = "g(" + s + ")"
g: (String)java.lang.String

// compose makes a new function that composes other functions f(g(x))
scala> val fComposeG = f _ compose g _
fComposeG: (String) => java.lang.String = <function>

scala> fComposeG("yay")
res0: java.lang.String = f(g(yay))

// andThen is like compose, but calls the first function and then the second, g(f(x))
scala> val fAndThenG = f _ andThen g _
fAndThenG: (String) => java.lang.String = <function>

scala> fAndThenG("yay")
res1: java.lang.String = g(f(yay))
```

```scala
scala> val list = List("this", "is", "an", "example")

// str2num is a map function
scala> def str2num(list: List[String]): List[Int] = { list.map(_.length) }
str2num: (list: List[String])List[Int]

// summary is a recude function
scala> def summary(list: List[Int]): Int = { list.reduce(_+_) }
summary: (list: List[Int])Int

// compose
scala> val redeceAfterMap = summary _ compose str2num _
redeceAfterMap: List[String] => Int = <function1>

scala> redeceAfterMap(list)
res0: Int = 15

// andThen
scala> val mapBeforeReduce = str2num _ andThen summary _
mapBeforeReduce: List[String] => Int = <function1>

scala> mapBeforeReduce(list)
res1: Int = 15
```
