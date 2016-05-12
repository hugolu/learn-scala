# ScalaTest

官網：http://www.scalatest.org/

## 開始
### 配合 sbt 使用

設定 sbt project 目錄
```shell
$ mkdir test
$ cd test
$ mkdir4sbt.sh
```
> mkdir4sbt.sh 請參考 https://gist.github.com/hugolu/a2391f57f82774837e42142df341504e

設定 build.sbt
```
name := ""
version := ""
scalaVersion := "2.11.7"
libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```

編寫測試程式 `src/test/scala/ExampleSpec.scala`
```scalc
import collection.mutable.Stack
import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}
```

執行測試
```shell
$ sbt test
[info] Set current project to  (in build file:/private/tmp/test/)
[info] Updating {file:/private/tmp/test/}test...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /private/tmp/test/target/scala-2.11/test-classes...
[info] ExampleSpec:
[info] A Stack
[info] - should pop values in last-in-first-out order
[info] - should throw NoSuchElementException if an empty stack is popped
[info] Run completed in 377 milliseconds.
[info] Total number of tests run: 2
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 8 s, completed 2016/5/10 下午 02:39:18
```

sbt `testOnly` (or `test-only`) 接收空白分隔的測試列表，例如進入 sbt REPL 後執行
```sbt
> testOnly org.example.MyTest1 org.example.MyTest2
```

也支援萬用字元
```scala
> testOnly org.example.My*
```

### 配合 scalac 使用

建立測試目錄
```shell
$ mkdir test
$ cd test
```

下載 ScalaTest jar 檔
```shell
$ wget https://oss.sonatype.org/content/groups/public/org/scalatest/scalatest_2.11/2.2.6/scalatest_2.11-2.2.6.jar
```

編譯待測程式
```shell
$ scalac -cp scalatest_2.11-2.2.6.jar ExampleSpec.scala
```

執行測試
```shell
$ scala -cp scalatest_2.11-2.2.6.jar org.scalatest.run ExampleSpec
Run starting. Expected test count is: 2
ExampleSpec:
A Stack
- should pop values in last-in-first-out order
- should throw NoSuchElementException if an empty stack is popped
Run completed in 173 milliseconds.
Total number of tests run: 2
Suites: completed 1, aborted 0
Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
All tests passed.
```

使用 ScalaTest 測試專案的三個步驟：

1. [選擇測試風格](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#選擇測試風格)
2. [為專案定義基礎類別](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#為專案定義基礎類別)
3. [開始第一個測試](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#開始第一個測試)

## 選擇測試風格

- [FunSuite](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#funsuite)
- [FlatSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#flatspec)
- [FunSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#funspec)
- [WordSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#wordspec)
- [FreeSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#freespec)
- [Spec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#spec)
- [PropSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#propspec)
- [FeatureSpec](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#featurespec)

### FunSuite

對於來自 xUnit 的團隊來說，[FunSuite](http://doc.scalatest.org/2.2.6/#org.scalatest.FunSuite) 讓人感到舒適與熟悉，此外也提供 BDD 的好處。FunSuite 易於描述測試名稱、寫出聚焦的測試、產生像是規格的輸出利於與人溝通。

```scala
import org.scalatest.FunSuite

class SetFunSuite extends FunSuite {
  test("An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }

  test("Invoking head on an empty Set should produce NoSuchElementException") {
    intercept[NoSuchElementException] {
      Set.empty.head
    }
  }
}
```
```
[info] SetFunSuite:
[info] - An empty Set should have size 0
[info] - Invoking head on an empty Set should produce NoSuchElementException
```

### FlatSpec

適合想從 xUnit 轉換到 BDD 的團隊嚐鮮，[FlatSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.FlatSpec) 平面化結構像 xUnit，所以用來簡單、熟悉，但測試名字必須寫成規格的形式："X should Y", "A must B" 等等。

```scala
import org.scalatest.FlatSpec

class SetFlatSpec extends FlatSpec {
  "An empty Set" should "have size 0" in {
    assert(Set.empty.size == 0)
  }

  it should "produce NoSuchElementException when head is invoked" in {
    intercept[NoSuchElementException] {
      Set.empty.head
    }
  }
}
```
```
[info] SetFlatSpec:
[info] An empty Set
[info] - should have size 0
[info] - should produce NoSuchElementException when head is invoked
```

### FunSpec

針對來自 Ruby RSpec 工具的團隊，[FunSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.FunSpec) 看來很熟悉。普遍來說，對於偏好 BDD 的團隊想書寫出規格化風格的測試，FunSpec 巢狀形式與結構化文本的引導(使用 `describe`與`it`)提供了通用的選擇。

```scala
import org.scalatest.FunSpec

class SetFunSpec extends FunSpec {
  describe("A Set") {
    describe("when empty") {
      it("should have size 0") {
        assert(Set.empty.size == 0)
      }

      it("should produce NoSuchElementException when head is invoked") {
        intercept[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }
}
```
```
[info] SetFunSpec:
[info] A Set
[info]   when empty
[info]   - should have size 0
[info]   - should produce NoSuchElementException when head is invoked
```

### WordSpec

對於來自 specs 或 specs2 的團隊，會對 [WordSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.WordSpec) 有熟悉感，通常也是把 specsN 測試移植到 ScalaTest 最自然的方法。WordSpec 對於測試的書寫規範很嚴謹，適合高度紀律的團隊。

```scala
import org.scalatest.WordSpec

class SetWordSpec extends WordSpec {
  "A Set" when {
    "empty" should {
      "have size 0" in {
        assert(Set.empty.size == 0)
      }
      "produce NoSuchElementException when head is invoked" in {
        intercept[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }
}
```
```
[info] SetWordSpec:
[info] A Set
[info]   when empty
[info]   - should have size 0
[info]   - should produce NoSuchElementException when head is invoked
```

### FreeSpec

給予絕對的自由度，不引導規格文本如何書寫，[FreeSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.FreeSpec) 適合對 BDD 有經驗的團隊，有共識如何建構規格化測試。

```scala
import org.scalatest.FreeSpec

class SetFreeSpec extends FreeSpec {

  "A Set" - {
    "when empty" - {
      "should have size 0" in {
        assert(Set.empty.size == 0)
      }

      "should produce NoSuchElementException when head is invoked" in {
        intercept[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }
}
```
```
[info] SetFreeSpec:
[info] A Set
[info]   when empty
[info]   - should have size 0
[info]   - should produce NoSuchElementException when head is invoked
```

### Spec

[Spec](http://doc.scalatest.org/2.2.6/#org.scalatest.Spec) 允許你把測試定義成方法，相較於風格類別把測試當成函數，Spec 每個測試省下一個函數字面文字 (function literal)。較少的 function literal 翻譯，較快的編譯時間，產生較少的類別檔案，有助於縮短建構 (build) 時間。因此，當建構時間是考量重點、透過靜態程式碼產生器產生大量測試，針對大型專案使用 Spec 是個好的選擇。

```scala
import org.scalatest.Spec

class SetSpec extends Spec {
  object `A Set` {
    object `when empty` {
      def `should have size 0` {
        assert(Set.empty.size == 0)
      }

      def `should produce NoSuchElementException when head is invoked` {
        intercept[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }
}
```
```
[info] SetSpec:
[info] A Set
[info]   when empty
[info]   - should have size 0
[info]   - should produce NoSuchElementException when head is invoked
```

### PropSpec

[ProcSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.PropSpec) 非常適合想用屬性檢查 (property check) 寫出各種組合測試的團隊；當不同風格特徵 (style trait) 被選為主單元測試風格時，這也是用來寫出測試矩陣的好選擇。

```scala
import org.scalatest._
import prop._
import scala.collection.immutable._

class SetPropSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  val examples =
    Table(
      "Set",
      BitSet.empty,
      HashSet.empty[Int],
      TreeSet.empty[Int]
    )

  property("an empty Set should have size 0") {
    forAll(examples) { set =>
      set.size should be (0)
    }
  }

  property("invoking head on an empty set should produce NoSuchElementException") {
    forAll(examples) { set =>
      a [NoSuchElementException] should be thrownBy { set.head }
    }
  }
}
```
```
[info] SetPropSpec:
[info] - an empty Set should have size 0
[info] - invoking head on an empty set should produce NoSuchElementException
```

### FeatureSpec

[FeatureSpec](http://doc.scalatest.org/2.2.6/#org.scalatest.FeatureSpec) 主要針對驗收測試 (acceptance testing)，包含促進程式設計師與非程式設計人員一起定義驗收需求。

```scala
import org.scalatest._

class TVSet {
  private var on: Boolean = false
  def isOn: Boolean = on
  def pressPowerButton() = { on = !on }
}

class TVFeatureSpec extends FeatureSpec with GivenWhenThen {
  info("As a TV set owner")
  info("I want to be able to turn the TV on and off")
  info("So I can watch TW when I want")
  info("And save energy when I'm not watching TV")

  feature("TV power button") {
    scenario("User presses power button when TV is off") {

      Given("a TV set that is switched off")
      val tv = new TVSet
      assert(!tv.isOn)

      When("the power button is pressed")
      tv.pressPowerButton()

      Then("the TV should switch on")
      assert(tv.isOn)
    }

    scenario("User presses power button when TV is on") {

      Given("a TV set that is switched on")
      val tv = new TVSet
      tv.pressPowerButton()
      assert(tv.isOn)

      When("the power button is pressed")
      tv.pressPowerButton()

      Then("the TV should switch off")
      assert(!tv.isOn)
    }
  }
}
```
```
[info] TVFeatureSpec:
[info] As a TV set owner
[info] I want to be able to turn the TV on and off
[info] So I can watch TW when I want
[info] And save energy when I'm not watching TV
[info] Feature: TV power button
[info]   Scenario: User presses power button when TV is off
[info]     Given a TV set that is switched off
[info]     When the power button is pressed
[info]     Then the TV should switch on
[info]   Scenario: User presses power button when TV is on
[info]     Given a TV set that is switched on
[info]     When the power button is pressed
[info]     Then the TV should switch off
```

## 為專案定義基礎類別

與其逐一定義單元測試的trait
```scala
package com.mycompany.myproject

import org.scalatest._

class TestMySpec extends FlatSpec with Matchers with OptionValues with Inside with Inspectors {
  // tests here
}
```

不如為測試程式產生一個基礎類別(非trait, 以加速編譯時間)
```scala
package com.mycompany.myproject

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside with Inspectors
```
```scala
package com.mycompany.myproject

import org.scalatest._

class TestMySpec extends UnitSpec {
  // tests here
}
```

大部份專案使用多種基礎型別，每一種專注不同的測試。你可以有一種針對資料庫的整合測試(可能叫做`DbSpec`)，另一種針對actor system的整合測試(可能叫做`ActorSysSpec`)，另一種同時需要資料庫與actor system(可能叫做`DbActorSysSpec`)，等等。一開始，你只需要創建一個單元測試的基礎類別。

## 開始第一個測試

在測試類別中定義測試，這個類別擴充像 `FlatSpec` 的風格類別。
```scala
import org.scalatest.FlatSpec

class FirstSpec extends FlatSpec {
  // tests go here...
}
```

在 `FlatSpec` 中，每個測試由測試句子組成，定義需要的行為與一段測試程式碼。測試句子需要主詞("A Stack")、動詞(`should`, `must`, `can`)、與其他部分。如下面的例子
```scala
"A Stack" should "pop values in last-in-first-out order"
```

相同主題多個測試，可使用`it`參考之前定義的主題。
```scala
it should "throw NoSuchElementException if an empty stack is popped"
```

測試句子後面，把測試的程式碼放在花掛號裡面`{}`
```
import collection.mutable.Stack
import org.scalatest._

class StackSpec extends FlatSpec {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[String]
    intercept[NoSuchElementException] {
      emptyStack.pop()
    }
  }
}
```

接下來看是要用 [sbt](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#配合-sbt-使用) 或是 [scalac/scala](https://github.com/hugolu/learn-scala/blob/master/testing-tools/ScalaTest.md#配合-scalac-使用) 進行測試。
