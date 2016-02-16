# Scala Type Bounds: Upper Bounds, Lower Bounds and View Bounds

參考連結：http://www.journaldev.com/9609/scala-typebounds-upper-lower-and-view-bounds

Type Bound是什麼?
- 在Scala中，Type Bound是加諸在型別參數(Type Parameter)或型別變數(Type Variable)的限制。藉由Type Bound，定義型別變數的限制。

Type Bound的好處
- 型別安全的應用開發

Type Bound的種類
- Upper Bounds
- Lower Bounds
- View Bounds

## Upper Bounds
```[T <: S]```，```T```是型別參數，```S```是種型別，表示參數型別```T```必須是```S```型別或是子型別(sub-type)。

## Lower Bounds
```[T >: S]```，```T```是型別參數，```S```是種型別，表示參數型別```T```必須是```S```型別或是超型別(super-type)。

## View Bounds
```[T <% S]```，```T```是型別參數，```S```是種型別，當型別參數需要隱式轉換，使用View Bound將型別```T```當成```S```型別或是子型別(sub-type)。

```scala
trait Movable {
  val name: String
  def move() = println(s"$name is moving")
}

class Shape {}
class Square extends Shape with Movable { val name = "Square" }
class Triangle extends Shape with Movable { val name = "Triangle" }

def move[T <% Movable](o: T) = o.move()         //> move: [T](o: T)(implicit evidence$3: T => myTest.test.Movable)Unit
move(new Square)                                //> Square is moving
move(new Triangle)                              //> Triangle is moving
```


