# Typeclasses

## 簡化版本
目的：希望不透過繼承，擴充 `Int`、`Double`、`String` 的功能，把值顛倒過來
- `!123` 得到 `321`
- `!123.456` 得到 `654.321`
- `!"ABC"` 得到 `"CBA"`

```scala
trait Foo[A] {
    def reverse(x: A): A
}

object Foo {
    def apply[A: Foo]: Foo[A] = implicitly[Foo[A]]

    implicit val IntFoo = new Foo[Int] {
        def reverse(x: Int): Int = x.toString.reverse.toInt
    }

    implicit val DoubleFoo = new Foo[Double] {
        def reverse(x: Double): Double = x.toString.reverse.toDouble
    }

    implicit val StringFoo = new Foo[String] {
        def reverse(x: String): String = x.reverse
    }

    implicit class Bar[A: Foo](x: A) {
        def unary_! = Foo[A].reverse(x)
    }
}
```
- 定義 type class trait，宣告型別 `Foo[A]` 的抽象方法 `revere`，將傳入的參數顛倒過來
- implicit class 擴充型別 `A` 的方法，如果對型別 `A` 呼叫 `unary_!` 方法，感覺上呼叫了 `!A`，實際上呼叫 `Foo[A].reverse(x)` 

```scala
scala> import Foo._
import Foo._

scala> !123
res0: Int = 321
```
- 呼叫 `!123`，發現 `Int` 沒有 `unary_!` 方法
- 試著在隱式作用域找尋隱式轉換，找到 `implicit class Bar[A: Foo](x: A)` 有包裝 `Int` 且提供 `unary_!` 方法
- `unary_!` 呼叫 `Foo[Int]` 型別的 `reverse` 方法
- 呼叫 `Foo[Int]` 觸發 `object Foo` 的 `apply`方法，呼叫 `implicitly[Foo[Int]]`
- 得到隱式實體 `implicit val IntFoo`
- 執行 `Foo[Int].reverse` 方法

```scala
scala> !123.456
res1: Double = 654.321

scala> !"ABC"
res2: String = CBA
```

## Codec

### 定義 type class trait 的抽象介面
```scala
trait Codec[A] {
  def encode(x: A, bv: ByteVector): Option[ByteVector]
  def decode(bv: ByteVector): Option[(A, ByteVector)]
}
```

### 定義 Codec companion object
```scala
object Codec {
  def apply[A : Codec]: Codec[A] = implicitly[Codec[A]]
  
  implicit val intCodec = new Codec[Int] { ... }
  implicit val stringCodec = new Codec[String] { ... }
  implicit val addressCodec = new Codec[Address] { ... }
  
  implicit class Ops[A : Codec](x: A) {
    def ~>(bv: ByteVector): Option[ByteVector] = Codec[A].encode(x, bv)
  }
}
```

### 測試
```scala
  import Codec._
  for {
    bv <- 123 ~> ByteVector.empty
    bv2 <- "hello" ~> bv
    (i, bv3) <- Codec[Int].decode(bv2)
    (s, bv4) <- Codec[String].decode(bv3)
  } assert(i == 123 && s == "hello" && bv4 == ByteVector.empty)
```
- 使用 “for comprehension” 連續處理 `Option` 運算 (如果為 `None` 結束運算)
- `bv <- 123 ~> ByteVector.empty`：將 `123` 與空陣列編碼成 `ByteVector`，儲存到 `bv`
- `bv2 <- "hello" ~> bv`：將 `"hello"` 與 `bv` 編碼成 `ByteVector`，儲存到 `bv2`
- `(i, bv3) <- Codec[Int].decode(bv2)`：使用 `Codec[Int].decode` 方法，取出 `123`
- `(s, bv4) <- Codec[String].decode(bv3)`：使用 `Codec[String].decode` 方法，取出 `"hello"`
