# Control Structures

## Looping with for and foreach

### `Array`
```scala
val array = Array(1, 2, 3)                      //> array  : Array[Int] = Array(1, 2, 3)

for (n <- array) yield (n * 2)                  //> res0: Array[Int] = Array(2, 4, 6)
array.map(_ * 2)                                //> res1: Array[Int] = Array(2, 4, 6)

for (n <- array if (n % 2 != 0)) yield (n)      //> res2: Array[Int] = Array(1, 3)
array.filter(_ % 2 != 0)                        //> res3: Array[Int] = Array(1, 3)
```

### `list`
```scala
val list = List(1, 2, 3)                        //> list  : List[Int] = List(1, 2, 3)

for (n <- list) yield (n * 2)                   //> res4: List[Int] = List(2, 4, 6)
list.map(_ * 2)                                 //> res5: List[Int] = List(2, 4, 6)

for (n <- list if (n % 2 != 0)) yield (n)       //> res6: List[Int] = List(1, 3)
list.filter(_ % 2 != 0)                         //> res7: List[Int] = List(1, 3)
```

### `Map`
```scala
val map = Map(1 -> 'A', 2 -> 'B', 3 -> 'C')     //> map  : scala.collection.immutable.Map[Int,Char] = Map(1 -> A, 2 -> B, 3 -> C)

for ((k, v) <- map) yield ((v, k))              //> res8: scala.collection.immutable.Map[Char,Int] = Map(A -> 1, B -> 2, C -> 3)
map.map(kv => (kv._2, kv._1))                   //> res9: scala.collection.immutable.Map[Char,Int] = Map(A -> 1, B -> 2, C -> 3)

for ((k, v) <- map if (k % 2 != 0)) yield ((k, v))
                                                //> res10: scala.collection.immutable.Map[Int,Char] = Map(1 -> A, 3 -> C)
map.filter(kv => kv._1 % 2 != 0)                //> res11: scala.collection.immutable.Map[Int,Char] = Map(1 -> A, 3 -> C)
```

### for loop counters
```scala
val list = List("A", "B", "C")                  //> list  : List[String] = List(A, B, C)

for (i <- 0 until list.length) println("list(" + i + ")=" + list(i))
                                                //> list(0)=A
                                                //| list(1)=B
                                                //| list(2)=C

for ((k, v) <- list.zipWithIndex) println(s"list($k)=$v")
                                                //> list(A)=0
                                                //| list(B)=1
                                                //| list(C)=2
```

## Using for Loops with Multiple Counters

```scala
for (i <- 1 to 3; j <- 1 to 3) println(s"($i,$j)")
                                                //> (1,1)
                                                //| (1,2)
                                                //| (1,3)
                                                //| (2,1)
                                                //| (2,2)
                                                //| (2,3)
                                                //| (3,1)
                                                //| (3,2)
                                                //| (3,3)

for {
  i <- 1 to 3
  j <- 1 to 3
} println(s"($i,$j)")                           //> (1,1)
                                                //| (1,2)
                                                //| (1,3)
                                                //| (2,1)
                                                //| (2,2)
                                                //| (2,3)
                                                //| (3,1)
                                                //| (3,2)
                                                //| (3,3)
```
- `for{...}` 可讀性比較高

## Using a for Loop with Embedded if Statements (Guards)

```scala
  for (i <- 1 to 100) {
    if (i % 3 == 0) {
      if (i % 7 == 0) {
        println(i)
      }
    }                                             //> 21
                                                  //| 42
                                                  //| 63
                                                  //| 84
  }

  for {
    i <- 1 to 100
    if (i % 3 == 0)
    if (i % 7 == 0)
  } println(i)                                    //> 21
                                                  //| 42
                                                  //| 63
                                                  //| 84
```
- **for-loop with guards** 大大提高可讀性

## Creating a for Comprehension (for/yield Combination)

```
val names = Array("chris", "ed", "maurice")     		//> names  : Array[String] = Array(chris, ed, maurice)
val capNames = for (e <- names) yield e.capitalize	//> capNames  : Array[String] = Array(Chris, Ed, Maurice)
```

## Implementing break and continue

```scala
println("\n=== BREAK EXAMPLE ===")              //> 
                                                //| === BREAK EXAMPLE ===
breakable {
  for (i <- 1 to 10) {
    println(i)
    if (i > 4) break // break out of the for loop
  }
}                                               //> 1
                                                //| 2
                                                //| 3
                                                //| 4
                                                //| 5
                                                
```

```scala
println("\n=== CONTINUE EXAMPLE ===")           //> 
                                                //| === CONTINUE EXAMPLE ===
val searchMe = "peter piper picked a peck of pickled peppers"
                                                //> searchMe  : String = peter piper picked a peck of pickled peppers

var numPs = 0                                   //> numPs  : Int = 0
for (i <- 0 until searchMe.length) {
  breakable {
    if (searchMe.charAt(i) != 'p') {
      break // break out of the 'breakable', continue the outside loop
    } else {
      numPs += 1
    }
  }
}
println("Found " + numPs + " p's in the string.")
                                                //> Found 9 p's in the string.
```

### scala 沒有支援 `braak` 與 `continue` 關鍵字，為什麼？
以下引用自 ⟪Programming in Scala⟫：

You may have noticed that there has been no mention of break or continue. Scala leaves out these commands because they do not mesh well with function literals, a feature described in the next chapter. It is clear what continue means inside a while loop, but what would it mean inside a function literal? While Scala supports both imperative and functional styles of programming, in this case it leans slightly towards functional programming in exchange for simplifying the language. Do not worry, though. There are many ways to program without break and continue, and if you take advantage of function literals, those alternatives can often be shorter than the original code.

## Using the if Construct Like a Ternary Operator

```scala
def max(x: Int, y: Int) = if (x > y) x else y   //> max: (x: Int, y: Int)Int
max(1, 2)                                       //> res0: Int = 2
```

## Using a Match Expression Like a switch Statement

```scala
val i = 6                                       //> i  : Int = 6
i match {
	case 1    => println("January")
	case 2    => println("February")
	case 3    => println("March")
	case 4    => println("April")
	case 5    => println("May")
	case 6    => println("June")
	case 7    => println("July")
	case 8    => println("August")
	case 9    => println("September")
	case 10   => println("October")
	case 11   => println("November")
	case 12   => println("December")
	
	// catch the default with a variable so you can print it
	case whoa => println("Unexpected case: " + whoa.toString)
}                                               //> June
```
## Matching Multiple Conditions with One Case Statement

```scala
val i = 6                                       //> i  : Int = 6
i match {
	case 1 | 2 | 3    => println("Spring")
	case 4 | 5 | 6    => println("Summer")
	case 7 | 8 | 9    => println("Autumn")
	case 10 | 11 | 12 => println("Winter")
	
	// catch the default with a variable so you can print it
	case whoa         => println("Unexpected case: " + whoa.toString)
}                                               //> Summer
```

## Assigning the Result of a Match Expression to a Variable

```scala
val someNumber = 7                              //> someNumber  : Int = 7
val evenOrOdd = someNumber match {
	case 1 | 3 | 5 | 7 | 9 => println("odd")
	case 2 | 4 | 6 | 8 | 10 => println("even")
}                                               //> odd
```

## Accessing the Value of the Default Case in a Match Expression

```scala
val i = 3                                       //> i  : Int = 3
i match {
  case 0    => println("1")
  case 1    => println("2")
  case whoa => println("You gave me: " + whoa)
}                                               //> You gave me: 3
```

## Using Pattern Matching in Match Expressions

```scala
case class Foo(val x: Int, val y: Int)

def whatIsIt(x: Any) = x match {
  // constant patterns
  case 0                 => "constant pattern: 0"
  case true              => "constant pattern: true"
  case "hello"           => "constant pattern: hello"
  case Nil               => "constant pattern: Nil"

  // sequence patterns
  case List(0, _, _)     => "sequence pattern: List(0, _, _)"
  case List(1, _*)       => "sequence pattern: List(1, _*)"
  case Vector(1, _*)     => "sequence pattern: Vector(1, _*)"

  // tuple patterns
  case (a, b)            => s"tuple pattern: ($a, $b)"

  // constructor patterns
  case Foo(1, 2)         => "constructor pattern: Foo(1, 2)"
  case Foo(123, y)       => s"constructor pattern: Foo(123, $y)"

  // typed patterns
  case s: String         => s"typed pattern: $s"
  case i: Int            => s"typed pattern: $i"
  case d: Double         => s"typed pattern: $d"
  case a: Array[Int]     => s"typed pattern: $a"
  case as: Array[String] => s"typed pattern: $as"
  case l: List[_]        => s"typed pattern: $l"
  case m: Map[_, _]      => s"typed pattern: $m"
  case foo: Foo          => s"typed pattern: $foo"

  // others
  case default           => s"others: $default"
}                                               //> whatIsIt: (x: Any)String

whatIsIt(0)                                     //> res0: String = constant pattern: 0
whatIsIt(true)                                  //> res1: String = constant pattern: true
whatIsIt("hello")                               //> res2: String = constant pattern: hello
whatIsIt("Nil")                                 //> res3: String = typed pattern: Nil

whatIsIt(List(0, 1, 2))                         //> res4: String = sequence pattern: List(0, _, _)
whatIsIt(List(1, 2, 3, 4))                      //> res5: String = sequence pattern: List(1, _*)
whatIsIt(Vector(1, 2, 3))                       //> res6: String = sequence pattern: Vector(1, _*)

whatIsIt(("hello", "world"))                    //> res7: String = tuple pattern: (hello, world)

whatIsIt(Foo(1, 2))                             //> res8: String = constructor pattern: Foo(1, 2)
whatIsIt(Foo(123, 456))                         //> res9: String = constructor pattern: Foo(123, 456)

whatIsIt("hello, scala")                        //> res10: String = typed pattern: hello, scala
whatIsIt(123)                                   //> res11: String = typed pattern: 123
whatIsIt(3.14)                                  //> res12: String = typed pattern: 3.14
whatIsIt(Array(1, 2, 3))                        //> res13: String = typed pattern: [I@2ed4e99c
whatIsIt(Array("apple", "banana", "carot"))     //> res14: String = typed pattern: [Ljava.lang.String;@1c3518c9
whatIsIt(List("apple", "banana", "carot"))      //> res15: String = typed pattern: List(apple, banana, carot)
whatIsIt(Map(1 -> "one", 2 -> "two"))           //> res16: String = typed pattern: Map(1 -> one, 2 -> two)
whatIsIt(Foo(22, 66))                           //> res17: String = typed pattern: Foo(22,66)

whatIsIt(null)                                  //> res18: String = others: null
```

- Constant patterns - A constant pattern can only match itself. Any literal may be used as a constant. If you specify a `0` as the literal, only an Int value of `0` will be matched.
- Variable patterns - Scala binds the variable to whatever the object is, which lets you use the variable on the right side of the case statement.
- Constructor patterns - The constructor pattern lets you match a constructor in a case statement.
- Sequence patterns - You can match against sequences like List, Array, Vector, etc. Use the `_` character to stand for one element in the sequence, and use `_*` to stand for “zero or more elements,” as shown in the examples.
- Tuple patterns - As shown in the examples, you can match tuple patterns and access the value of each element in the tuple. You can also use the `_` wildcard if you’re not interested in the value of an element.
- Type patterns - In the following example, `str: String` is a typed pattern, and `str` is a pattern variable
```scala
case str: String => s"you gave me this string: $str"
```

```scala
def whatIsIt(x: Any) = x match {
	case list: List(1, _*) => println(s"pattern match: $list")
	                                              // Multiple markers at this line
	                                              // '=>' expected but '(' found.
	                                              // type List takes type parameters

	case _ => println("not matched")
}                                               //> whatIsIt: (x: Any)Unit

whatIsIt(List(1,2,3))                           //> pattern match: List(1, 2, 3)
```
- The solution to this problem is to add a **variable-binding pattern** to the **sequence pattern**:
```scala
def whatIsIt(x: Any) = x match {
  case list @ List(1, _*) => println(s"pattern match: $list")
  case _                  => println("not matched")
}                                               //> whatIsIt: (x: Any)Unit

whatIsIt(List(1, 2, 3))                         //> pattern match: List(1, 2, 3)
```


```scala
  def whatIsIt(x: Any) = x match {
    case Some(_) => "get a Some(_)"
  }                                               //> whatIsIt: (x: Any)String

  whatIsIt(Some(1))                               //> res0: String = get a Some(_)
```
-  you can match a Some with the approach shown, but you can’t access its information on the righthand side of the expression. 

```scala
def whatIsIt(x: Any) = x match {
  case Some(x) => s"get a Some(x), x=$x"
}                                               //> whatIsIt: (x: Any)String

whatIsIt(Some(1))                               //> res0: String = get a Some(x), x=1
```
- you can access the value inside the Some

```scala
def whatIsIt(x: Any) = x match {
  case some@ Some(x) => s"get a $some"
}                                               //> whatIsIt: (x: Any)String

whatIsIt(Some(1))                               //> res0: String = get a Some(1)
```
- you access to the Some object itself

## Using Case Classes in Match Expressions

## Adding if Expressions (Guards) to Case Statements

```scala
case class Foo(x: Int)

def whatIsIt(x: Any) = x match {
case e: Int if e % 2 == 0 => "even integer"
case o: Int if o % 2 != 0 => "odd integer"
case Foo(x) if x > 100    => s"big Foo($x)"
case Foo(x) if x <= 100   => s"small Foo($x)"
case _                    => "other"
}                                               //> whatIsIt: (x: Any)String

whatIsIt(1)                                     //> res0: String = odd integer
whatIsIt(2)                                     //> res1: String = even integer
whatIsIt(Foo(0))                                //> res2: String = small Foo(0)
whatIsIt(Foo(1000))                             //> res3: String = big Foo(1000)
```

## Using a Match Expression Instead of isInstanceOf

```scala
case class Foo(value: Int)
val x: Any = Foo(1)                             //> x  : Any = Foo(1)

if (x.isInstanceOf[Foo]) {
val foo = x.asInstanceOf[Foo]
println(foo)
}                                               //> Foo(1)

x match {
case foo: Foo => println(foo)
case _        => {}
}                                               //> Foo(1)
```
- use pattern matching instead of `isInstanceOf`

## Working with a List in a Match Expression

```scala
def sum(list: List[Int]): Int = list match {
case Nil     => 0
case x :: xs => x + sum(xs)
}                                               //> sum: (list: List[Int])Int
sum(List(1, 2, 3, 4, 5))                        //> res0: Int = 15

def acc(list: Seq[Int]): Int = list match {
case Nil              => 0
case Seq(x, xs @ _*) => x + acc(xs)
}                                               //> acc: (list: Seq[Int])Int
acc(List(1, 2, 3, 4, 5))                        //> res1: Int = 15

```

## Matching One or More Exceptions with try/catch

```scala
def toInt(s: String): Option[Int] =
try {
Some(s.toInt)
} catch {
case e: Exception => None
}                                             //> toInt: (s: String)Option[Int]

toInt("123")                                    //> res0: Option[Int] = Some(123)
toInt("abc")                                    //> res1: Option[Int] = None
```

## Declaring a Variable Before Using It in a try/catch/finally Block

## Creating Your Own Control Structures
```scala
var i = 0                                       //> i  : Int = 0
while (i < 3) {
  println(i)
  i = i + 1
}                                               //> 0
                                                //| 1
                                                //| 2
```

```scala
def loop(condition: => Boolean)(codeBlock: => Unit) {
  if (condition) {
    codeBlock
    loop(condition)(codeBlock)
  }
}                                               //> loop: (condition: => Boolean)(codeBlock: => Unit)Unit

var i = 0                                       //> i  : Int = 0
loop(i < 3) {
  println(i)
  i = i + 1
}                                               //> 0
                                                //| 1
                                                //| 2
```
- 實作取代 while-loop 的 tail recursion 範例
