# SBT Build Definition

reference: http://www.scala-sbt.org/0.12.2/docs/Getting-Started/Full-Def.html

## `.scala` source files in the build definition project
```
hello/                # the base directory of project
├── build.sbt         # a shorthand of build definition; it will be merged to project/
└── project/          # the base directory of build definition project
    └── Build.scala   # a source file in billd definition
```

The `project/` directory is *another project* inside *your project* which knows **how to build your project**. The project inside project can (in theory) do anything any other project can do. Your build definition is an sbt project.

Any time files ending in `.scala` or `.sbt` are used, naming them `build.sbt` and `Build.scala` are **conventions only*. This also means that multiple files are allowed.

## Relating `build.sbt` to `Build.scala`

project/Build.scala:
```scala
import sbt._
import Keys._

object HelloBuild extends Build {

  val sampleKeyA = SettingKey[String]("sample-a", "demo key A")
  val sampleKeyB = SettingKey[String]("sample-b", "demo key B")
  val sampleKeyC = SettingKey[String]("sample-c", "demo key C")
  val sampleKeyD = SettingKey[String]("sample-d", "demo key D")

  override lazy val settings = super.settings ++
    Seq(sampleKeyA := "A: in Build.settings in Build.scala", resolvers := Seq())

  lazy val root = Project(id = "hello",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(sampleKeyB := "B: in the root project settings in Build.scala"))
}
```

build.sbt:
```scala
sampleKeyC in ThisBuild := "C: in build.sbt scoped to ThisBuild"
sampleKeyD := "D: in build.sbt"
```

Inspect each key (the detail is listed in the bottom):

| Key | Setting | Provided by | 
|-----|---------|-------------|
| `sample-a` | `A: in Build.settings in Build.scala` | `{file:.../hello/}/*:sampleA` |
| `sample-b` | `B: in the root project settings in Build.scala` | `{{file:.../hello/}hello/*:sampleB` |
| `sample-c` | `C: in build.sbt scoped to ThisBuild` | `{file:.../hello/}/*:sampleC` |
| `sample-d` | `D: in build.sbt` | `{file:.../hello/}hello/*:sampleD` |

___
```
$ sbt "inspect sample-a"
[info] Setting: java.lang.String = A: in Build.settings in Build.scala
[info] Description:
[info] 	demo key A
[info] Provided by:
[info] 	{file:/Users/hugo/workspace.scala/sbt/hello/}/*:sampleA
[info] Defined at:
[info] 	/Users/hugo/workspace.scala/sbt/hello/project/Build.scala:12
[info] Delegates:
[info] 	*:sampleA
[info] 	{.}/*:sampleA
[info] 	*/*:sampleA
[info] Related:
[info] 	{.}/*:sampleA
```

```
$ sbt "inspect sample-b"
[info] Setting: java.lang.String = B: in the root project settings in Build.scala
[info] Description:
[info] 	demo key B
[info] Provided by:
[info] 	{file:/Users/hugo/workspace.scala/sbt/hello/}hello/*:sampleB
[info] Defined at:
[info] 	/Users/hugo/workspace.scala/sbt/hello/project/Build.scala:16
[info] Delegates:
[info] 	*:sampleB
[info] 	{.}/*:sampleB
[info] 	*/*:sampleB
```

```
$ sbt "inspect sample-c"
[info] Setting: java.lang.String = C: in build.sbt scoped to ThisBuild
[info] Description:
[info] 	demo key C
[info] Provided by:
[info] 	{file:/Users/hugo/workspace.scala/sbt/hello/}/*:sampleC
[info] Defined at:
[info] 	/Users/hugo/workspace.scala/sbt/hello/build.sbt:1
[info] Delegates:
[info] 	*:sampleC
[info] 	{.}/*:sampleC
[info] 	*/*:sampleC
[info] Related:
[info] 	{.}/*:sampleC
```

```
$ sbt "inspect sample-d"
[info] Setting: java.lang.String = D: in build.sbt
[info] Description:
[info] 	demo key D
[info] Provided by:
[info] 	{file:/Users/hugo/workspace.scala/sbt/hello/}hello/*:sampleD
[info] Defined at:
[info] 	/Users/hugo/workspace.scala/sbt/hello/build.sbt:2
[info] Delegates:
[info] 	*:sampleD
[info] 	{.}/*:sampleD
[info] 	*/*:sampleD
```
