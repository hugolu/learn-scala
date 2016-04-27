出處：[Monads are Elephants Part 4](http://james-iry.blogspot.tw/2007/11/monads-are-elephants-part-4.html)

# 莫內是頭大象 之四 (Monads are Elephants Part 4)

親身經驗一頭成象前，你不會真的理解他們能長多大。如果 Monad 是頭大象，那麼在這一系列文章中，我只讓你看到像 `List` 跟 `Option` 的小象，但現在該是看看成年大象的時候。作為額外獎勵，這篇文章還會耍點馬戲團魔術。

## 函數編程與 IO (Functional Programming and IO)

函數編程中有個叫做引用透明性 (referential transparency) 的概念。引用透明性意指你能在任何地方、任何時間、使用相同的參數呼叫特定函數，都能得到相同的結果。如你想像，一個具有引用透明性的函數比不具引用透明性的函數更容易使用與除錯。

有個地方引用透明性似乎不可能辦到：IO。多次呼叫相同的控制台行讀取函數 (readLine console) 可能根據使用者早餐吃了什麼而得到任何不一樣的字串。發送網路封包可能成功傳送也可能沒有。

但我們不能只為了達成引用透明性而屏除 IO。一個沒有 IO 的程式只是個讓你 CPU 發熱的複雜方式。

你可能猜測針對這一系列主題 Monad 提供了引用透明性的 IO，但我要從一些簡單的原則努力前進。我會解決讀取與寫入字串到控制台的問題，但相同的問題可以擴展成任意種類的 IO，像是檔案或網路。

當然，你可能不認為引用透明性對於 Scala 非常重要。我不會在此宣揚純函式編程引用透明性的真理。我在這裡聊 Monad，剛好 IO Monad 能清楚說明 Monad 如何工作。

## 杯中世界 (The World In a Cup)

從控制台讀取字串不具引用透行性，因為讀取結果取決於使用者狀態而使用者不是參數之一。檔案讀取函數依賴檔案系統的狀態。網頁讀取函數依賴目標網站的狀態、網際網路、和區域網路。相當的輸出函數有相似的相依性。

透過創建一個名為 `worldState` 的類別並使之同時成為參數與所有 IO 函數的結果，概括一切要討論的東西。

不幸的是，世界很大。我初次嘗試寫 `worldState` 導致編譯器耗光記憶體而崩潰。所以取而代之，我會嘗試寫一些小一點的東西而不是模擬整個宇宙。那裡馬戲團魔術會派上用場。

我耍的花招是只模擬世界的一小部分，而且假裝 `worldState` 知道世界其他的部分。這裏有一些有用的角度

1. 世界狀態在 IO 函式間被改變。
2. 世界狀態就是它的樣子。你不能任意創建一個你想要的新世界 (`val coolWorldState = new WorldState(){ def jamesIsBillionaire = true }`) 
3. 任意瞬間，世界精準地處於一個狀態。

特性三有點狡猾，讓我們先處理特性一、二。

這裏是特性一的概略描述

```scala
//file RTConsole.scala
object RTConsole_v1 {
  def getString(state: WorldState) = (state.nextState, Console.readLine)
  def putString(state: WorldState, s: String) = (state.nextState, Console.print(s))
}
```

`getString` 跟 `putString` 使用定義在 `scala.Console` 的原始函數 (raw primitive function) 。接收一個 `worldState` 並回傳一個包含 `worldState` 與 IO 結果的數組 (tuple)。

這裏顯示如何實作特性二

```scala
//file RTIO.scala
sealed trait WorldState { def nextState: WorldState }

abstract class IOApplication_v1 {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args: Array[String]): Unit = {
    iomain(args, new WorldStateImpl(0))
  }
  def iomain(args: Array[String], startState: WorldState): (WorldState, _)
}
```

`worldState` 是個密封特徵 (sealed trait)；它只能在同一個檔案中被擴充。`IOApplication` 定義私有的類別 (the only implementation privately)，所以沒人能實例化它。`IOApplication` 也定義一個無法被覆載 (override) 的 `main` 函數，並呼叫必須實作在繼承的子類別的 `iomain` 函數。隱藏一切不讓使用 IO 函式庫的程式設計師知道細節。

有了這些，hello world 看起來像

```scala
// file HelloWorld.scala
class HelloWorld_v1 extends IOApplication_v1 {
  import RTConsole_v1._
  def iomain(args: Array[String], startState: WorldState) = putString(startState, "Hello world")
}
```

## 該死的特性三 (That Darn Property 3)

第三個特性說，在任何瞬間世界只能有一個狀態。我解決了一個問題，但還有另一個。

```scala
class Evil_v1 extends IOApplication_v1 {
  import RTConsole_v1._
  def iomain(args: Array[String], startState: WorldState) = {
    val (stateA, a) = getString(startState)
    val (stateB, b) = getString(startState)
    assert(a == b)
    (startState, b)
  }
}
```

在此，我對相同的輸入呼叫兩次 `getString`。如果這程式碼具有引用透明性，那麼結果 `a` 與 `b` 應該相同，但事實並非如此除非使用者輸入兩次相同的東西。問題在於 `startState` 是可見的，在相同的時間點上存在 `stateA` 與 `stateB` 兩種狀態。

## 由內而外 (Inside Out)

作為解決問題的第一步，我打算由內而外實作一切。原本 `iomain` 是個輸入 `worldState` 輸出 `worldState` 的函數，取而代之， `iomain` 傳回這樣的函數，然後由 `main` 來執行。這裏是程式碼。

```scala
//file RTConsole.scala
object RTConsole_v2 {
  def getString = {state: WorldState => (state.nextState, Console.readLine)}
  def putString(s: String) = {state: WorldState => (state.nextState, Console.print(s))}
}
```

`getString` 與 `putString` 不再負責取得與寫入字串 - 而是各自回傳一個新的函數，這函數直到提供 `worldState` 才會被執行。

```scala
//file RTIO.scala
sealed trait WorldState { def nextState:  WorldState }

abstract class IOApplication_v2 {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args: Array[String]): Unit = {
    val ioAction = iomain(args)
    ioAction(new WorldStateImpl(0));
  }
  def iomain(args: Array[String]): WorldState => (WorldState, _)
}
```

`IOApplication` 的 `main` 呼叫 `iomain` 取得將要執行的函數，然後用一個初始的 `worldState` 執行這個函數。除了不再接受 `WorldState` 以外，`HelloWorld` 沒改變太多。

```scala
//file HelloWorld.scala
class HelloWorld_v2 extends IOApplication_v2 {
  import RTConsole_v2._
  def iomain(args: Array[String]) = putString("Hello world")
}
```

乍看之下，`worldState` 不存在於 `HelloWorld`，我們似乎解決了問題。但事實證明，這只是掩人耳目一下而已。

## 歐～ 該死的特性三 (Oh That Darn Property 3)

```scala
class Evil_v2 extends IOApplication_v2 {
  import RTConsole_v2._
  def iomain(args: Array[String]) = { startState: WorldState =>
    val (statea, a) = getString(startState)
    val (stateb, b) = getString(startState)
    assert(a == b)
    (startState, b)
  }
}
```

產生 `iomain` 那種故意找碴的函數實在太邪惡了。只要程式設計師能夠創建任意 IO 函數就能看見 `WorldState` 正在運行。

## 特性三壓扁好了 (Property 3 Squashed For Good)

我們要避免程式設計師用對的簽名創建任意函數。嗯... 現在我們需要做什麼？

好的，如所見使用 `WorldState` 能輕易避免程式設計師產生子類別。所以讓我們把函數簽名改成特徵 (trait)。
> 簽名改成特徵: `WorldState => (WorldState, _)` ⇒ `Function1[WorldState, (WorldState, A)]`

```scala
sealed trait IOAction[+A] extends Function1[WorldState, (WorldState, A)]
private class SimpleAction[+A](expression: => A) extends IOAction[A]...
```

不像 `WorldState` 我們需要創建 `IOAction` 實例。例如，`getString` 與 `putString` 放在單獨的文件但他們需要產生新的 `IOAction`，我們只是要他們安全地做這件事。這有點兩難，除非我們理解 `getString` 與 `putString` 有兩部分：一部分做基本 IO、另一部分接收一個世界狀態傳回下個世界狀態。使用工廠方法也或許有助於程式邏輯整潔。

```scala
//file RTIO.scala
sealed trait IOAction_v3[+A] extendsFunction1[WorldState, (WorldState, A)]

object IOAction_v3 {
  def apply[A](expression: => A): IOAction_v3[A] = new SimpleAction(expression)

  private class SimpleAction [+A](expression: => A) extends IOAction_v3[A] {
    def apply(state: WorldState) = (state.nextState, expression)
  }
}

sealed trait WorldState { def nextState: WorldState }

abstract class IOApplication_v3 {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args: Array[String]): Unit = {
    val ioAction = iomain(args)
    ioAction(new WorldStateImpl(0));
  }
  def iomain(args: Array[String]): IOAction_v3[_]
}
```

`IOAction object` 正是個產生 `SimpleAction` 的好工廠。`SimpleAction` 的建構函數接受一個惰性表達式 (lazy expression) 作為參數，所以使用 `=> A` 的註記方式。在 `SimpleAction` 的 `apply` 方法被呼叫前，那個表達式不會被拿來求值。要呼叫 `SimpleAction` 的 `apply` 方法，必須傳入一個 `WorldState`。回傳的東西是數組 (tuple)，包含新的世界狀態與表達式的結果。

這裏是 IO 方法現在看起來的樣子

```scala
//file RTConsole.scala
object RTConsole_v3 {
  def getString = IOAction_v3(Console.readLine)
  def putString(s: String) = IOAction_v3(Console.print(s))
}
```

最後，我們的 `HelloWorld` 類別一點都沒變

```scala
class HelloWorld_v3 extends IOApplication_v3 {
  import RTConsole_v3._
  def iomain(args: Array[String]) = putString("Hello world")
}
```

稍加思索，現在沒有方法能產生邪惡的 `IOApplication`。程式設計師沒有接觸 `WorldState` 的機會，它被密封起來了。`main` 只傳遞一個 `WorldState` 給 `IOAction` 的 `apply` 方法，我們不能用客製化定義的 `apply` 創建任意 `IOAction` 的子類別。

不幸地，我們碰到了組合的問題。我們不能組合多個 `IOAction`，所以我們不能做像 「你的名字是什麼？」「Bob」「哈囉 Bob」 這麼簡單的事情。

嗯～ `IOAction` 是個用來裝表達式的容器，而 Monad 是容器。`IOAction` 需要被組合，而 Monand 可以組合。或許，只是或許...

## 女士先生，為您介紹神奇的 IO Monad (Ladies and Gentleman I Present the Mighty IO Monad)

`IOAction.apply` 工廠方法接受一個型別 `A` 的表達式，然後傳回一個 `IOAction[A]`。它看起來像 "unit"。但它不是，不過目前來說很接近了。如果我們知道這個 Monad 的 `flatMap` 是什麼，那麼 Monad 法則告訴我們如何用它跟 "unit" 來產生 `map`。但 `flatMap` 會是什麼？函數簽名要看起來像 `def flatMap[B](f: A => IOAction[B]): IOAction[B]`。但它做什麼呢？

要它做的事情是，把一個動作串接到函數，然後回傳一個動作，一旦 (IO Monad) 被啟動兩個動作會依序發生。換句話說，`getString.flatMap {y => putString(y)}` 應該得到一個新的 `IOAction` Monad，一旦 Monad 被啟動，首先啟動 `getString` 動作然後執行 `putString` 回傳的動作。來試試看。

```scala
//file RTIO.scala
sealed abstract class IOAction_v4[+A] extends Function1[WorldState, (WorldState, A)] {
  def map[B](f: A => B): IOAction_v4[B] = flatMap {x => IOAction_v4(f(x))}
  def flatMap[B](f: A => IOAction_v4[B]): IOAction_v4[B]= new ChainedAction(this, f)

  private class ChainedAction[+A, B](action1: IOAction_v4[B], f: B => IOAction_v4[A]) extends IOAction_v4[A] {
    def apply(state1: WorldState) = {
      val (state2, intermediateResult) = action1(state1)
      val action2 = f(intermediateResult)
      action2(state2)
    }
  }
}

object IOAction_v4 {
  def apply[A](expression: => A): IOAction_v4[A] = new SimpleAction(expression)

  private class SimpleAction[+A](expression: => A) extends IOAction_v4[A] {
    def apply(state: WorldState) = (state.nextState, expression)
  }
}

// 其餘照舊
sealed trait WorldState { def nextState: WorldState }

abstract class IOApplication_v4 {
  private class WorldStateImpl(id: BigInt) ...
```

`IOAction` 工廠與 `SimpleAction` 照舊。`IOAction` 類別有 Monad 方法 (譯注：`map` & `flatMap`)。按照 Monad 法則，目前 `map` 只是用 `flatMap` 與 "unit" 來定義。`flatMap` 把全部的苦差事都推到一個叫做 `ChainedAction` 的 `IOAction` 實作上。

`ChainedAction` 的花招在於它 `apply` 的方法。首先用第一個世界狀態呼叫 `action1`。這產生第二個世界狀態與一個中間結果。
`apply` 這函數被串接成需要第一次呼叫的結果，然後返回一個產生下個動作 `action2` 的函數。用第二個世界狀態呼叫 `action2`，最終產生一個數組。記住，直到 `main` 傳入一個初始化的 `WorldState` 物件前，所有一切都不會發生。

## 測試驅動器 (A Test Drive)

某一點上，你可能懷疑為何 `getString` 與 `putString` 不改名叫做 `createGetStringAction`/`createPutStringAction`，因為事實上它們就是做這些事情。要回答這問題，你把他們放到 "for" 表示式中看看會發生什麼事情。

```scala
object HelloWorld_v4 extends IOApplication_v4 {
  import RTConsole_v4._
  def iomain(args: Array[String]) = for {
    _ <- putString("This is an example of the IO monad.")
    _ <- putString("What's your name?")
    name <- getString
    _ <- putString("Hello " + name)
  } yield ()
}
```
似乎用 "for" 與 `getString`/`putString` 產生一個精簡的語言，能用來創建一個複雜的 `IOAction` 。

## 深呼吸一口氣 (Take a Deep Breath)

現在是總結學了什麼的好時機。`IOApplication` 是抽象類別 (pure plumbing)。使用者實作子類別並創建一個叫做 `iomain` 的方法，然後被 `main` 呼叫。回傳值是 `IOAction` - 事實上，它可能是單一動作或是串接在一起的多個動作。在這個 `IOAction` 能真正工作之前，它只是癡癡等待 `WorldState` 物件。`ChainedAction` 類別負責保證沿著每個串連的動作 `WorldState` 依序被改變。

`getString` 與 `putString` 不像他們名字所說，真正去存取字串。相反地，他們產生 `IOAction`。但既然 `IOAction` 是個 Monad，我們可以把它放到 "for" 敘述句中，而結果看起來就像 `getString`/`putString` 真的按照他們所說的那樣做。

這是個好開始，我們快要在 `IOAction` 上得到一個完美的 Monand。這裏有兩個問題。首先，因為 "unit" 會改變世界狀態，有點違背 Monad 規則 (例如 `m flatMap unit ≡ m`)。這情形有點囉唆，因為它是不可見的。但我們也會修正它。

第二個問題是，一般來說，IO 可能會失敗而我們還沒有捕捉這個。

## IO 錯誤 (IO Errors)

以 Monad 術語來說，失敗會用 Zero 表示。
所以我們需要將原本失敗的概念 (exception) 對應到我們的 Monad。
在這一點上，我要走另一條目前為止尚未走過的路：我要用註解方式寫一個函式庫的最終版本。

`IOAction` 物件保留工廠方法與私有實作的方便模組。
`SimpleAction` 維持原樣，`IOAction` 的 `apply` 方法是它們的工廠方法。

```scala
//file RTIO.scala
object IOAction {
  private class SimpleAction[+A](expression: => A) extends IOAction[A] {
    def apply(state: WorldState) = (state.nextState, expression)
  }

  def apply[A](expression: => A): IOAction[A] = new SimpleAction(expression)
```

UnitAction is a class for unit actions - actions that return the specified value but don't change the world state. 
unit is a factory method for it. 
It's kind of odd to make a distinction from SimpleAction, but we might as well get in good monad habits now for monads where it does matter.

`UnitAction` 是單元動作類別 - 動作回傳指定結果卻不改變世界狀態。
`unit` 是它的工廠方法。
要跟 `SimpleAction` 有所區隔有點怪怪的，但現在要培養對 Monad 來說很重要的好習慣。

```scala
  private class UnitAction[+A](value: A) extends IOAction[A] {
    def apply(state:WorldState) = (state, value)
  }
  
  def unit[A](value: A): IOAction[A] = new UnitAction(value)
```

`FailureAction` 是針對 Zero 的類別。
這是一個永遠會丟出例外 (exception) 的 `IOAction`。
`UserException` 是一個可能的例外。
`fail` 與 `ioError` 方法是產生 Zero 的工廠方法。
`fail` 接收一個字串，回傳一個會產生 `UserException` 的動作；而 `ioError` 接收任意例外，回傳一個會丟出例外的動作。

```scala
  private class FailureAction(e: Exception) extends IOAction[Nothing] {
    def apply(state: WorldState) = throw e
  }

  private class UserException(msg: String) extends Exception(msg)

  def fail(msg: String) = ioError(new UserException(msg))
  def ioError[A](e: Exception): IOAction[A] = new FailureAction(e)
}
```

`IOAction` 的 `flatMap` 與 `ChainedAction` 維持原樣。
改變 `map` 真正呼叫 `unit` 方法，以便符合 Monad 法則。
加入兩個方便的方法：`>>` 與 `<<`。
`flatMap` 將這個動作與一個回傳動作的函數串連起來，`>>` 與 `<<` 把這個動作與另一個動作串起來。
這是個得到哪個結果的問題。
`>>` 唸作 "then"，產生一個回傳第二個結果的動作，所以 `putString "What's your name" >> getString` 產生一個顯示提示的動作然後回傳使用者的反應。
相反地，`<<` 唸作 "before"，產生一個回傳第一個動作結果的動作。

```scala
sealed abstract class IOAction[+A] extends Function1[WorldState, (WorldState, A)] {
  def map[B](f: A => B): IOAction[B] = flatMap {x => IOAction.unit(f(x))}
  def flatMap[B](f: A => IOAction[B]): IOAction[B]= new ChainedAction(this, f)

  private class ChainedAction[+A, B](action1: IOAction[B], f: B => IOAction[A]) extends IOAction[A] {
    def apply(state1: WorldState) = {
      val (state2, intermediateResult) = action1(state1)
      val action2 = f(intermediateResult)
      action2(state2)
    }
  }

  def >>[B](next: => IOAction[B]): IOAction[B] =
    for {
      _ <- this;
      second <- next
    } yield second

  def <<[B](next: => IOAction[B]): IOAction[A] =
    for {
      first <- this;
      _ <- next
    } yield first
```

因為現在有了 Zero，只要遵守 Monad 法則就有機會加入 `filter` 方法。
但在此我創建兩個過濾的方法。
一個接收使用者指定的訊息說明為何 `filter` 沒有匹配成功，另一個合乎 Scala 需要的介面並使用一般錯誤的訊息。

```scala
def filter(p: A => Boolean, msg: String): IOAction[A] = flatMap{x => if (p(x)) IOAction.unit(x) else IOAction.fail(msg)}
def filter(p: A => Boolean): IOAction[A] = filter(p, "Filter mismatch")
```

一個 Zero 也意味我們能產生 Monad 加法。
作為產生 Zero 的基礎設施，`HandlingAction` 是種包裹另一個動作的動作，如果動作丟出例外，它把例外送到另一個處理函數。
`onError` 是產生 `HandlingAction` 的工廠方法。
最後，"or" 是 Monad 加法。
基本上是說，如果這個動作因為例外失敗就嘗試另一個動作。

```scala
private class HandlingAction[+A](action: IOAction[A], handler: Exception => IOAction[A]) extends IOAction[A] {
  def apply(state: WorldState) = {
    try {
      action(state)
    } catch {
      case e: Exception => handler(e)(state)
    }
  }
}

def onError[B >: A](handler: Exception => IOAction[B]): IOAction[B] = new HandlingAction(this, handler)
def or[B >: A](alternative: IOAction[B]): IOAction[B] = this onError {ex => alternative}
```

`IOApplication` 最終版本維持一樣

```scala
sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication {
  private class WorldStateImpl(id: BigInt) extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args: Array[String]): Unit = {
    val ioaction = iomain(args)
    ioaction(new WorldStateImpl(0));
  }
  def iomain(args: Array[String]): IOAction[_]
}
```

`RTConsole` 幾乎維持原樣，但我加入 `putLine` 方法好比 `println`。
我也把 `getString` 改為一個 `val`。為什麼？ 因為它總是做一樣的動作。

```scala
//file RTConsole.scala
object RTConsole {
  val getString = IOAction(Console.readLine)
  def putString(s: String) = IOAction(Console.print(s))
  def putLine(s: String) = IOAction(Console.println(s))
}
```

現在 `HelloWorld` 行使一些新功能。
`sayHello` 從字串產生一個動作。
如果字串是一個可辨識的名字，結果會是適當 (或不適當) 的問候。
否則就是一個失敗的動作。

`ask` 是個方便的方法，產生顯示指定字串然後取得字串的動作。
`>>` 確保動作的結果會是 `getString` 的結果。

`processString` 接收任意字串，如果字串是 `quit` 它會產生一個道別與完成的動作；其餘狀況，`sayHello` 會被呼叫。
以防 `sayHello` 失敗，使用 `or` 合併結果與另一個動作。

`loop` 很有趣。
它被定義成 `val`，因為定義成 `def` 也可以作用。
所以作為遞歸函數意義上它不太算是迴圈 (loop)，但它是一個遞歸值，因為它的定義用到 `processString` 而 `processString` 的定義又根據 `loop`。

藉由創建動作 (顯示簡介然後執行迴圈動作指定的事情)，`iomain` 函數啟動一切。

**警告：因為函式庫用迴圈實作，最終會撐爆堆疊 (stack)。不要用在生產程式碼上。閱讀評論看看原因**

```scala
object HelloWorld extends IOApplication {
  import IOAction._
  import RTConsole._

  def sayHello(n: String) = n match {
    case "Bob" => putLine("Hello, Bob")
    case "Chuck" => putLine("Hey, Chuck")
    case "Sarah" => putLine("Helloooo, Sarah")
    case _ => fail("match exception")
  }

  def ask(q: String) = putString(q) >> getString

  def processString(s: String) = s match {
    case "quit" => putLine("Catch ya later")
    case _ => (sayHello(s) or putLine(s + ", I don't know you.")) >> loop
  }

  val loop: IOAction[Unit] =
    for {
      name <- ask("What's your name? ")
      _ <- processString(name)
    } yield ()

  def iomain(args: Array[String]) = {
    putLine("This is an example of the IO monad.") >>
    putLine("Enter a name or 'quit'") >>
    loop
  }
}
```

## 第四部分結論 (Conclusion for Part 4)

在本文我稱 IO Monad 叫做 `IOAction`，為了講清楚 `IOAction` 的實例是等待被執行的動作。
很多人發現 IO Monad 在 Scala 上實用性不高。
沒關係，我不是在這裡宣揚引用透明性。
然而，IO Monad 是最簡單的 Monad 之一，任何意義上顯然不是集合。

儘管如此，IO Monad 的實例可以視為容器。
但是他們不存值而是存放表達式。
`flatMap` 與 `map` 本質上將嵌入式表達式 (embedded expression) 變成更複雜的形式。

或許更有用的心智模型是把 IO Monad 實例視為計算或函數。
`flatMap` 可以看成在計算上套用函數以產生更複雜的計算。

在這一系列的最後我涵蓋了統一容器與計算模型的方法。
通過展示使用 Monand 做一些更複雜的事情，強調 Monad 有多好用。
