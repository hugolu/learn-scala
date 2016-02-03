# Implicit Conversion

參考連結 http://meetfp.com/zh/blog/implicit

簡單範例
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

複雜範例
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
___
# Implicit Parameters

參考連結 http://meetfp.com/zh/blog/implicit-parameters.html
