# Regular Expression

參考連結: [規則表示式與提取器](http://openhome.cc/Gossip/Scala/RegularExpressionExtractor.html)

一些常用的範圍，可以使用預先定義的字元類別

| 字元 | 作用 | 說明 |
|------|------|--------|
| ```.``` | | 任一字元 |
| ```\d``` | ```[0-9]``` | 數字 |
| ```\D``` | ```[^0-9]``` | 非數字 |
| ```\s``` | ```[ \t\n\x0B\f\r]``` | 空白字元 |
| ```\S``` | ```[^ \t\n\x0B\f\r]``` | 非空白字元 |
| ```\w``` | ```[a-zA-Z_0-9]``` | 數字或是英文字 |
| ```\W``` | ```[^a-zA-Z_0-9]``` | 非數字與英文字 |

Greedy quantifiers

| 表示 | 說明 |
|------|------|
| ```X?``` | X出現一次或完全沒有 |
| ```X*``` | X出現零次或多次 |
| ```X+``` | X出現一次或多次 |
| ```X{n}``` | X出現n次 |
| ```X{n,}``` | X出現至少n次 |
| ```X{n,m}``` | X出現至少n次，但不超過m次 |

___
```scala
import scala.util.matching.Regex

val string = "Birthday: 1975/05/26"       //> string  : String = Birthday: 1975/05/26
val rex = new Regex("""\d\d\d\d""")       //> rex  : scala.util.matching.Regex = \d\d\d\d
val ans = rex.findFirstIn(string)         //> ans  : Option[String] = Some(1975)
ans.getOrElse("not found")                //> res0: String = 1975
```
- 如果你在```"```與```"```間定義規則表示式，那麼對於```\d```的第一個```\```字元，你必須避開（Escape），也就是寫為```\\d```的形式，如果你不想特意作避開字元的動作，則可以在```"""```與```"""```定義規則表示式
- ```new Regex("""\d\d\d\d""")``` 可以替換成 ```"""\d\d\d\d""".r```

Regrex 群組
```scala
val string = "1975/05/26"                       //> string  : String = 1975/05/26
val Brithday = """(\d\d\d\d)/(\d\d)/(\d\d)""".r //> Brithday  : scala.util.matching.Regex = (\d\d\d\d)/(\d\d)/(\d\d)
val Brithday(y, m, d) = string                  //> y  : String = 1975
                                                //| m  : String = 05
                                                //| d  : String = 26
```
- 使用```()```將規則表示式中某些規則群組起來時，你可以運用提取器的語法來提取符合的元素

```scala
val string = "05/26"                            //> string  : String = 05/26
val Brithday = """(\d\d\d\d)?/?(\d\d)/(\d\d)""".r
                                                //> Brithday  : scala.util.matching.Regex = (\d\d\d\d)?/?(\d\d)/(\d\d)
val Brithday(y, m, d) = string                  //> y  : String = null
                                                //| m  : String = 05
                                                //| d  : String = 26
```
- ```()```被設定為可出現零次的情況，若要比對的字串沒有出現，則提取出```null```值


之所以可以使用這樣的提取器語法，是因為 ```scala.util.matching.Regex``` 類別定義了```unapplySeq()```方法，對於符合規則表示式中使用()群組的部份提取出來。
