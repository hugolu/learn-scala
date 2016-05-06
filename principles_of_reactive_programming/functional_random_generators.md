# Functional Random Generators

For-expression 可以用在 set, database, options，那所有集合都能用嗎？不能，因為相關動作會被 Scal 翻譯成 `map`、`flatMap`、`withFilter`，除非把這方法些實作出來。

除了 collection 還有很多領域 (domain) 提供這類的翻譯，例如接下來要研究的 random value generators。

## Random Values

```scala
import java.util.Random

val rand = new Random
rand.nextInt()
```

這是隨機整數的產生器，有沒有系統性的方法產生其他領域的變數產生器呢？例如：Boolean, String, Pair/Tuple, List, Set, Tree...

### Generators

先定義產生器的特徵
```scala
trait Generator[+T] {
  def generate: T
}
```

#### 整數產生器
```scala
val integers = new Generator[Int] {
  val rand = new java.util.Random
  def generate = rand.nextInt()
}
```
```scala
integers.generate                               //> res0: Int#1107 = 1595486028
integers.generate                               //> res1: Int#1107 = 1829414112
integers.generate                               //> res2: Int#1107 = -249564254
```

#### 布林產生器
```scala
val booleans = new Generator[Boolean] {
  val rand = new java.util.Random
  def generate = (rand.nextInt() > 0)
}
```
```scala
booleans.generate                               //> res3: Boolean#2529 = true
booleans.generate                               //> res4: Boolean#2529 = false
booleans.generate                               //> res5: Boolean#2529 = false
```

#### 整數對產生器
```scala
val pairs = new Generator[(Int, Int)] {
  val rand = new java.util.Random
  def generate = (integers.generate, integers.generate)
}
```
```scala
pairs.generate                                  //> res6: (Int#1107, Int#1107) = (1929895468,-1861069052)
pairs.generate                                  //> res7: (Int#1107, Int#1107) = (67910442,-323151241)
pairs.generate                                  //> res8: (Int#1107, Int#1107) = (-42523306,-811035502)
```

## Streamlining It
有可能不用 `new Generator` 這樣套版嗎？理論上可以這樣寫
```scala
val booleans = for (x <- integers) yield x > 0

def pairs[T, U](t: Generator[T], u: Generator[U]) = for {
  x <- t
  y <- u
} yield (x, y)
```

會被翻譯成
```scala
val booleans = integers map (x => x > 0)

def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap(x => u map (y => (x, y)))
```

所以需要提供 `map` 與 `flatMap` 方法！

## Generators with `map` and `flatMap`

```scala
trait Generator[+T] {
  self => // an alias for "this"

  def generate: T

  def map[S](f: T => S): Generator[S] = new Generator[S] {
    def generate = f(self.generate)
  }

  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    def generate = f(self.generate).generate
  }
}
```

`flatMap` 比較有趣，它產生一個新的 `Generator`。這個新的產生器的 `generate` 方法是拿原本 `generate` 產生的值 (型別`T`) 透過 `f` 函數產生另一個 `Generator` 然後呼叫這個 `Generator` 的 `generate` 方法，產生的值型別為 `S`。

驗證一下
```scala
val booleans = integers map { x => x > 0 }

booleans.generate                               //> res3: Boolean#2529 = true
booleans.generate                               //> res4: Boolean#2529 = true
booleans.generate                               //> res5: Boolean#2529 = true
```
```scala
val pairs = integers flatMap { x => integers map { y => (x, y) } }

pairs.generate                                  //> res6: (Int#1107, Int#1107) = (-2057760815,-1627130508)
pairs.generate                                  //> res7: (Int#1107, Int#1107) = (769104506,-1841858564)
pairs.generate                                  //> res8: (Int#1107, Int#1107) = (-1553100971,590271155)
```
