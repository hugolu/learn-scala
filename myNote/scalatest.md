# ScalaTest

參考連結
- [ScalaTest quick start](http://www.scalatest.org/quick_start)
- [Scala Testing 環境設置筆記](http://ot-note.logdown.com/posts/244277/scala-tdd-preliminary-environmental-setting-tips)
- Scala Cookbook, 12.6. Pretending that a String Is a File

SUT: FileUtils.scala
```scala
package foo

object FileUtils {
    def getLineUppercased(source: io.Source): List[String] = {
      (for (line <- source.getLines) yield line.toUpperCase).toList
    }
}
```

Test Driver: FileUtilTests.scala
```scala
package foo

import org.scalatest.{FunSuite, BeforeAndAfter}
import scala.io.Source

class FileUtilTests extends FunSuite with BeforeAndAfter {
  var source: Source = _
  after { source.close }

  // assume the file has the string "foo" as its first line
  test("1 - foo.txt") {
    source = Source.fromFile("foo.txt")
    val lines = FileUtils.getLineUppercased(source)
    assert(lines(0) == "FOO")
  }

  test("2 - foo string") {
    source = Source.fromString("foo\n")
    val lines = FileUtils.getLineUppercased(source)
    assert(lines(0) == "FOO")
  }
}
```

foo.txt
```
foo
```

download scalaTest jar file
```shell
$ wget https://oss.sonatype.org/content/groups/public/org/scalatest/scalatest_2.11/2.2.6/scalatest_2.11-2.2.6.jar
$ ls
FileUtilTests.scala      foo.txt
FileUtils.scala          scalatest_2.11-2.2.6.jar
```

compile and run test
```shell
$ scalac FileUtils.scala
$ scalac -cp scalatest_2.11-2.2.6.jar:. FileUtilTests.scala
$ tree
.
├── FileUtilTests.scala
├── FileUtils.scala
├── foo
│   ├── FileUtilTests$$anonfun$1.class
│   ├── FileUtilTests$$anonfun$2.class
│   ├── FileUtilTests$$anonfun$3.class
│   ├── FileUtilTests.class
│   ├── FileUtils$$anonfun$getLineUppercased$1.class
│   ├── FileUtils$.class
│   └── FileUtils.class
└── scalatest_2.11-2.2.6.jar

$ scala -cp scalatest_2.11-2.2.6.jar org.scalatest.run foo.FileUtilTests
Run starting. Expected test count is: 2
FileUtilTests:
- 1 - foo.txt
- 2 - foo string
Run completed in 163 milliseconds.
Total number of tests run: 2
Suites: completed 1, aborted 0
Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
All tests passed.
```
> 記得 `scalac -cp` 要包含當前路徑 `.`，不然 `FileUtilTests` 會找不到 `FileUtils`
