# Making our own typeclass with simulacrum

來源：http://eed3si9n.com/herding-cats/making-our-own-typeclass-with-simulacrum.html

The conventional steps of defining a modular typeclass in Scala used to look like:

定義模組化 typeclass 的習慣步驟看起來像：
1. 定義 typeclass trait `Foo`
2. 定義伴隨物件，包含輔助方法 `apply` 作用像是 `implicitly`，與定義 `Foo` 實例
3. 定義 `FooOps` 類別，定義一元或二元操作子
4. 定義 `FooSyntax` trait，從 `Foo` 實例隱喻提供 `FooOps`
