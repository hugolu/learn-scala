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
def pair[T, U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = for {
	x <- t
	y <- u
} yield (x, y)
```
```scala
val pairs = pair(integers, integers)

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
- `val booleans`: `integers` 透過 `map` 方式轉換成布林**產生器**
- `def pairs`: 接收兩個 `integers`，回傳整數對產生器，是個用來創造產生器的**函數**

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

傑克，這真是太神奇了！趕快來推導一下

### 布林產生器

原本 for-expression 版本
```scala
val booleans = for (x <- integers) yield x > 0
```

根據 `map` 轉換規則
- `for(x <- e1) yield e2` ≡ `e2 map { x => e1 }`
```scala
val booleans = integers map { x => x > 0 }
```

根據 `map` 方法定義
- `def map[S](f: T => S) = new Generator[S] { def generate = f(self.generate) }`, where
- `T => S` ≡ `T => U`
- `f = { x => x > 0 }`
```scala
val booleans = new Generator[Boolean] {
  def generate = (x: Int => x > 0)(integers.generate)
}
```

根據 `x` ≡ `integers.generate`
```scala
val booleans = new Generator[Boolean] {
  def generate = integers.generate > 0
}
```

### 整數對產生器

原本 for-expression 版本
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = for {
  x <- t
  y <- u
} yield (x, y)
```

根據 `flatMap` & `map` 轉換規則
- `for (x <- e1; y <- e2; s) yield e3` ≡ `e1.flatMap { x => for (y <- e2; s) yield e3 }`
- `for (x <- e1) yield e2` ≡ `e2 map { x => e1 }`
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
  x => u map { y => (x, y) }
}
```

根據 `map` 方法定義
- `def map[S](f: T => S) = new Generator[S] { def generate = f(self.generate) }`, where
- `T => S` ≡ `U => (U, T)`
- `f = { y => (x, y) }`
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
  x => new Generator[(U, T)] { def generate = (x: T, y: U)(u.generate) }
}
```
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
  x => new Generator[(U, T)] { def generate = (x, u.generate) }
}
```

根據 `flatMap` 方法定義
- `def flatMap[S](f: T => Generator[S])= new Generator[S] { def generate = f(self.generate).generate }`, where
- `T => Generator[S]` ≡ `T => Generator[(T, U])`
- `f = { x => new Generator[(U, T)] { def generate = (x, u.generate) }`
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
  def generate = (new Generator[(T, U)] { def generate = (t.generate, u.generate) }).generate
}
```

`(new Generator[(T, U)]).generate` 產生 `(t.generate, u.generate)`
```scala
def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
  def generate = (t.generate, u.generate)
}
```

### 其他產生器範例

```scala
def single[T](x: T): Generator[T] = new Generator[T] { def generate = x }

val singles = single("hello")
singles.generate                                //> res9: String#242 = hello
singles.generate                                //> res10: String#242 = hello
singles.generate                                //> res11: String#242 = hello
```

```scala
val positives = integers map { x => if (x > 0) x else -x }

positives.generate                              //> res12: Int#1107 = 395292324
positives.generate                              //> res13: Int#1107 = 1109994896
positives.generate                              //> res14: Int#1107 = 225955989
```

```scala
def choose(lo: Int, hi: Int): Generator[Int] = for (x <- positives) yield lo + x % (hi - lo)

val chooses = choose(10, 20)
chooses.generate                                //> res15: Int#1107 = 15
chooses.generate                                //> res16: Int#1107 = 15
chooses.generate                                //> res17: Int#1107 = 14
```

```scala
def oneOf[T](xs: T*): Generator[T] = for (idx <- choose(0, xs.length)) yield xs(idx)

val fruits = oneOf("apple", "banana", "cherry")
fruits.generate                                 //> res18: String#242 = banana
fruits.generate                                 //> res19: String#242 = banana
fruits.generate                                 //> res20: String#242 = cherry
```

### List Generator

思考方向：根據 `booleans.generate` 的結果，一個 list 可能是空的 (`Nil`)，也可能是有值 (`head :: tail`) 後面再接另一個 list，重複迭代下去。

```scala
def lists: Generator[List[Int]] = for {
  isEmpty <- booleans
  list <- if (isEmpty) emptyLists else nonEmptyLists
} yield list

def emptyLists = single(Nil)

def nonEmptyLists = for {
  head <- integers
  tail <- lists
} yield head :: tail

lists.generate                                  //> res21: List#545717[Int#1107] = List()
lists.generate                                  //> res22: List#545717[Int#1107] = List(-1502195284)
lists.generate                                  //> res23: List#545717[Int#1107] = List(136304174, 823170112, -908792429)
```

### Tree Generator

思考方向：根據 `booleans.generate` 的結果，一個 tree 可能是 `Leaf` (包含一個 Int)，也可能是 `Inner` 左右兩邊再接 tree，重複迭代下去。

```scala
trait Tree
case class Inner(leaf: Tree, right: Tree) extends Tree
case class Leaf(x: Int) extends Tree

def trees: Generator[Tree] = for {
	isLeaf <- booleans
	tree <- if (isLeaf) leafs else inners
} yield tree

def leafs: Generator[Leaf] = for {
	x <- integers
} yield Leaf(x)

def inners: Generator[Inner] = for {
	l <- trees
	r <- trees
} yield Inner(l, r)

trees.generate                            //> res24: myTest#27.test24#2438936.Tree#5927749 = Inner(Leaf(1493212505),Inner(Leaf(-1746873448),Leaf(2124703850)))
trees.generate                            //> res25: myTest#27.test24#2438936.Tree#5927749 = Leaf(1392404615)
trees.generate                            //> res26: myTest#27.test24#2438936.Tree#5927749 = Inner(Leaf(1227358752),Inner(Inner(Inner(Inner(Inner(Inner(Leaf(-661742618),Leaf(1796993099)),Inner(Inner(Inner(Inner(Inner(Leaf(545580286),Leaf(-1960481325)),Inner(Leaf(-781903228),Leaf(1213806331))),Leaf(-2119345189)),Leaf(-993956323)),Inner(Leaf(-842508617),Leaf(46759649)))),Leaf(-401949709)),Leaf(862711250)),Leaf(-1228855276)),Inner(Inner(Inner(Leaf(-1092150647),Inner(Inner(Inner(Inner(Leaf(-445515240),Leaf(-796674427)),Leaf(137369267)),Inner(Leaf(-820157410),Inner(Leaf(-355070272),Leaf(1175353547)))),Leaf(-584813611))),Leaf(-1312457555)),Inner(Inner(Leaf(-1344348981),Leaf(1026943691)),Leaf(1908757408)))))
```

### Application: Random Testing

所謂 unit test
- 某些輸入待測函數的測試數據，與後置條件 (postcondition)
- 後置條件是期望結果的特性
- 檢查待測函數是否遵守後置條件

問題：能不能用隨機輸入取代測試輸入

```scala
def test[T](g: Generator[T], numTimes: Int = 100)(test: T => Boolean): Unit = {
  for (i <- 0 until numTimes) {
    val value = g.generate
    assert(test(value), "test failed for " + value)
  }
  println("passed " + numTimes + " tests")
}
```

以下這個測試方式對嗎？
```scala
test(pair(lists, lists)) {
  case (xs, ys) => (xs ++ ys).length > xs.length
}
```

不對，因為當 list 為 Nil 時長度為 0，postcondition 不成立
```scala
test(pair(lists, lists)) {
  case (xs, ys) => (xs ++ ys).length > xs.length
}                                               //> java.lang.AssertionError: assertion failed: test failed for (List(315471556
                                                //| ),List())
                                                //| 	at scala.Predef$.assert(Predef.scala:165)
                                                //| 	at myTest.test24$$anonfun$main$1$$anonfun$test$1$1.apply$mcVI$sp(myTest.
                                                //| test24.scala:110)
```

<<< 未完待續 >>>
