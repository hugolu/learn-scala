# Specs2

官網：https://etorreborre.github.io/specs2/website/SPECS2-3.8/index.html

## 配合 sbt 使用

設定 sbt proejct 目錄
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
libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8" % "test")
scalacOptions in Test ++= Seq("-Yrangepos")
```

編寫測試程式 `src/test/scala/HelloWorldSpec.scala`
```scala
class HelloWorldSpec extends Specification { def is = s2"""

 This is a specification to check the 'Hello world' string

 The 'Hello world' string should
   contain 11 characters                                         $e1
   start with 'Hello'                                            $e2
   end with 'world'                                              $e3
                                                                 """

  def e1 = "Hello world" must have size(11)
  def e2 = "Hello world" must startWith("Hello")
  def e3 = "Hello world" must endWith("world")
}
```

執行測試
```shell
$ sbt test
[info] Set current project to  (in build file:/private/tmp/test/)
[info] Updating {file:/private/tmp/test/}test...
[info] Resolving jline#jline;2.12.1 ...
[info] downloading https://jcenter.bintray.com/org/specs2/specs2-core_2.11/3.8/specs2-core_2.11-3.8.jar ...
[info] 	[SUCCESSFUL ] org.specs2#specs2-core_2.11;3.8!specs2-core_2.11.jar (4170ms)
[info] downloading https://jcenter.bintray.com/org/specs2/specs2-matcher_2.11/3.8/specs2-matcher_2.11-3.8.jar ...
[info] 	[SUCCESSFUL ] org.specs2#specs2-matcher_2.11;3.8!specs2-matcher_2.11.jar (3478ms)
[info] downloading https://jcenter.bintray.com/org/specs2/specs2-common_2.11/3.8/specs2-common_2.11-3.8.jar ...
[info] 	[SUCCESSFUL ] org.specs2#specs2-common_2.11;3.8!specs2-common_2.11.jar (3255ms)
[info] downloading https://jcenter.bintray.com/org/specs2/specs2-codata_2.11/3.8/specs2-codata_2.11-3.8.jar ...
[info] 	[SUCCESSFUL ] org.specs2#specs2-codata_2.11;3.8!specs2-codata_2.11.jar (2471ms)
[info] downloading https://jcenter.bintray.com/org/scalaz/scalaz-core_2.11/7.2.0/scalaz-core_2.11-7.2.0.jar ...
[info] 	[SUCCESSFUL ] org.scalaz#scalaz-core_2.11;7.2.0!scalaz-core_2.11.jar(bundle) (8854ms)
[info] downloading https://jcenter.bintray.com/org/scalaz/scalaz-effect_2.11/7.2.0/scalaz-effect_2.11-7.2.0.jar ...
[info] 	[SUCCESSFUL ] org.scalaz#scalaz-effect_2.11;7.2.0!scalaz-effect_2.11.jar(bundle) (1996ms)
[info] downloading https://jcenter.bintray.com/org/scalaz/scalaz-concurrent_2.11/7.2.0/scalaz-concurrent_2.11-7.2.0.jar ...
[info] 	[SUCCESSFUL ] org.scalaz#scalaz-concurrent_2.11;7.2.0!scalaz-concurrent_2.11.jar(bundle) (1939ms)
[info] downloading https://jcenter.bintray.com/org/scala-lang/modules/scala-xml_2.11/1.0.5/scala-xml_2.11-1.0.5.jar ...
[info] 	[SUCCESSFUL ] org.scala-lang.modules#scala-xml_2.11;1.0.5!scala-xml_2.11.jar(bundle) (1947ms)
[info] Done updating.
[success] Total time: 48 s, completed 2016/5/10 下午 02:51:44
```

## 配合 scalac 使用
