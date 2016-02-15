# closure

參考連結：http://openhome.cc/Gossip/Scala/Closure.html

閉包（Closure）是擁有閒置變數（Free variable）的運算式。
- 閒置變數是指對於函式而言，既非區域變數也非參數的變數，其作用範圍基本上在被定義的函式範圍中。
- 它是被綁定變數（Bound variable）。
- 閒置變數真正扮演的角色依當時語彙環境（Lexical environment）而定。
- 建立函式不等於建立閉包。如果函式的閒置變數與當時語彙環境綁定，該函式才稱為閉包。


```scala
def addNum(num: Int) = {
  def f = (x: Int) => num + x
  f
}                                               //> addNum: (num: Int)Int => Int

val add10 = addNum(10)                          //> add10  : Int => Int = <function1>
add10(1)                                        //> res0: Int = 11
add10(2)                                        //> res1: Int = 12
```
