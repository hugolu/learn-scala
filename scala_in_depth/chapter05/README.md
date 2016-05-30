# Chapter 5 用隱喻方式寫出有表達力的程式碼

Scala 編譯器會在編譯期間推測使用者沒有明確寫出來的訊息

- missing parameter: 方法或建構函數遺漏參數
- missing conversion: 型別間沒有明確指出如何轉換，或是呼叫方法接收的物件需要轉換

## 5.1 簡介隱喻 (implicit)

`implicit` 關鍵字有兩種用法

- 方法與參數定義：告訴編譯器解析隱喻時，可以使用這些方法或參數定義
- 方法參數列表：告訴編譯器參數列表可能遺缺，編譯器應該在解析隱喻 (implicit resolution) 時把他們找出來

```scala
scala> def findAnInt(implicit x : Int) = x
findAnInt: (implicit x: Int)Int
```
定義 `findAnInt` 方法，用 `implicit` 標示 `x` 參數，表示如果呼叫者沒提供，編譯器要在隱喻範圍 (implicit scope) 搜尋 `Int` 的變數

```scala
scala> findAnInt
<console>:12: error: could not find implicit value for parameter x: Int
       findAnInt
       ^
```
在沒有提供隱喻值情況下，呼叫沒提供參數會發生錯誤

```scala
scala> implicit val test = 5
test: Int = 5
```
使用 `implicit` 宣告 `test` (名字隨便取)，所以將來解析隱喻就能找到整數的隱喻值

```scala
scala> findAnInt
res1: Int = 5
```
呼叫 `findAnInt` 沒有提供參數，編譯器使用隱喻值

```scala
scala> findAnInt(2)
res2: Int = 2
```
當然，在有提供參數的情況下，就會使用提供的參數

### 5.1.1 識別符 (identifier)

探索隱喻解析機制前，先理解編譯器如何在特定範圍解析識別符。識別符在隱喻選擇上扮演關鍵的角色。

- 使用 *Entity* (實體) 表示型別、值、方法、類別。
- 使用 *Binding* (綁定) 當作名字參考到實體

```scala
scala> class Foo{}
defined class Foo

scala> new Foo
res1: Foo = Foo@6a6824be
```
- Foo 類別是一個 entity；這個類別 binding 一個名字 `Foo`
- 使用名稱 `Foo` 表示型別，可以產生物件物件

```scala
scala> object test {
     |   class Foo{}
     | }
defined object test

scala> new Foo
<console>:11: error: not found: type Foo
       new Foo
           ^

scala> new test.Foo
res1: test.Foo = test$Foo@707f7052

scala> import test.Foo
import test.Foo

scala> new Foo
res2: test.Foo = test$Foo@5b464ce8
```
- `Foo` 類別是 `object test` 的成員
- 直接使用 `Foo` 在 local scope 找不到這個類別，`new Foo` 發生錯誤
- 要用 `new test.Foo` 產生物件，或是
- 使用 `import` 關鍵字把 `test.Foo` 綁定到 loccal scope 的 `Foo`，然後用 `new Foo` 才能產生物件

```scala
scala> import test.{Foo => Bar}
import test.{Foo=>Bar}

scala> new Bar
res3: test.Foo = test$Foo@6500df86
```
- Scala `import` 還可以給綁定的實體取任意名字
- 這個彈性在使用 Java package 得到很大的方便，例如
  - `import java.util.{List=>JList}`
  - `import java.{io=>jio}`

### 5.1.2 範圍與綁定 (Scope and bindings)

範圍 (Scope) 是決定 binding 是否可行的邊界。

```scala
scala> class Foo(x: Int) {
     |   def tmp = {
     |     x
     |   }
     | }
defined class Foo

scala> val foo = new Foo(3)
foo: Foo = Foo@5680a178

scala> foo.tmp
res1: Int = 3
```
- `tmp` 方法定義在巢狀範圍內，內層仍然可以讀取外層的參數

```scala
scala> class Bar(x: Int) {
     |   def tmp = {
     |     var x = 2
     |     x
     |   }
     | }
defined class Bar

scala> val bar = new Bar(3)
bar: Bar = Bar@281e3708

scala> bar.tmp
res2: Int = 2
```
- `tmp` 方法裡面定義 `x` 同名變數，將遮蔽 (shadow) 巢狀範圍外層的 `x`

#### 綁定的優先順序 (由高至低)
1. 局部、繼承、同檔案內 `packet` 的定義與宣告
2. 明確匯入
3. 萬用自元匯入
4. 使用 `package` 的定義與宣告

程式碼 - [bindings](bindings)

##### externalbindings.scala
```scala
package test

object x {
  override def toString = "Externally bound x object in package test."
}
```

##### test.scala
```scala
package test

object Test {
    def main(args: Array[String]) {
        testSamePackage()
        testWildcardImport()
        testExplicitImport()
        testInlineDefinition()
    }

    def testSamePackage() = {
        println(x)
    }

    object Wildcard {
        def x = "Wildcard Import x"
    }
    def testWildcardImport() = {
        import Wildcard._
        println(x)
    }

    object Explicit {
        def x = "Explicit Import x"
    }
    def testExplicitImport() = {
        import Explicit.x
        import Wildcard._
        println(x)
    }

    def testInlineDefinition() = {
        val x = "Inline definition x"
        import Explicit.x
        import Wildcard._
        println(x)
    }
}
```

| 呼叫 | 結果 | 最高優先權 |
|------|------|------|
| `testSamePackage` | Externally bound x object in package test. | 同 `package` 的宣告 |
| `testWildcardImport` | Wildcard Import x | 萬用自元匯入 |
| `testExplicitImport` | Explicit Import x | 明確匯入 |
| `testInlineDefinition` | Inline definition x | 局部變數 |

### 5.1.3 隱喻解析 (Implicit resolution)

兩條規則，用來搜尋標記為隱喻的實體

- 隱喻綁定沒有使用前綴，x 就是 `x` 不會是 `foo.x`
- 如果上面找不到可用的實體，搜尋所有屬於隱喻範圍 (implicit scope) 內物件的 `implicit` 成員

```scala
scala> def findAnInt(implicit x: Int) = x
findAnInt: (implicit x: Int)Int

scala> object test {
     |   implicit val foo = 5
     | }
defined object test

scala> findAnInt
<console>:12: error: could not find implicit value for parameter x: Int
       findAnInt
       ^

scala> implicit val bar = 6
bar: Int = 6

scala> findAnInt
res1: Int = 6
```
- 規則一：隱喻綁定只會在局部範圍內找沒有前綴的實體 (available on the local scope with no prefix)

```scala
scala> object holder {
     |   trait Foo
     |   object Foo {
     |     implicit val x = new Foo {
     |       override def toString = "Companion Foo"
     |     }
     |   }
     | }
defined object holder

scala> import holder.Foo
import holder.Foo

scala> def method(implicit foo: Foo) = println(foo)
method: (implicit foo: holder.Foo)Unit

scala> method
Companion Foo
```
- 規則二：當編譯器靠規則一找不到可用的隱喻成員，會在隱喻範圍內搜尋伴生物件 (companion object) 內定義的隱喻成員。

型別 `T` 的隱喻範圍是一組跟 `T` 相關的伴生物件：

- `T` 的子型別
- 如果 `T` 是型別參數，隱喻範圍包含部分型別。例如隱喻搜尋 `List[String]`，則 `List` 與 `String` 的伴生物件都在搜尋範圍內
- 如果 Singleton 型別 `T` 在某物件內，這個物件也會在搜尋範圍內
- 如果 `T` 是型別投影 `S#T`，`S` 的一部分也包含在型別 `T` 的部分內 (不懂?? 6.1.1 會詳述)

#### 型別參數衍伸的隱喻範圍 (IMPLICIT SCOPE VIA TYPE PARAMETERS)

```scala
scala> object holder {
     |   trait Foo
     |   object Foo {
     |     implicit val list = List(new Foo{ override def toString = "Foo!!" })
     |   }
     | }
defined object holder

scala> import holder.Foo
import holder.Foo

scala> def method(implicit list: List[Foo]) = println(list(0))
method: (implicit list: List[holder.Foo])Unit

scala> method
Foo!!
```
- 型別參數為 `List[Foo]`，隱喻範圍包含 `List` 與 `Foo` 的伴生物件

```scala
scala> implicitly[List[holder.Foo]]
res2: List[holder.Foo] = List(Foo!!)
```
- 定義: `def implicitly[T](implicit arg : T) = arg` 
- 用這個函數在目前的隱喻範圍找尋型別 (6.2 會詳述)
- 回傳定義在 `Foo` 伴生物件內的的隱喻列表 (implicit list)

#### 巢狀隱喻範圍 (IMPLICIT SCOPE VIA NESTING)

```scala
scala> object Foo {
     |   trait Bar
     |   implicit def newBar = new Bar {
     |     override def toString = "Implicit Bar"
     |   }
     | }
defined object Foo

scala> implicitly[Foo.Bar]
res0: Foo.Bar = Implicit Bar
```
- 外部型別是 `Foo`，裡面定義特徵 `Bar`
- `Foo` 物件裡面定義一個隱喻產生`Bar`實例的方法
- 當呼叫 `implicitly[Foo.Bar]`，隱喻值透過外部型別 `Foo` 找到產生 `Bar` 的方法

```scala
scala> object Foo {
     |   object Bar { override def toString = "Bar" }
     |   implicit def b: Bar.type = Bar
     | }
defined object Foo

scala> implicitly[Foo.Bar.type]
res0: Foo.Bar.type = Bar
```
- 物件不能有隱喻伴生物件 (Scala objects can’t have companion objects for implicits.)
- 隱喻關聯物件的型別要加上 `.type`，例如 `Bar.type`
- 當呼叫 `implicitly[Foo.Bar.type]`，隱喻值透過外部型別 `Foo` 找到產生 `Bar.type` 的方法

#### Package object

對於定義在 package 裡面所有型別，任何定義在 package object 裡的隱喻都在隱喻範圍內。

程式碼 - [implicit-resolution](implicit-resolution)

##### package.scala
```scala
package object foo {
    implicit def foo = new Foo
}

package foo {
    class Foo {
        override def toString = "Foo!"
    }
}
```

##### test.scala
```scala
object Test extends App {
    def method(implicit x: foo.Foo) = println(x)
    method
}
```

## 5.2 用隱喻視圖擴充現有類別 (Enhancing existing classes with implicit views)

implicit view 能自動轉換型別以滿足表示式。

形式：`implicit def <myConversion-Name>(<argumentName>: OriginalType): ViewType`

- 把 `OriginalType` 轉換成 `ViewType` 

```scala
scala> def foo(msg: String) = println(msg)
foo: (msg: String)Unit

scala> foo(5)
<console>:12: error: type mismatch;
 found   : Int(5)
 required: String
       foo(5)
           ^

scala> implicit def intToString(x: Int): String = x.toString

scala> foo(5)
5
```
- 使用 `implicit` 關鍵字定義 `intToString` 方法，接收 `Int` 參數，回傳 `String` 值
- `intToString`的型別是 `Int => String` 
- `foo` 方法接受 `String` 參數，但傳入型別為 `Int`，編譯器會尋找能修正情況的 implicit view

Implicit view 在兩種情況下使用：

1. 如果表示式沒有符合編譯器的期待，編譯器會尋找能夠符合預期型別的 implicit view
2. `e.t` 表示存取成員，如果 `e` 型別沒有 `t` 這個成員變數，編譯器尋找用來轉換 `e` 的 implicit view，讓轉換後的型別有 `t` 這個成員變數

```scala
scala> object test {
     |   trait Foo
     |   trait Bar
     |   object Foo {
     |     implicit def fooToBar(foo: Foo) = new Bar{
     |       override def toString = "Bar!"
     |     }
     |   }
     | }

scala> import test._
import test._

scala> def bar(x: Bar) = println(x)
bar: (x: test.Bar)Unit

scala> var x = new Foo{}
x: test.Foo = $anon$1@59e84876

scala> bar(x)
Bar!
```
- 伴生物件 `Foo` 包含一個將型別 `Foo` 轉換成型別 `Bar` 的 implicit view
- 當編譯器要尋找能將 `Foo` 隱喻轉換成 `Bar` 的 implicit view 時，它會到伴生物件 `Foo` 裡面找

> 經實驗，把 implicit view 放在伴生物件 `Bar` 中，scala 2.11.7 編譯器也能知道如何轉換

隱喻風格讓我們能將一個函式庫調整成另一個，或是把便利的方法增加到型別內。

#### 將 java 函式庫轉換成 scala 函式庫

程式碼 - [ScalaSecurityImplicits](ScalaSecurityImplicits)

##### ScalaSecurityImplicits.scala
```scala
import java.security._

object ScalaSecurityImplicits {
    implicit def functionToPrivilegedAction[A](func: Function0[A]) =
        new PrivilegedAction[A] {
            override def run() = func()
        }
}
```
- `functionToPrivilegedAction` 這個 implicit view 把 `Function0` 轉換成 `PrivilegedAction`

##### test.scala
```scala
import ScalaSecurityImplicits._
import java.security._

object Test extends App {
    AccessController.doPrivileged( () => println("This is privileged.") )
}
```
- `java.security.AccessController` 的 `doPrivileged` 方法接受 `PrivilegedExceptionAction` 型別
- 當傳遞匿名函數 `() => println("this is privileged")` 給 `doPrivileged` 時，不符合預期型別，編譯器會試圖找尋 implicit view，接著將匿名函數從 scala 物件包裝成 java 物件
- 當使用 java 函式庫，寫個包裝類別的做法相當普遍，藉以增加更多進階的 scala 用法。

#### 為型別增添方法

程式碼 - [FileWrapper](FileWrapper)

##### FileWrapper.scala
```scala
class FileWrapper(val file: java.io.File) {
    def /(next: String) = new FileWrapper(new java.io.File(file, next))
    override def toString = file.getCanonicalPath
}

object FileWrapper {
    implicit def wrap(file: java.io.File) = new FileWrapper(file)
    implicit def unwrap(wrapper: FileWrapper) = wrapper.file
}
```
- `FileWrapper` 類別建構函數接收 `java.io.File`，並提供 `/` 方法產生另一個 `FileWrapper`
- `FileWrapper` 伴生物件提供兩個 implicit view，轉換 `java.io.File` 與 `FileWrapper` 兩種型別

##### test.scala
```scala
import FileWrapper.wrap

object Test extends App {
    val cur = new java.io.File(".")
    println(cur / "temp.txt")

    def useFile(file : java.io.File) = println(file.getCanonicalPath)
    useFile(cur / "temp.txt")
}
```
- 當呼叫 `java.io.File` 的 `/` 方法，編譯器在這個類別內找不到方法，試圖尋找可用的 implicit view (`Function1[java.io.File, FileWrapper]`)，然後找到 `wrap` 函數將 `java.io.File` 包裝成 `FileWrapper`，最後呼叫 `/` 方法
- 當傳遞 `FileWrapper` 物件給 `useFile` 函數，編譯器發現這個函數期望接收 `java.io.File` 物件，試圖尋找可用的 implicit view (`Function1[FileWrapper, java.io.File]`)，然後找到 `unwrap` 函數將 `FileWrapper` 物件脫殼得到 `java.io.File`，最後傳給 `useFile` 函數處理

implicit view 很好用，但有幾個考量點

- 隱喻轉換可能帶來效能的問題，`HotSpot` 優化處理或許可以減輕或許不能
- 使用太多 implicit view 會拉高新進開發人員的進入門檻

## 5.3 預設使用隱喻參數 (Utilize implicit parameters with defaults)

隱喻參數讓使用者不必重複定義參數，以下用計算 “MxN 矩陣乘法” 當範例

- 單一執行緒
- 多執行緒
- 使用 implicit 定義預設計算方式

程式碼 - [Matrix](Matrix)

##### Matrix.scala
```scala
import scala.collection.mutable.ArrayBuffer

class Matrix(private val repr: Array[Array[Double]]) {
    def row(idx: Int): Seq[Double] = {
        repr(idx)
    }

    def col(idx: Int): Seq[Double] = {
        repr.foldLeft(ArrayBuffer[Double]()) { (buffer, currentRow) =>
            buffer.append(currentRow(idx))
            buffer
        } toArray
    }

    lazy val rowRank = repr.size
    lazy val colRank = if (rowRank > 0) repr(0).size else 0

    override def toString = "Matrix" + repr.foldLeft("") {
        (msg, row) => msg + row.mkString("\n|", " | ", "|")
    }
}
```
- `row` 方法回傳某 *列* 的值；`rowRank` 取得列數
- `col` 方法回傳某 *行* 的值；`colRank` 取得行數
- `toString` 印出矩陣內容

##### MatrixUtils.scala
```scala
object MatrixUtils {
    def multiply(a: Matrix, b: Matrix)(implicit threading: ThreadStrategy = SameThreadStrategy): Matrix = {

        assert(a.colRank == b.rowRank)

        val buffer = new Array[Array[Double]](a.rowRank)
        for (i <- 0 until a.rowRank) {
            buffer(i) = new Array[Double](b.colRank)
        }

        def computeValue(row: Int, col: Int): Unit = {
            val pairwiseElements = a.row(row).zip(b.col(col))
            val products = for ((x,y) <- pairwiseElements) yield x*y
            val result = products.sum
            buffer(row)(col) = result
        }

        val computations = for {
            i <- 0 until a.rowRank
            j <- 0 until b.colRank
        } yield threading.execute { () => computeValue(i, j) }

        computations.foreach(_())
        new Matrix(buffer)
    }
}
```
- `MatrixUtils` 物件提供 `multiply` 函數 (Scala Currying Function)，接受兩個矩陣 `a` 與 `b`，以及 `threading` 參數決定要用單一/多重執行緒方式
-  `assert(a.colRank == b.rowRank)` 檢查 `a` 的行數與 `b` 的列數是否相等
-  `buffer = new Array[Array[Double]]` 產生一個二維陣列
-  `computeValue` 計算 `a` 某列乘上 `b` 某行的乘積
-  `computations` 所有行列相乘的計算
-  `computations.foreach(_())` 真正進行計算的地方
-  `new Matrix(buffer)` 回傳計算的結果

```scala
trait ThreadStrategy {
    def execute[A](func: Function0[A]): Function0[A]
}
```
- 介面 `ThreadStrategy` 定義一個 `execute` 方式，傳入型別 `A` 參數，回傳型別 `A` 結果
- 呼叫 `execute` 可能會阻斷 (block) 當前的執行緒直到得到結果 (這很好理解，如果你不是用 multi-thread 或 concurrent 方式呼叫函數，當然要等函數執行完，才會回到呼叫位置繼續往下走)

```scala
/* Simple Strategy */
object SameThreadStrategy extends ThreadStrategy {
    def execute[A](func: Function0[A]) = func
}
```
- 單一執行緒的做法 - 讓當前執行緒直接執行傳入的函數

```scala
/* Concurrent Strategy */
import java.util.concurrent.{Callable, Executors}

object ThreadPoolStrategy extends ThreadStrategy {
    val pool = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime.availableProcessors)

    def execute[A](func: Function0[A]) = {
        val future = pool.submit(new Callable[A] {
            def call(): A = {
                Console.println("Executing function on threads: " +
                    Thread.currentThread.getName)
                func()
            }
        })
        () => future.get()
    }
}
```
- 並行方式作法 - `ThreadPoolStrategy` 物件擁有一個執行者的儲存池，呼叫 `execute` 就將要執行的 `func` 用 `Callable` 包起來放到執行者的儲存池，然後回傳一個 `future` 
- `() => future.get()` 回傳一個呼叫 `future.get` 的 anonymous closure，這個呼叫會先卡住直到有人執行它，並回傳 `func` 的回傳值 (這個機制還不熟...)
- 另外，每次 `Callable` 裡面的 `call` 被呼叫，會順便印出執行緒的資訊

##### 使用 SameThreadStrategy
```scala
object Test1 extends App {
    implicit val ts = SameThreadStrategy

    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}
```
- 提供 `implicit val ts = SameThreadStrategy` 使用單一執行緒

```
[info] Running Test1
Matrix
|1.0 | 2.0 | 3.0|
|4.0 | 5.0 | 6.0|
Matrix
|1.0|
|1.0|
|1.0|
Matrix
|6.0|
|15.0|
```

##### 使用 ThreadPoolStrategy
```scala
object Test2 extends App {
    implicit val ts = ThreadPoolStrategy

    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}
```
- 提供 `implicit val ts = ThreadPoolStrategy` 使用多執行緒

```
[info] Running Test2
Matrix
|1.0 | 2.0 | 3.0|
|4.0 | 5.0 | 6.0|
Matrix
|1.0|
|1.0|
|1.0|
Executing function on threads: pool-3-thread-1
Executing function on threads: pool-3-thread-2
Matrix
|6.0|
|15.0|
```

##### 使用預設參數
```scala
def multiply(a: Matrix, b: Matrix)(implicit threading: ThreadStrategy = SameThreadStrategy): Matrix = { ... }
```
```scala
object Test3 extends App {
    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}
```
- 使用預設參數 `SameThreadStrategy`

```
[info] Running Test3
Matrix
|1.0 | 2.0 | 3.0|
|4.0 | 5.0 | 6.0|
Matrix
|1.0|
|1.0|
|1.0|
Matrix
|6.0|
|15.0|
```

## 5.4 限制隱喻的範圍 (Limiting the scope of implicits)

隱喻可能出現在：

1. 任何相關連型別的**伴生物件**
2. `scala.Predef._`
  - `Predef` 物件包含許多有用的轉換，例如 `java.lang.Integer => scala.Int` 轉換 java boxed type 與 scala unified type
3. 藉由 `import` 帶入的隱喻範圍
  - 這樣的隱喻難以追蹤，也難以文件化

### 5.4.1 為匯入產生隱喻 (Creating implicits for import)

透過 `import` 導入 implicit view 或 implicit parameter 要確保以下事項

- 沒有衝突發生
- 名字沒有跟 `scala.Predef` 裡面的任何東西衝突
- 使用者可以找得到 (discoverable)

程式碼 - [Time](Time)

##### Time.scala
```scala
object Time {
    case class TimeRange(start: Long, end: Long)
    implicit def longWrapper(start: Long) = new {
        def to(end: Long) = TimeRange(start, end)
    }
}
```
- `Time` 物件包含一個類別 (`TimeRange`) 與一個隱喻轉換 (`longWrapper`)
- `longWrapper` 可能跟 `scala.Predef.longWrapper` 發生衝突

當 `import Time.longWrapper`，`1L to 10L` 會變成...
- `1L` 轉變成一個 `AnyRef` 物件，裡面有一個 `to` 方法 (接收 `Long` 回傳 `TimeRange`)
- `AnyRef.to(10L)` 回傳 `TimeRange(1L, 10L)` 物件

##### test.scala
```scala
object Test extends App {
    println(1L to 10L)

    import Time._
    println(1L to 10L)

    def x() = {
        import scala.Predef.longWrapper
        println(1L to 10L)

        def y() = {
            import Time.longWrapper
            println(1L to 10L)
        }
        y()
    }
    x()
}
```
```
[info] Running Test
NumericRange(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
TimeRange(1,10)
NumericRange(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
TimeRange(1,10)
```
- 第一個 `1L to 10L`，使用 `Predef` 的 `NumericRange`
- 第二個 `1L to 10L`，因為 `import Time._`，使用 `Time` 的 `TimeRange`
- 第三個 `1L to 10L`，因為明確 `import scala.Predef.longWrapper`，使用 `Predef` 的 `NumericRange`
- 第四個 `1L to 10L`，因為明確 `import Time.longWrapper`，使用 `Time` 的 `TimeRange`

為避免這類的衝突，最好的方式是避免跨 implicit view 的衝突，但有時候很難辦到。這種情況下，最好只有一個轉換使用隱喻，其他用明確的方式。

把隱喻標記為找得到 (discoverable)，有助程式可讀性。

在 Scala 社群，一般實踐中在兩個地方限制匯入隱喻

- Package objects
- Singleton objects that have the postfix Implicits


### 5.4.2 免稅隱喻 (Implicits without the import tax)

## 5.5 結論 (Summary)

本章討論隱喻查找機制。Scala 提供兩種隱喻

- implicit value: 提供方法參數
- implicit view: 呼叫方法時進行型別轉換

這兩種隱喻使用同樣的隱喻解析機制，分成兩階段

- 第一階段在目前範圍尋找沒有前綴的隱喻 
- 第二階段在相關類型的伴生物件內找尋隱喻

隱喻很強大，但要小心使用。限制隱喻範圍，把隱喻定義在眾所皆知或容易找到的位置，將是成功使用隱喻的關鍵。
