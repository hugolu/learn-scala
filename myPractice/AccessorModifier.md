# 屬性存取方法

參考連結: [http://openhome.cc/Gossip/Scala/AccessorModifier.html]

```scala
class Foo {
	private[this] var v: Int = _
	def value: Int = v
	def value_=(v: Int) = { this.v = v }
}

val foo = new Foo                               //> foo  : Foo = $Foo$1@2e7f4425
foo.value                                       //> res0: Int = 0
foo.value = 2
foo.value                                       //> res1: Int = 2
```
- 取 Accessor -`def value: Int`
- 存 Modifier - `def value_=(v: Int)`
