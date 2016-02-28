# Strings

## Testing String Equality 測試相等性

```scala
scala> val s1 = "hello"
s1: String = hello

scala> val s2 = "he" + "llo"
s2: String = hello

scala> val s3 = null
s3: Null = null

scala> val s4 = "Hello"
s4: String = Hello

scala> s1 == s2
res0: Boolean = true

scala> s1 == s3
res1: Boolean = false

scala> s1.toUpperCase == s4.toUpperCase
res2: Boolean = true

scala> s1.equalsIgnoreCase(s4)
res3: Boolean = true
```


## Creating Multiline Strings 產生多行字串

```scala
scala> val string = """hello world
     | this is a test
     | scala"""
string: String =
hello world
this is a test
scala

scala> string.replaceAll("\n", " ")
res0: String = hello world this is a test scala
```

## Splitting Strings 分離字串

```scala
scala> "hello world".split(" ")
res0: Array[String] = Array(hello, world)

scala> "apple, banana, carot".split(", ")
res1: Array[String] = Array(apple, banana, carot)
```

## Substituting Variables into Strings 字串內變數置換

```scala
scala> val str = "hello"
str: String = hello

scala> val int = 123
int: Int = 123

scala> val double = 3.14
double: Double = 3.14

scala> println(s"$str $int $double")
hello 123 3.14

scala> println(s"${str.toUpperCase} ${int + 1} ${double * 2}")
HELLO 124 6.28

scala> println(f"$str%6s 0x$int%x $double%.3f")
 hello 0x7b 3.140
```

```scala
scala> println(s"$str\t$str")
hello	hello

scala> println(raw"$str\t$str")
hello\thello
```

## Processing a String One Character at a Time 逐次處理字串內字元

```scala
scala> for (c <- "hello world") print(c)
hello world

scala> "hello world".foreach(print)
hello world
```

```scala
scala> for (c <- "hello world") yield (c.toUpper)
res0: String = HELLO WORLD

scala> "hello world".map(_.toUpper)
res`: String = HELLO WORLD
```

```scala
scala> for (c <- "hello world" if c != 'l') yield (c)
res2: String = heo word

scala> "hello world".filter(_ != 'l')
res3: String = heo word
```

## Finding Patterns in Strings 找尋子字串

```scala
scala> val string = "123 scala 456"
string: String = 123 scala 456

scala> val pattern = "[0-9]+".r
pattern: scala.util.matching.Regex = [0-9]+

scala> pattern.findFirstIn(string)
res0: Option[String] = Some(123)

scala> pattern.findFirstIn(string).toArray
res1: Array[String] = Array(123)

scala> pattern.findAllIn(string)
res2: scala.util.matching.Regex.MatchIterator = non-empty iterator

scala> pattern.findAllIn(string).toArray
res3: Array[String] = Array(123, 456)

scala> pattern.findFirstIn("hello world")
res4: Option[String] = None

scala> pattern.findFirstIn("hello world").toArray
res5: Array[String] = Array()
```

## Replacing Patterns in Strings 取代子字串

```scala
scala> val string = "123 scala 456"
string: String = 123 scala 456

scala> val pattern = "[0-9]+".r
pattern: scala.util.matching.Regex = [0-9]+

scala> pattern.replaceFirstIn(string, "xxx")
res0: String = xxx scala 456

scala> pattern.replaceAllIn(string, "xxx")
res1: String = xxx scala xxx

scala> string.replaceFirst("[0-9]+", "???")
res2: String = ??? scala 446

scala> string.replaceAll("[0-9]+", "???")
res3: String = ??? scala ???
```

## Extracting Parts of a String That Match Patterns 取出子字串

## Accessing a Character in a String 存取字串內字元

## Add Your Own Methods to the String Class 新增字串方法
