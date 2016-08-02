# Typeclasses

“Typeclasses are among the most powerful features in Haskell. They allow you to define generic interfaces that provide a common feature set over a wide variety of types. Typeclasses are at the heart of some basic language features such as equality testing and numeric operators.” -- [Using Typeclasses](http://book.realworldhaskell.org/read/using-typeclasses.html)

### 需要 typeclass 的理由
- 判斷顏色是否相等: `colorEq :: Color -> Color -> Bool`
- 判斷字串是否相等: `stringEq :: [Char] -> [Char] -> Bool`
- 問題在於使用「不同名稱的函式」處理「不同的型別」，但事實上都做「比較」的工作。
- 這種寫法很沒效率也很困擾，不如使用 `==` 來比較任何東西更為方便。藉由一個通用的函式比較任何事物，能讓程式碼更加通用化：如果一段程式碼只做比較的工作，它應該能接收編譯器知道如何比較的任何資料型別。更甚者，如果新的資料型別稍後加入，現有的程式碼也不受影響。

## 一個簡單的實作
目的：提供一個方法顛倒 `Int`、`Double`、`String` 的值
- `!123` 得到 `321`
- `!123.456` 得到 `654.321`
- `!"ABC"` 得到 `"CBA"`

```scala
trait Reversable[A] {
    def reverse(x: A): A
}

object Reversable {
    def apply[A: Reversable]: Reversable[A] = implicitly[Reversable[A]]

    implicit val IntReversable = new Reversable[Int] {
        def reverse(x: Int): Int = x.toString.reverse.toInt
    }

    implicit val DoubleReversable = new Reversable[Double] {
        def reverse(x: Double): Double = x.toString.reverse.toDouble
    }

    implicit val StringReversable = new Reversable[String] {
        def reverse(x: String): String = x.reverse
    }

    implicit class Ops[A: Reversable](x: A) {
        def unary_! = Reversable[A].reverse(x)
    }
}
```
- 定義 type class trait，宣告型別 `Reversable[A]` 的抽象方法 `revere`，將傳入的參數顛倒過來
- implicit class 擴充型別 `A` 的方法，如果對型別 `A` 呼叫 `unary_!` 方法，感覺上呼叫了 `!A`，實際上呼叫 `Reversable[A].reverse(x)` 

```scala
scala> import Reversable._
import Reversable._

scala> !123
res0: Int = 321
```
- 呼叫 `!123`，發現 `Int` 沒有 `unary_!` 方法
- 試著在隱式作用域找尋隱式轉換，找到 `implicit class Bar[A: Reversable](x: A)` 有包裝 `Int` 且提供 `unary_!` 方法
- `unary_!` 呼叫 `Reversable[Int]` 型別的 `reverse` 方法
- 呼叫 `Reversable[Int]` 觸發 `object Reversable` 的 `apply`方法，呼叫 `implicitly[Reversable[Int]]`
- 得到隱式實體 `implicit val IntReversable`
- 執行 `Reversable[Int].reverse` 方法

```scala
scala> !123.456
res1: Double = 654.321

scala> !"ABC"
res2: String = CBA
```

## `@typeclass`
定義模組化 typeclass 的習慣步驟看起來像：

- 定義 typeclass trait Foo
- 定義伴隨物件，包含輔助方法 apply 作用像是 implicitly，與定義 Foo 實例
- 定義 FooOps 類別，定義一元(?)或二元操作子
- 定義 FooSyntax trait，從 Foo 實例隱喻提供 FooOps

坦白說，這些步驟大部份是複製貼上的樣板，除了第一個以外。匯入 Michael Pilquist 的 simulacrum，放上 `@typeclass` 標記，simulacrum 就能神奇的產生大部份 2-4 步驟。

### 手刻版
```scala
trait Appendable[A] {
    def append(x: A, y: A): A
}

object Appendable {
    def apply[A: Appendable]: Appendable[A] = implicitly[Appendable[A]]

    implicit val intAppendable = new Appendable[Int] {
        def append(x: Int, y: Int): Int = x + y
    }

    implicit val stringAppendable = new Appendable[String] {
        def append(x: String, y: String): String = x + y
    }

    implicit class Ops[A: Appendable](x: A) {
        def |+|(y: A): A = Appendable[A].append(x, y)
    }
}
```

### 使用 simulacrum 簡化步驟
```scala
import simulacrum.typeclass

@typeclass
trait Appendable[A] {
    @op("|+|") def append(x: A, y: A): A
}

object Appendable {
    implicit val intAppendable = new Appendable[Int] {
        def append(x: Int, y: Int): Int = x + y
    }

    implicit val stringAppendable = new Appendable[String] {
        def append(x: String, y: String): String = x + y
    }
}
```
- 自動產生 `apply` 與 `implicit class Ops[A: Appendable]`

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
case class Address(host: String, port: Int)

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

```scala
  val a = Address("localhost", 1234)
  for {
    bv <- a ~> ByteVector.empty
    (a2, bv2) <- Codec[Address].decode(bv)
  } assert(a == a2 && bv2 == ByteVector.empty)
```
- `bv <- a ~> ByteVector.empty`：將型別Address `a` 與空陣列編碼成 `ByteVector`，儲存到 `bv`
- `(a2, bv2) <- Codec[Address].decode(bv)`：使用 `Codec[Address].decode` 方法，取出 `a`
