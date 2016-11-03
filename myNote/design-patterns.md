# Design Patterns

明明是 functional programming，還玩 design pattern，我一定是瘋了，哈哈

## Abstract Factory
參考資料：[抽象工廠](https://zh.wikipedia.org/wiki/%E6%8A%BD%E8%B1%A1%E5%B7%A5%E5%8E%82)

![](https://upload.wikimedia.org/wikipedia/commons/9/9d/Abstract_factory_UML.svg)

```scala
trait Button
trait Border

class MacButton extends Button { override def toString = "MacButton" }
class MacBorder extends Border { override def toString = "MacBorder" }
class WinButton extends Button { override def toString = "WinButton" }
class WinBorder extends Border { override def toString = "WinBorder" }

trait Factory {
  def createButton: Button
  def createBorder: Border
}

class MacFactory extends Factory {
  def createButton = new MacButton
  def createBorder = new MacBorder
}

class WinFactory extends Factory {
  def createButton = new WinButton
  def createBorder = new WinBorder
}

class Client(factory: Factory) {
  def test = {
    val button = factory.createButton
    val border = factory.createBorder
    println(s"create $button and $border")
  }
}

val c1 = new Client(new MacFactory)
c1.test // create MacButton and MacBorder

val c2 = new Client(new WinFactory)
c2.test // create WinButton and WinBorder
```
