import test.Foo
import test.{Foo => Bar}

object Test extends App {
    println("new Foo => " + new Foo)
    println("new Bar => " + new Bar)

}
