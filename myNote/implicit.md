# Implicit Conversion

參考連結 http://meetfp.com/zh/blog/implicit

## 簡單範例
```scala
def show(str: String) = println("Hello world, " + str)

scala> show(123)      //error: type mismatch;
```

```scala
implicit def IntToString(i: Int): String = i.toString

show(123)             //> Hello world, 123
```

## 複雜範例 (隐式转换代码)
```scala
abstract class TimeDuration {
    def toMillis: Int
}
case class Seconds(length: Int) extends TimeDuration {
    def toMillis = length * 1000
}
case class Minutes(length: Int) extends TimeDuration {
    def toMillis = length * 1000 * 60
}
case class Hours(length: Int) extends TimeDuration {
    def toMillis = length * 1000 * 60 * 60
}
```

```scala
class Duration(length: Int) {
    def seconds = Seconds(length)
    def minutes = Minutes(length)
    def hours = Hours(length)
}
implicit def intToDuration(i: Int) = new Duration(i)
```

```scala
scala> 2 seconds      //res1: Seconds = Seconds(2)
scala> 3 minutes      //res2: Minutes = Minutes(3)
scala> 4 hours        //res3: Hours = Hours(4)
```

以上过程，演示了怎样将显示转换改写为隐式转换，反过来，当你看到你所不能理解的代码时，比如上面的```4 hours```，你只需要在代码范围内寻找是否有针对```4```也就是```Int```类型的隐式转换， 比如我们找到有这个```implicit def IntToDuration(i: Int) = new Duration(i)```，那么，将```4```还原为显示的```new Duration(4)```就可帮助你理解代码了。

## 隐式扩展类型
```scala
implicit class IntExtendsToDuration(length: Int) {
    def seconds = Seconds(length)
    def minutes = Minutes(length)
    def hours = Hours(length)
}
```

**隐式扩展类型**，看上去比**隐式转换**更自然，相当于在原有库的基础上添加了一系列自定义库函数。这样，在该隐式类型的范围内，```Int```类型都拥有以上三个方法。同样的，如果它不在你的程序范围，引入进来即可。 比如，隐式扩展```类IntExtendToDuration```定义在```implicits```包的```Implicits```对象中，你只需```import implicits.Implicits.IntExtendToDuration```即可。

擴充```Int```，使其擁有類似 ruby times 的用法
```scala
implicit class IntExtendToSomething(i: Int) {
  def times(f: => Unit) = { (1 to i) foreach (_=>f) }
}

scala> 3 times println("hello")
hello
hello
hello
```
___
# Implicit Parameters

參考連結 http://meetfp.com/zh/blog/implicit-parameters.html

就实际运行机制而言，隐式参数与缺省参数是完全不一样的。缺省参数是函数定义方设定了一个缺省值，在调用者没有指明时将使用该缺省值。 隐式参数则不同，最终是会由调用方指定参数值，只是不一定在调用的语句里指定而已。编译器在发现缺少隐式参数时，会在程序范围内寻找符合类型的隐式值，如果找不到则编译会失败。

```scala
abstract class Logger {
    def log(s: String)
}

class FileLogger extends Logger {
    def log(s: String) = { println(s"File: $s") }
}

class StdoutLogger extends Logger {
    def log(s: String) = { println(s"Stdout: $s") }
}

def add(a: Int, b: Int)(implicit logger: Logger) {
    logger.log(s"$a + $b = ${a+b}")
}
```
```scala
scala> implicit val log = new FileLogger
scala> add(1,2)                     //> File: 1 + 2 = 3
scala> add(2,3)(new StdoutLogger)   //> Stdout: 2 + 3 = 5
```

如果上述代码没有```implicit val log = new FileLogger```这一句，在代码范围内也没有其他的Logger类型的implicit值，编译器会报错。

也与隐式转换一样，隐式值的名称对编译器并无影响，只要类型符合即可，但是为了可读性，建议赋予它合适的名称。也就是說 ```log```可以任意命名。

以下示範default parameter與implicit parameter的差異
```scala
scala> def foo(str: String = "Hello, World!") = println(str)

scala> foo()        //> Hello, World!
scala> foo          //error: missing arguments for method foo;
```
```scala
scala> def bar(implicit str: String) = println(str)
scala> implicit val word = "Hello, World!"

scala> bar          //> Hello, World!
scala> bar()        //error: not enough arguments for method bar: (implicit str: String)Unit.
```
