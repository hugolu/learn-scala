# Design Patterns

明明是 functional programming，還玩 design pattern，我一定是瘋了，哈哈

## Abstract Factory
參考資料：[Abstract factory pattern](https://en.wikipedia.org/wiki/Abstract_factory_pattern)

用同一個介面來建立一整族相關或相依的物件，不需點名個物件真正所屬的具象類別。

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

## Factory Method
參考資料：[Factory method pattern](https://en.wikipedia.org/wiki/Factory_method_pattern

定義可資生成物件的介面，但讓子類別去決定該具現出哪一種類別的物件。此模式讓類別將具現化程序交付給子類別去處置。

![](https://upload.wikimedia.org/wikipedia/commons/a/a3/FactoryMethod.svg)

```scala
trait Product { abstract def fun: Unit }

class ConcreteProduct extends Product { def fun = println("I'm ConcreteProductProduct") }

trait Creator { def factoryMethod: Product }

class ConcreteCreator extends Creator { def factoryMethod = new ConcreteProduct }

val c = new ConcreteCreator
val p = c.factoryMethod
p.fun // I'm ConcreteProductProduct
```
