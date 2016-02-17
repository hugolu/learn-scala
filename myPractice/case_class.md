# Case Class

參考連結 http://openhome.cc/Gossip/Scala/CaseClass.html

```scala
case class Point(x: Int, y: Int)

val p1 = Point(1, 2)                            //> p1  : myTest.test8.Point = Point(1,2)
val p2 = Point(1, 2)                            //> p2  : myTest.test8.Point = Point(1,2)
val p3 = Point(2, 1)                            //> p3  : myTest.test8.Point = Point(2,1)

println(p1)                                     //> Point(1,2)
p1 == p2                                        //> res0: Boolean = true
p1 == p3                                        //> res1: Boolean = false
```
- 案例類別（Case class）是在類別定義時加上case關鍵字
- 對於案例類別，編譯器會對它動些手腳，首先就是編譯器會建立一個以Point為名的工廠方法（Factory method），所以你可以這麼建立Point的實例，而不用使用new來建立
- 案例類別參數列上名稱都是val（而不是private[this] val），所以可以直接存取
- 編譯器會加上toString、hashCode與equals()的實作，所以你可以直接顯示Point的描述，或者是使用==來比較

```scala
case class Point(x: Int, y: Int)
case class Circle(p: Point, r: Int)

def show(c: Circle) = c match {
  case Circle(Point(0, 0), r)   => "原點的圓, 半徑 " + r
  case Circle(Point(10, 10), r) => "終點的圓, 半徑 " + r
  case Circle(_, r)             => "其它的圓, 半徑 " + r
}                                               //> show: (c: myTest.test9.Circle)String

println(show(Circle(Point(0, 0), 10)))          //> 原點的圓, 半徑 10
println(show(Circle(Point(10, 10), 20)))        //> 終點的圓, 半徑 20
println(show(Circle(Point(5, 5), 30)))          //> 其它的圓, 半徑 30
```
- 常數模式（Constant pattern）: ```0```, ```10```
- 萬用字元模式（Wildcard pattern）: ```_```
- 建構式模式（Constructor pattern）: ```Point(0, 0)```
- 變數模式（Variable pattern）: ```r```

____
[Case Classes and Pattern Matching](https://www.artima.com/pins1ed/case-classes-and-pattern-matching.html)

Wildcard patterns
  - The wildcard pattern (_) matches any object whatsoever.
 
Constant patterns
  - A constant pattern matches only itself. Any literal may be used as a constant. For example, 5, true, and "hello" are all constant patterns.
  
Variable patterns
  - A variable pattern matches any object, just like a wildcard. Unlike a wildcard, Scala binds the variable to whatever the object is. You can then use this variable to act on the object further.
  
Constructor patterns
  - Constructors are where pattern matching becomes really powerful. A constructor pattern looks like "BinOp("+", e, Number(0))". It consists of a name (BinOp) and then a number of patterns within parentheses: "+", e, and Number(0).
  
Sequence patterns
  - You can match against sequence types like List or Array just like you match against case classes. Use the same syntax, but now you can specify any number of elements within the pattern
  
Tuple patterns
  - You can match against tuples, too. A pattern like (a, b, c) matches an arbitrary 3-tuple. 
  
Typed patterns
- You can use a typed pattern as a convenient replacement for type tests and type casts.
