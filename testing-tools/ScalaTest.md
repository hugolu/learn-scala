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

## 選擇測試風格

sbt `testOnly` (or `test-only`) 接收空白分隔的測試列表，例如進入 sbt REPL 後執行
```sbt
> testOnly org.example.MyTest1 org.example.MyTest2
```

也支援萬用字元
```scala
> testOnly org.example.My*
```

### FunSuite
**xUnit**測試風格

For teams coming from xUnit, FunSuite feels comfortable and familiar while still giving some of the benefits of BDD: FunSuite makes it easy to write descriptive test names, natural to write focused tests, and generates specification-like output that can facilitate communication among stakeholders.

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
**BDD**測試風格
A good first step for teams wishing to move from xUnit to BDD, FlatSpec's structure is flat like xUnit, so simple and familiar, but the test names must be written in a specification style: "X should Y," "A must B," etc.

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
For teams coming from Ruby's RSpec tool, FunSpec will feel very familiar; More generally, for any team that prefers BDD, FunSpec's nesting and gentle guide to structuring text (with describe and it) provides an excellent general-purpose choice for writing specification-style tests.

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
For teams coming from specs or specs2, WordSpec will feel familiar, and is often the most natural way to port specsN tests to ScalaTest. WordSpec is very prescriptive in how text must be written, so a good fit for teams who want a high degree of discipline enforced upon their specification text.

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
Because it gives absolute freedom (and no guidance) on how specification text should be written, FreeSpec is a good choice for teams experienced with BDD and able to agree on how to structure the specification text.

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
Spec allows you to define tests as methods, which saves one function literal per test compared to style classes that represent tests as functions. Fewer function literals translates into faster compile times and fewer generated class files, which can help minimize build times. As a result, using Spec can be a good choice in large projects where build times are a concern as well as when generating large numbers of tests programatically via static code generators.

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
PropSpec is perfect for teams that want to write tests exclusively in terms of property checks; also a good choice for writing the occasional test matrix when a different style trait is chosen as the main unit testing style.

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
Trait FeatureSpec is primarily intended for acceptance testing, including facilitating the process of programmers working alongside non-programmers to define the acceptance requirements.

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
