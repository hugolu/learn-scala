# Enumeration

參考連結：http://openhome.cc/Gossip/Scala/Enumeration.html

```scala
class Enum {
  class Value
}

object Action extends Enum {
  type Action = Value
  val Up, Down, Left, Right = new Value
}

def showAction(a: Action) = a match {
  case Up    => println("Up")
  case Down  => println("Down")
  case Left  => println("Left")
  case Right => println("Right")
}                                               //> showAction: (a: myTest.Action.Action)Unit

showAction(Up)                                  //> Up
showAction(Down)                                //> Down
showAction(Left)                                //> Left
showAction(Right)                               //> Right
```
