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

### WHILE
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
