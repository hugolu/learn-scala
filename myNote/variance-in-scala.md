以下內容翻譯文章 [Variance in scala](http://like-a-boss.net/2012/09/17/variance-in-scala.html)
- 專有名詞除第一次出現時給予中文翻譯，其餘文章皆沿用原文，避免歧義。

___
Variance ([wikipedia](http://en.wikipedia.org/wiki/Variance_%28computer_science%29), [scala-lang](http://www.scala-lang.org/node/129)) 乍看瑣碎，卻是保證型別安全的有用工具。我花了一點時間弄懂它隱含的意思，做以下總結以及它能做什麼。

# Variance (變型)

在 Scala 中，類別的通用化參數可額外加註 variance 註記。那些註記進一步約束宣告的類別如何使用。

Variance 有點像 [Liskov Substitution Principle](http://en.wikipedia.org/wiki/Liskov_substitution_principle)。LSP 描述子類能夠替換其超類被使用。

Variance 施加額外限制讓我們從子型別 (sub-typing) 與超型別 (super-typing) 的角度使用通用類別。

## Covariance (協變)

在 Scala 中有個 covariant 類別的範例，```Vector```。

觀察這個類別階層：

```scala
class Animal
class Dog extends Animal
```

Covariance 定義以下關係：如果 ```A``` 是 ```B``` 的子型別，那麼 ```Vector[A]``` 是 ```Vector[B]``` 的子型別。具體來說，```Vector[Dog]``` 是 ```Vector[Animal]``` 的子型別。

由另一的角度來看，如果 ```Vector``` 是協變的，必能使用 ```Vector[Dog]``` 在任何地方取代會使用 ```Vector[Animal]```，因為 ```Dog``` 是一種 ```Animal```。Covariance 意味窄型別與寬型別之間的轉變 - 你可以把 ```Vector[Dog]``` 當成 ```Vector[Animal]```。


```scala
// we can assign a Vector of Animals to a Vector of Animals 
scala> val aCovariantVector: Vector[Animal] = Vector.empty[Animal]
aCovariantVector: Vector[Animal] = Vector()

// We can also asign a Vector of Dogs to a Vector of Animals
scala> val aCovariantVector: Vector[Animal] = Vector.empty[Dog]
aCovariantVector: Vector[Animal] = Vector()

// Because Animal is the wider type this isn't possible.
scala> val aCovariantVector: Vector[Dog] = Vector.empty[Animal]
<console>:9: error: type mismatch;
 found   : scala.collection.immutable.Vector[Animal]
 required: Vector[Dog]
       val aCovariantVector: Vector[Dog] = Vector.empty[Animal]
                                                       ^
```

## Invariance (不變)

如果類別參數是不變的 (invariant)，意味著類別沒有寬、窄型別間的轉換。

invariance 最一般的用法是集合 (collection)。```Array``` 是 invariant 類別的例子。

```scala
scala> val anInvariantArray: Array[Animal] = Array.empty[Animal]
anInvariantArray: Array[Animal] = Array()

scala> val anInvariantArray: Array[Animal] = Array.empty[Dog]
<console>:9: error: type mismatch;
 found   : Array[Dog]
 required: Array[Animal]
Note: Dog <: Animal, but class Array is invariant in type T.
You may wish to investigate a wildcard type such as `_ <: Animal`. (SLS 3.2.10)
       val anInvariantArray: Array[Animal] = Array.empty[Dog]
                                                       ^

scala> val anInvariantArray: Array[Dog] = Array.empty[Animal]
<console>:9: error: type mismatch;
 found   : Array[Animal]
 required: Array[Dog]
Note: Animal >: Dog, but class Array is invariant in type T.
You may wish to investigate a wildcard type such as `_ >: Dog`. (SLS 3.2.10)
       val anInvariantArray: Array[Dog] = Array.empty[Animal]
```

Invariance 對於集合的型別安全很重要。Java 陣列具有協變性。以下示範為什麼這是個壞主意：

```java
// declare an array of strings
String[] a = new String[1];

// because in java arrays are covariant we should be able to
// use it as if it's an array of objects
Object[] b = a;

// but storing an Integer will rightfully cause an java.lang.ArrayStoreException
b[0] = 1;
```

因為陣列的使用者可能會認為他們在處理```Object[]```而不是```String[]```。如果 Java 允許把 ```Integer``` 儲存到 ```String``` 陣列，這會搞爛網站 - 讀取者以為自己在處理 ```String``` 陣列而不是 ```Object``` 陣列，然後嘗試從陣列中讀出字串。

## Contravariance (逆變)

某方面來說，contravariance 算是 convariance 的反面。在 scala 中，逆變類別的例子是 ```Function1[-T1, +R]```。為什麼 ```Function1``` 的輸入參數需要是逆變？

用協變的角度思考一下。先前討論過，因為```Dog```是```Animal```的子類別，協變意味著```Vector[Dog]```到```Vector[Animal]```間存在轉變。那麼相似的轉變在```Function1```說得過去嗎？

假設```Function1[Dog, Any]```成立，一般來說，這個函式應該對```Dog```與其子類別都適用。但不必然對```Animal```適用，因為這類別可能使用只有子類才具備的方法。

然而，逆向轉變可行 - 如果一個函式能作用在```Animal```上，那麼設計上，這個函式也應該能作用在```Dog```上。

逆變意味如果```B```是```A```的超型別，則```Function1[A, R]```是```Function1[B, R]```的超型別。具體來說，```Function1[Dog, Any]```會是```Function1[Animal, Any]```的超型別。

```scala
class Contravariant[-A] 

// this all works as expected:
scala> val contravariantClass: Contravariant[Animal] = new Contravariant[Animal]
contravariantClass: Contravariant[Animal] = Contravariant@632b6836

scala> val contravariantClass: Contravariant[Dog] = new Contravariant[Animal]
contravariantClass: Contravariant[Dog] = Contravariant@6dce272d

// because Dog is a subtype of Animal not the other way around we get an error
scala> val contravariantClass: Contravariant[Animal] = new Contravariant[Dog]
<console>:10: error: type mismatch;
 found   : Contravariant[Dog]
 required: Contravariant[Animal]
       val contravariantClass: Contravariant[Animal] = new Contravariant[Dog]
                                                    ^
```

# Variance and type safety (變型與型別安全)

使用```var```欄位定義通用類別，會發生編譯錯誤：

```scala
scala> class Invariant[T](var t: T)
defined class Invariant

scala> class Covariant[+T](var t: T)
<console>:7: error: covariant type T occurs in contravariant position in type T of value t_=
       class Covariant[+T](var t: T)
             ^

scala> class Contravariant[-T](var t: T)
<console>:7: error: contravariant type T occurs in covariant position in type => T of method t
       class Contravariant[-T](var t: T)
```

來拆解一下。為什麼編譯器不允許協變類別中有讀取器(getter)？

```scala
scala> abstract trait Covariant[+T] {
     |   def take(t: T): Unit
     | }
<console>:8: error: covariant type T occurs in contravariant position in type T of value t
         def take(t: T): Unit
                  ^

scala> abstract trait Contravariant[-T] {
     |   def take(t: T): Unit
     | }
defined trait Contravariant
```

為什麼？想想有關協變的用法，假設有個類別：

```scala
class Printer[+T] {
     |    def print(t: T): Unit = ???
     | }
<console>:8: error: covariant type T occurs in contravariant position in type T of value t
          def print(t: T): Unit = ???
```

如果```print```方法可以列印```Dog```，通常它也應該印出```Animal```，這說得過去嗎？或許有時可以，但通常如果我們想通用化```Printer```類別，我們應該使用逆變。編譯器會聰明地幫忙檢查使用的型別。

想想有關第二個例子，回傳一個通用參數：

```scala
scala> class Create[-T] {
     |   def create: T = ???
     | }
<console>:8: error: contravariant type T occurs in covariant position in type => T of method create
         def create: T = ???
```

再一次 - ```Create```用逆變來通用化，這說得過去嗎？如果```Create```回傳```Animal```類別的實例，我們能在每個期望出現```Create[Dog]```的地方使用嗎？Scala編譯器聰明的很，如果我們這麼試，它會在我們面前炸掉。
