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

src/test/scala/flatspec.scala:
```scala
import org.scalatest.FunSuite

class SetSuite extends FunSuite {
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

### FlatSpec
**BDD**測試風格
A good first step for teams wishing to move from xUnit to BDD, FlatSpec's structure is flat like xUnit, so simple and familiar, but the test names must be written in a specification style: "X should Y," "A must B," etc.

src/test/scala/flatspec.scala:
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

### FunSpec
For teams coming from Ruby's RSpec tool, FunSpec will feel very familiar; More generally, for any team that prefers BDD, FunSpec's nesting and gentle guide to structuring text (with describe and it) provides an excellent general-purpose choice for writing specification-style tests.

src/test/scala/funspec.scala
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

### WordSpec

### FreeSpec

### Spec

### PropSpec

### FeatureSpec
