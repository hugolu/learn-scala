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
