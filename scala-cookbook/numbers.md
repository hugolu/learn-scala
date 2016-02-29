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

```scala
scala> 3.14
res0: Double = 3.14

scala> 3.14.toInt
res1: Int = 3

scala> 3.14.toFloat
res2: Float = 3.14
```

## Overriding the Default Numeric Type 覆載數字預設型別

```scala
scala> 1
res0: Int = 1

scala> 1d
res1: Double = 1.0
```

```scala
scala> 1: Int
res3: Int = 1

scala> 1: Double
res4: Double = 1.0
```

```scala
scala> var a: Int = _
a: Int = 0
```
- default value of `a` is `0`

## Replacements for ++ and −− 取代++, --

```scala
scala> var a = 0
a: Int = 0

scala> a += 1

scala> println(a)
1
```

## Comparing Floating-Point Numbers 比較浮點數

```scala
scala> val a = 0.1 + 0.2
a: Double = 0.30000000000000004

scala> val b = 0.3
b: Double = 0.3

scala> a == b
res0: Boolean = false

scala> def ~=(a: Double, b: Double, precision: Double) = (a - b).abs < precision
$tilde$eq: (a: Double, b: Double, precision: Double)Boolean

scala> ~=(a, b, 0.0001)
res1: Boolean = true
```

## Handling Very Large Numbers 處理大數值

```scala
scala> var a = BigInt(1234567890)
a: scala.math.BigInt = 1234567890

scala> val b = BigDecimal(123456.789)
b: scala.math.BigDecimal = 123456.789

scala> a*a
res0: scala.math.BigInt = 1524157875019052100

scala> b*b
res1: scala.math.BigDecimal = 15241578750.190521
```

## Generating Random Numbers 產生亂數

```scala
scala> val random = scala.util.Random
random: util.Random.type = scala.util.Random$@4012a6de

scala> random.next[Tab]
nextBoolean   nextDouble   nextGaussian   nextLong            nextString
nextBytes     nextFloat    nextInt        nextPrintableChar

scala> random.nextInt
res0: Int = -2129101691

scala> random.nextInt(100)
res1: Int = 46

scala> random.nextFloat
res2: Float = 0.35779196
```
- `random.nextInt(100)` 隨機取得 `0~99` 之間的整數
- `random.nextFloat` 隨機取得 `0.0~1.0` 之間的浮點數

產生三個 `0~99` 之前的整數
```scala
scala> val random = scala.util.Random
random: util.Random.type = scala.util.Random$@4012a6de
scala> (1 to 3).map(_ => random.nextInt(100))
res13: scala.collection.immutable.IndexedSeq[Int] = Vector(75, 33, 66)
```

## Creating a Range, List, or Array of Numbers 產生數字的 `Range`, `List`, `Array`

```scala
scala> (1 to 10)
res14: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> (1 to 10).toList
res15: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> (1 to 10).toArray
res16: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

## Formatting Numbers and Currency 格式化數字與金額

```scala
scala> val pi = scala.math.Pi
pi: Double = 3.141592653589793

scala> f"$pi%.3f"
res0: String = 3.142

scala> "%.3f".format(pi)
res1: String = 3.142
```

```scala
scala> val formatter = java.text.NumberFormat.getIntegerInstance
formatter: java.text.NumberFormat = java.text.DecimalFormat@674dc

scala> formatter.format(1000000)
res2: String = 1,000,000
```
- `java.text.NumberFormat.getIntegerInstance` 整數格式化

```scala
scala> val formatter = java.text.NumberFormat.getInstance
formatter: java.text.NumberFormat = java.text.DecimalFormat@674dc

scala> formatter.format(scala.math.Pi)
res3: String = 3.142
```
- `java.text.NumberFormat.getIntegerInstance` 一般格式化 (可處理浮點)

```scala
scala> val formatter = java.text.NumberFormat.getCurrencyInstance
formatter: java.text.NumberFormat = java.text.DecimalFormat@7a3fa

scala> formatter.format(123.456)
res4: String = NT$123.46
```
- `java.text.NumberFormat.getCurrencyInstance` 金額格式化

變更區域
```scala
scala> import java.util.{Currency, Locale}
import java.util.{Currency, Locale}

scala> val us = Currency.getInstance(new Locale("us", "US"))
us: java.util.Currency = USD

scala> formatter.setCurrency(us)

scala> formatter.format(123.456)
res5: String = USD123.46
```
