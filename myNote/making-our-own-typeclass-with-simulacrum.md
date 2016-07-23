# Making our own typeclass with simulacrum

來源：http://eed3si9n.com/herding-cats/making-our-own-typeclass-with-simulacrum.html


定義模組化 typeclass 的習慣步驟看起來像：

1. 定義 typeclass trait `Foo`
2. 定義伴隨物件，包含輔助方法 `apply` 作用像是 `implicitly`，與定義 `Foo` 實例
3. 定義 `FooOps` 類別，定義一元或二元操作子
4. 定義 `FooSyntax` trait，從 `Foo` 實例隱喻提供 `FooOps`

坦白說，這些步驟大部份是複製貼上的樣板，除了第一個以外。輸入 Michael Pilquist 的 simulacrum。只要放上 `@typeclass` 標記，simulacrum 就能神奇的產生大部份 2-4 步驟。

## Yes-No typeclass

來看看是否能做出我們自己的 `truth` typeclass

```scala
scala> import simulacrum._
import simulacrum._

scala> :paste
// Entering paste mode (ctrl-D to finish)

@typeclass trait CanTruthy[A] { self =>
  /** Return true, if `a` is truthy. */
  def truthy(a: A): Boolean
}
object CanTruthy {
  def fromTruthy[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
    def truthy(a: A): Boolean = f(a)
  }
}

// Exiting paste mode, now interpreting.

defined trait CanTruthy
defined object CanTruthy
```

上面的巨集會產生豐富的操作

```scala
// This is the supposed generated code. You don't have to write it!
object CanTruthy {
  def fromTruthy[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
    def truthy(a: A): Boolean = f(a)
  }

  def apply[A](implicit instance: CanTruthy[A]): CanTruthy[A] = instance

  trait Ops[A] {
    def typeClassInstance: CanTruthy[A]
    def self: A
    def truthy: A = typeClassInstance.truthy(self)
  }

  trait ToCanTruthyOps {
    implicit def toCanTruthyOps[A](target: A)(implicit tc: CanTruthy[A]): Ops[A] = new Ops[A] {
      val self = target
      val typeClassInstance = tc
    }
  }

  trait AllOps[A] extends Ops[A] {
    def typeClassInstance: CanTruthy[A]
  }

  object ops {
    implicit def toAllCanTruthyOps[A](target: A)(implicit tc: CanTruthy[A]): AllOps[A] = new AllOps[A] {
      val self = target
      val typeClassInstance = tc
    }
  }
}
```

To make sure it works, let’s define an instance for Int and use it. The eventual goal is to get 1.truthy to return true:


為了確定能正常工作，定義一個 `Int` 實例並使用它。最終目的是執行 `1.truthy` 得到 `true`
```scala
scala> implicit val intCanTruthy: CanTruthy[Int] = CanTruthy.fromTruthy({
     | case 0 => false
     | case _ => true
     | })
intCanTruthy: CanTruthy[Int] = CanTruthy$$anon$1@36fd2771

scala> import CanTruthy.ops._
import CanTruthy.ops._

scala> 10.truthy
res0: Boolean = true
```

成功了！相當漂亮。有個警告是編譯需要 Macro Paradise 插件。一旦編譯成功，CanTruthy 使用者不需要 Macro Paradise 插件就能使用 `CanTruthy[Int]`。