# Numbers

`Byte`, `Char`, `Double`, `Float`, `Int`, `Long`, and `Short` 都擴充自 `Trait AnyVal`
- `Char` - 16-bit unsigned Unicode character
- `Byte` - 8-bit signed value
- `Short` - 16-bit signed value
- `Int` - 32-bit signed value
- `Long` - 64-bit signed value
- `Float`- 32-bit IEEE 754 single precision float
- `Double` - 64-bit IEEE 754 single precision float

## Parsing a Number from a String 解析數字字串

```scala
scala> "100".toByte
res0: Byte = 100

scala> "100".toShort
res1: Short = 100

scala> "100".toInt
res2: Int = 100

scala> "100".toLong
res3: Long = 100

scala> "100".toFloat
res4: Float = 100.0

scala> "100".toDouble
res5: Double = 100.0
```

`parseInt(string, radix)`
- `string`: 必需。要被解析的字串
- `radix`: 可選。要解析字串的基數
```scala
scala> Integer.parseInt("100", 2)
res6: Int = 4

scala> Integer.parseInt("100", 8)
res7: Int = 64

scala> Integer.parseInt("100", 16)
res8: Int = 256
```

```scala
scala> implicit class StringToInt(s: String) {
     | def toInt(radix: Int) = Integer.parseInt(s, radix)
     | }
defined class StringToInt

scala> "100".toInt(8)
res9: Int = 64
```

解決 `toInt` 遭遇非數字例外 `NumberFormatException`
```scala
"123".toInt                             //> res0: Int = 123
"abc".toInt                             //> java.lang.NumberFormatException: For input string: "abc"
```
```scala
implicit class StringToInt(str: String) {
  def toInteger: Option[Int] = {
    try {
      Some(str.toInt)
    } catch {
      case e: NumberFormatException => None
    }
  }
}

"123".toInteger.getOrElse(0)                    //> res0: Int = 123
"abc".toInteger.getOrElse(0)                    //> res1: Int = 0
```

## Converting Between Numeric Types (Casting) 數字間的轉型

## Overriding the Default Numeric Type 

## Replacements for ++ and −−

## Comparing Floating-Point Numbers

## Handling Very Large Numbers

## Generating Random Numbers

## Creating a Range, List, or Array of Numbers

## Formatting Numbers and Currency
