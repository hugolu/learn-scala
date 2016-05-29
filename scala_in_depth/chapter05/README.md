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

相關程式碼 - [bindings](https://github.com/hugolu/learn-scala/tree/master/scala_in_depth/chapter05/bindings)

externalbindings.scala:
```scala
package test

object x {
  override def toString = "Externally bound x object in package test."
}
```

test.scala:
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
