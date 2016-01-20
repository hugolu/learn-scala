# Option

參考資料
- https://wizardforcel.gitbooks.io/w3school-scala/content/19.html

```scala
scala> val colors = Map("red" -> "#FF0000", "azure" -> "#F0FFFF", "peru" -> "#CD853F")
colors: scala.collection.immutable.Map[String,String] = Map(red -> #FF0000, azure -> #F0FFFF, peru -> #CD853F)

// get a Some[T] or None
scala> colors.get("red")
res0: Option[String] = Some(#FF0000)

scala> colors.get("black")
res1: Option[String] = None

// use match-case to get the value
scala> def getColor(color: Option[String]) = color match {
     | case Some(rgbCode) => rgbCode
     | case None => "?"
     | }
getColor: (color: Option[String])String

scala> getColor(colors.get("red"))
res2: String = #FF0000

scala> getColor(colors.get("black"))
res3: String = ?

// use getOrElse to get the value
scala> colors.get("red").getOrElse("?")
res4: String = #FF0000

scala> colors.get("black").getOrElse("?")
res5: String = ?
```
