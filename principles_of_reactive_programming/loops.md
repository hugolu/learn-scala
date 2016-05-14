# Loops

命令式編程 (imperative programming) 的迴圈控制，能用函數 (function) 做到嗎？例如
```scala
def power(x: Double, exp: Int): Double = {
  var r = 1.0
  var i = exp
  while (i > 0) { r = r * x; i = i - 1 }
  r
}
```
因為 `while` 在 scala 是個關鍵字，所以迴圈函數名稱用 `WHILE` 代替

### while (condition) {command}
```scala
def WHILE(condition: => Boolean)(command: => Unit): Unit =
  if (condition) {
    command
    WHILE(condition)(command)
  } else ()
}
```
```scala
var n = 2                                       //> n  : Int = 2
var r = 1.0                                     //> r  : Double = 1.0
var x = 1.2                                     //> x  : Double = 1.2
WHILE(n > 0) { r = x * r; n = n - 1 }
println(r)                                      //> 1.44
```

兩個地方要注意
- `condition` 與 `command` 都是 passed by name, 每次循環呼叫到的時候才會求值
- `WHILE` 是 [tail recursive](https://zh.wikipedia.org/wiki/%E5%B0%BE%E8%B0%83%E7%94%A8)，編譯器可以優化程式使用固定的 stack size

### repeat {command} (condition)
```scala
def REPEAT(command: => Unit)(condition: => Boolean): Unit = {
  command
  if (condition) REPEAT(command)(condition) else ()
}
```
```scala
var n = 10                                      //> n  : Int = 10
var acc = 0                                     //> acc  : Int = 0
REPEAT{acc = acc + n; n = n - 1}(n > 0)
println(acc)                                    //> 55
```

### repeat {command} until (condition)
```scala
def REPEAT_UNTIL(command: => Unit)(condition: => Boolean): Unit = {
  command
  if (condition) () else REPEAT_UNTIL(command)(condition)
}
```
```scala
var n = 0                                       //> n  : Int = 0
var acc = 0                                     //> acc  : Int = 0
REPEAT_UNTIL{acc = acc + n; n = n + 1}(n > 10)
println(acc)                                    //> 55
```

### for-loops
```java
for (int i = 1; i < 3; i = i + 1) { print(i + " ") }
```

上面的 for-loop 無法用 high-order function 做到這樣，因為`condition`參數裡面包含變數 `i` 的宣告。但 scala 可以模擬另一種 for-loop，例如

```java
for (i <- 1 until 3){ print(i + " ") }
```

呼叫 collection 的 `def foreach(f: => Unit): Unit`，對每個元素使用 `f`，例如

```scala
for (i <- 1 until 3; j <- "abc") println (i + " " + j)
```

等同於
```scala
(1 until 3) foreach {i => "abc" foreach {j => println (i + " " + j)}}
```

執行結果都是
```scala
//> 1 a
//| 1 b
//| 1 c
//| 2 a
//| 2 b
//| 2 c
```
