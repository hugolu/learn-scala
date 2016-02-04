# Implicit Conversion

參考連結 http://meetfp.com/zh/blog/implicit

## 簡單範例
```scala
scala> def show(str: String) = println("Hello world, " + str)
show: (str: String)Unit

scala> show(1)
<console>:24: error: type mismatch;
 found   : Int(1)
 required: String
       show(1)
            ^

scala> implicit def IntToString(i: Int): String = i.toString
warning: there was one feature warning; re-run with -feature for details
IntToString: (i: Int)String

scala> show(123)
Hello world, 123
```

## 複雜範例 (隐式转换代码)
```scala
scala> abstract class TimeDuration {
     |   def toMillis: Int
     |   override def toString = "we got %d millis".format(toMillis)
     | }
defined class TimeDuration

scala> case class Seconds(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000
     | }
defined class Seconds

scala> case class Minutes(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000 * 60
     | }
defined class Minutes

scala> case class Hours(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000 * 60 * 60
     | }
defined class Hours

scala> class Duration(length: Int) {
     |   def seconds = Seconds(length)
     |   def minutes = Minutes(length)
     |   def hours = Hours(length)
     | }
defined class Duration

scala> implicit def IntToDuration(i: Int) = new Duration(i)
warning: there was one feature warning; re-run with -feature for details
IntToDuration: (i: Int)Duration

scala> println(2 seconds)
warning: there was one feature warning; re-run with -feature for details
we got 2000 millis

scala> println(3 minutes)
warning: there was one feature warning; re-run with -feature for details
we got 180000 millis

scala> println(4 hours)
warning: there was one feature warning; re-run with -feature for details
we got 14400000 millis
```

以上过程，演示了怎样将显示转换改写为隐式转换，反过来，当你看到你所不能理解的代码时，比如上面的```4 hours```，你只需要在代码范围内寻找是否有针对```4```也就是```Int```类型的隐式转换， 比如我们找到有这个```implicit def IntToDuration(i: Int) = new Duration(i)```，那么，将```4```还原为显示的```new Duration(4)```就可帮助你理解代码了。

## 隐式扩展类型
```scala
scala> abstract class TimeDuration {
     |   def toMillis: Int
     |   override def toString = "we got %d millis".format(toMillis)
     | }
defined class TimeDuration

scala> case class Seconds(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000
     | }
defined class Seconds

scala> case class Minutes(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000 * 60
     | }
defined class Minutes

scala> case class Hours(length: Int) extends TimeDuration {
     |   def toMillis = length * 1000 * 60 * 60
     | }
defined class Hours

scala> implicit class IntExtendToDuration(i: Int) {
     |   def seconds = Seconds(i)
     |   def minutes = Minutes(i)
     |   def hours = Hours(i)
     | }
defined class IntExtendToDuration

scala> println(2 seconds)
warning: there was one feature warning; re-run with -feature for details
we got 2000 millis
```

**隐式扩展类型**，看上去比**隐式转换**更自然，相当于在原有库的基础上添加了一系列自定义库函数。这样，在该隐式类型的范围内，```Int```类型都拥有以上三个方法。同样的，如果它不在你的程序范围，引入进来即可。 比如，隐式扩展```类IntExtendToDuration```定义在```implicits```包```的Implicits```对象中，你只需```import implicits.Implicits.IntExtendToDuration```即可。

擴充```Int```，類似 ruby times 的用法
```scala
scala> implicit class IntExtendToSomething(i: Int) { def times(f: ()=>Unit) = {(1 to i).foreach(_ => f())} }
defined class IntExtendToSomething

scala> 2.times(() => println("hello world"))
hello world
hello world
```


___
# Implicit Parameters

參考連結 http://meetfp.com/zh/blog/implicit-parameters.html

就实际运行机制而言，隐式参数与缺省参数是完全不一样的。缺省参数是函数定义方设定了一个缺省值，在调用者没有指明时将使用该缺省值。 隐式参数则不同，最终是会由调用方指定参数值，只是不一定在调用的语句里指定而已。编译器在发现缺少隐式参数时，会在程序范围内寻找符合类型的隐式值，如果找不到则编译会失败。

```scala
scala> abstract class Logger {
     |   def log(s: String)
     | }
defined class Logger

scala> class FileLogger extends Logger {
     |   def log(s: String) = {println("File: " + s)}
     | }
defined class FileLogger

scala> class StdoutLogger extends Logger {
     |   def log(s: String) = {println("Stdout: " + s)}
     | }
defined class StdoutLogger

scala> def Add(a: Int, b: Int)(implicit logger: Logger) {
     |   logger.log("%d + %d = %d".format(a, b, a+b))
     | }
Add: (a: Int, b: Int)(implicit logger: Logger)Unit

scala> implicit val log = new FileLogger
logger: FileLogger = FileLogger@45d3972c

scala> Add(1,2)
File: 1 + 2 = 3

scala> Add(2,3)(new StdoutLogger) //you may do it explicitly
Stdout: 2 + 3 = 5
```

如果上述代码没有```implicit val log = new FileLogger```这一句，在代码范围内也没有其他的Logger类型的implicit值，编译器会报错。

也与隐式转换一样，隐式值的名称对编译器并无影响，只要类型符合即可，但是为了可读性，建议赋予它合适的名称。也就是說 ```log```可以任意命名。

以下示範default parameter與implicit parameter的差異
```scala
scala> def sayhi(greeting: String = "Hello, World!") = {println(greeting)}
sayhi: (greeting: String)Unit

scala> sayhi()
Hello, World!

scala> sayhi
<console>:12: error: missing arguments for method sayhi;
follow this method with `_' if you want to treat it as a partially applied function
       sayhi
       ^
```
```scala
scala> def sayhi(implicit greeting: String) = {println(greeting)}
sayhi: (implicit greeting: String)Unit

scala> implicit val greeting = "Hello, World!"
greeting: String = Hello, World!

scala> sayhi
Hello, World!

scala> sayhi()
<console>:13: error: not enough arguments for method sayhi: (implicit greeting: String)Unit.
Unspecified value parameter greeting.
       sayhi()
            ^
```
