# Call by Value vs Call by Name

```scala
scala> def one = {
     |   println("will return 1")
     |   1
     | }
one: Int

scala> one
will return 1
res0: Int = 1
```

## Call by Value
```scala
scala> def showNum(n: Int) = {
     |   println("show a number")
     |   println(n)
     | }
showNum: (n: Int)Unit

scala> showNum(one)
will return 1
show a number
1
```
- ```one```的值在呼叫```showNum```的時候就被 evaluate

## Call by Name
```scala
scala> def showNum(n: => Int) = {
     |   println("show a number")
     |   println(n)
     | }
showNum: (n: => Int)Unit

scala> showNum(one)
show a number
will return 1
1
```
- ```one```的值在```showNum```使用到的時候才去 evaluate

___
## What is the difference between “def” and “val” to define a value
```
val cbv = util.Random.nextInt                   //> cbv  : Int = -1419163692
cbv                                             //> res0: Int = -1419163692
cbv                                             //> res1: Int = -1419163692

def cbn = util.Random.nextInt                   //> cbn: => Int
cbn                                             //> res2: Int = -2019876673
cbn                                             //> res3: Int = 1024155007
```
- With ```def``` you can get new value on every evaluate

## What is the difference between “def” and “val” to define a function
```
val cbv = { val r = util.Random.nextInt; () => r }
                                             //> cbv  : () => Int = <function0>
cbv()                                           //> res0: Int = -1266642976
cbv()                                           //> res1: Int = -1266642976

def cbn = { val r = util.Random.nextInt; () => r }
                                             //> cbn: => () => Int
cbn()                                           //> res2: Int = -418853578
cbn()                                           //> res3: Int = 797816830
```
- With ```def``` you can get new function on every call
- ```val``` evaluates when defined, ```def``` evaluates when called
