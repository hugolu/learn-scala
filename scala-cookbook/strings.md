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

## Splitting Strings 分離字串

## Substituting Variables into Strings 字串內變數置換

## Processing a String One Character at a Time 逐次處理字串內字元

## Finding Patterns in Strings 找尋子字串

## Replacing Patterns in Strings 取代子字串

## Extracting Parts of a String That Match Patterns 取出子字串

## Accessing a Character in a String 存取字串內字元

## Add Your Own Methods to the String Class 新增字串方法
