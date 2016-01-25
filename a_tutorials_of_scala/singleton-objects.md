# Singleton Objects

Methods and values that aren’t associated with individual instances of a class belong in *singleton objects*, denoted by using the keyword ```object``` instead of ```class```.

```scala
object Blah {
  var string = ""
  def sum(l: List[Int]): Int = l.sum
}

val a = Blah
val b = Blah

a.string = "hello"
println(b.string)
//hello

println(b.sum(List(1,2,3)))
//6
```

## Companions
Most singleton objects do not stand alone, but instead are associated with a class of the same name. When this happens, the singleton object is called the *companion object* of the class, and the class is called the *companion class* of the object.

## Companion Object
- http://daily-scala.blogspot.tw/2009/09/companion-object.html
- http://stackoverflow.com/questions/6919965/companion-object-cannot-access-private-variable-on-the-class

One of the most common uses of a companion object is to define factory methods for class. An example is case-classes. When a case-class is declared a companion object is created for the case-class with a factory method that has the same signature as the primary constructor of the case class. That is why one can create a case-class like: MyCaseClass(param1, param2). No new element is required for case-class instantiation.

```scala
class MyString(val jString:String) {
  private var extraData = ""
  override def toString = jString+extraData
}; object MyString {
  def apply(base:String, extras:String) = {
    val s = new MyString(base)
    s.extraData = extras
    s
  }
  def apply(base:String) = new MyString(base)
}

println(MyString("hello"," world"))
//hello world
println(MyString("hello"))
//hello
```

My practice:
```scala
case class MyString(val str: String*) { override def toString = str.toList.reduce(_+ " " +_) }

println(MyString("hello"))
//hello
println(MyString("hello", "world"))
//hello world
println(MyString("hello", "world", "scala"))
//hello world scala
```
