# Partial Function

```scala
val f1: Int => String = {
  case 1 => "one"
}                                               //> f1  : Int => String = <function1>

val f2: Function1[Int, String] = {
  case 1 => "one"
}                                               //> f2  : Int => String = <function1>
```
- ```f1``` = ```f2```

```scala
f1(1)                                           //> res0: String = one
f1(2)                                           //> scala.MatchError: 2 (of class java.lang.Integer)
                                                //| 	at myTest.test17$$anonfun$main$1$$anonfun$2.apply(myTest.test17.scala:5)
                                                //| 
                                                //| 	at myTest.test17$$anonfun$main$1$$anonfun$2.apply(myTest.test17.scala:5)
                                                //| 
                                                //| 	at myTest.test17$$anonfun$main$1.apply$mcV$sp(myTest.test17.scala:14)
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                //| orksheetSupport.scala:65)
                                                //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                //| ksheetSupport.scala:75)
                                                //| 	at myTest.test17$.main(myTest.test17.scala:3)
                                                //| 	at myTest.test17.main(myTest.test17.scala)
```
- ```case 2``` is not defined

```scala
val h: PartialFunction[Int, String] = {
  case 1 => "one"
}                                               //> h  : PartialFunction[Int,String] = <function1>

h(1)                                            //> res1: String = one
h.isDefinedAt(1)                                //> res2: Boolean = true
h.isDefinedAt(2)                                //> res3: Boolean = false
```
- ```isDefinedAt(2)``` to test whether ```case 2``` is defined
