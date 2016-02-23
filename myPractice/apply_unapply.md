# Apply & unapply

參考連結: [apply() 與 unapply() 方法](http://openhome.cc/Gossip/Scala/ApplyUnApply.html)

```scala
object Foo {
  def apply(i: Int, b: Boolean, s: String): String =
    i + "," + b + "," + s

  def unapply(str: String): Option[(Int, Boolean, String)] = {
    val parts = str.split(",")
    if (parts.size == 3) Some(parts(0).toInt, parts(1).toBoolean, parts(2)) else None
  }
}

val foo = Foo(123, true, "scala")               //> foo  : String = 123,true,scala

val Foo(integer, boolean, string) = foo         //> integer  : Int = 123
                                                //| boolean  : Boolean = true
                                                //| string  : String = scala

var Some((i, b, s)) = Foo.unapply(foo)          //> i  : Int = 123
                                                //| b  : Boolean = true
                                                //| s  : String = scala
```
- 相對於unapply()方法，apply()方法則稱之為注入方法（Injection method），提取方法與注入方法通常同時存在（但非必要），apply()方法與unapply()方法的作用通常是相反的
- unapply()方法稱之為提取方法（Extraction method），只具備提取方法的物件稱之為提取器（Extractor），提取器讓你對非案例類別的實例，也可以進行模式比對
```scala
object Bar {
  def unapply(str: String): Option[(String, String, String)] = {
    var parts = str.split(",")
    if (parts.length == 3) Some(parts(0), parts(1), parts(2)) else None
  }
}

val bars = List("apple,banana,carot", "hello,world,scala", "foo,bar,buz")
                                                //> bars  : List[String] = List(apple,banana,carot, hello,world,scala, foo,bar,b
                                                //| uz)
bars.foreach(_ match {
  case Bar(s1, s2, s3) => println(s1, s2, s3)
})                                              //> (apple,banana,carot)
                                                //| (hello,world,scala)
                                                //| (foo,bar,buz)
```

## UnapplySeq
若想要提取的元素個數不定，則可以定義unapplySeq()方法
```scala
object Extractor {
  def unapplySeq(str: String): Option[Seq[String]] = {
    var parts = str.split(",")
    Some(parts)
  }

}

val string = "hello,world,scala"                //> string  : String = hello,world,scala
var Extractor(seq @ _*) = string                //> seq  : Seq[String] = WrappedArray(hello, world, scala)
var Extractor(s1, s2, s3) = string              //> s1  : String = hello
                                                //| s2  : String = world
                                                //| s3  : String = scala
```
