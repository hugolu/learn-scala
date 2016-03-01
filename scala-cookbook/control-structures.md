# Control Structures

## Looping with for and foreach

## Using for Loops with Multiple Counters

## Using a for Loop with Embedded if Statements (Guards)

## Creating a for Comprehension (for/yield Combination)

## Implementing break and continue

## Using the if Construct Like a Ternary Operator

## Using a Match Expression Like a switch Statement

## Matching Multiple Conditions with One Case Statement

## Assigning the Result of a Match Expression to a Variable

## Accessing the Value of the Default Case in a Match Expression

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

## Working with a List in a Match Expression

## Matching One or More Exceptions with try/catch

## Declaring a Variable Before Using It in a try/catch/finally Block

## Creating Your Own Control Structures
