出處：[Monads are Elephants Part 4](http://james-iry.blogspot.tw/2007/11/monads-are-elephants-part-4.html)

# 莫內是頭大象 之四 (Monads are Elephants Part 4)

親身經驗一頭成象前，你不會真的理解他們可以有多大。
如果 Monad 是頭大象，那麼在這一系列文章中，我只讓你看到像 `List` 跟 `Option` 的小象。
現在該是看看成年大象的時候。
作為額外獎勵，這篇文章還會表演些馬戲團魔術。

## 函數編程與 IO (Functional Programming and IO)

函數編程中有個叫做引用透明性 (referential transparency) 的概念。
引用透明性意指你能在任何地方、任何時間、使用相同的參數呼叫特定函數，都能得到相同的結果。
如你想像，一個具有引用透明性的函數比不具引用透明性的函數更容易使用與除錯。

有個地方引用透明性似乎不可能辦到：IO。
多次呼叫相同的控制台行讀取 (readLine console) 函數可能根據使用者早餐吃了什麼而得到任何不一樣的字串。
發送網路封包可能成功傳送也可能沒有。

但我們不能只為了達成引用透明性而屏除 IO。
一個沒有 IO 的程式只是個讓你 CPU 發熱的複雜方式。

你可能猜測針對這一系列主題 Monad 提供了引用透明性的 IO，但我要從一些簡單的原則努力前進。
我會解決讀取與寫入字串到控制台的問題，但相同的問題可以擴展成任意種類的 IO，像是檔案或網路。

當然，你可能不認為引用透明性對於 Scala 非常重要。
我不會在此佈道純函式編程引用透明性的真理。
我在這裡聊 Monad，剛好 IO Monad 能清楚說明 Monad 如何工作。

## 杯中世界 (The World In a Cup)

從控制台讀取字串不具引用透行性，因為讀取結果取決於使用者狀態而使用者不是參數之一。
檔案讀取函數依賴檔案系統的狀態。
網頁讀取函數依賴目標網站的狀態、網際網路、和區域網路。
等效輸出函數有相似的相依性。

全部可以總結到創建一個名為 `worldState` 使之同時成為參數與所有 IO 函數的結果。
不幸的是，世界很大。
我寫 `worldState` 的初次嘗試導致編譯器耗光記憶體而崩潰。
所以取而代之，我將嘗試寫一些小一點的東西而不是模擬整個宇宙
在此耍點馬戲團魔術。

我採用的方法是只去模擬世界的一小部分，而且假裝 `worldState` 知道世界其他的部分。這裏是某些有用的部分

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

`getString` 跟 `putString` 作為原始函數 (raw primitive function) 定義在 `scala.Console` 。
他們接收一個 `worldState` 並回傳一個包含 `worldState` 與 IO 結果的數組 (tuple)。

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

`worldState` 是個密封特徵 (sealed trait)；它只能在同一個檔案中被擴充。
`IOApplication` 定義私有的類別 (the only implementation privately)，所以沒人能實例化它。
`IOApplication` 也定義一個無法被覆載 (override) 的 `main` 函數，並呼叫必須實作在繼承的子類別的 `iomain` 函數。
一切都是為了對使用 IO 函式庫的程式設計師做隱藏。

有了上面這些材料，這裏顯示 hello world 的長相

```scala
// file HelloWorld.scala
class HelloWorld_v1 extends IOApplication_v1 {
  import RTConsole_v1._
  def iomain(args: Array[String], startState: WorldState) = putString(startState, "Hello world")
}
```

## 該死的特性三 (That Darn Property 3)

第三個特性描述，在任何瞬間世界只能有一個狀態。
我解決了一個問題，但還有另一個。

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

在此，我對相同的輸入呼叫兩次 `getString`。
如果這程式碼具有引用透明性，那麼結果 `a` 與 `b` 應該相同，但事實不是如此除非使用者輸入兩次相同的東西。
問題是 `startState` 是可見的，在相同的時間點上有 `stateA` 與 `stateB` 兩個世界的狀態。

## 由裡而外 (Inside Out)

作為解決問題的第一步，我打算由裡而外實作一切。
原本 `iomain` 是個輸入 `worldState` 輸出 `worldState` 的函數，取而代之把 `iomain` 改成傳回函數，然後由 `main` 來執行。
這裏是程式碼。

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

`IOApplication` 的 `main` 呼叫 `iomain` 取得將要執行的函數，然後用一個初始的 `worldState` 執行這個函數。
除了不再接受 `WorldState` 以外，`HelloWorld` 沒改變太多。

```scala
//file HelloWorld.scala
class HelloWorld_v2 extends IOApplication_v2 {
  import RTConsole_v2._
  def iomain(args: Array[String]) = putString("Hello world")
}
```

乍看之下，`worldState` 不存在於 `HelloWorld`，我們似乎解決了問題。
但事實證明，這只是掩人耳目一下而已。

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

產生 `iomain` 那種故意找碴的函數真是太邪惡了。
只要程式設計師能夠創建任意 IO 函數就能看見 `WorldState` 正在運行。

## 特性三壓扁了好 (Property 3 Squashed For Good)

我們要避免程式設計師用對的簽名創建任意函數。
嗯... 現在我們需要做什麼？

好的，如果所見使用 `WorldState` 能輕易避免程式設計師產生子類別。
所以讓我們把函數簽名改成特徵 (trait)。

```scala
sealed trait IOAction[+A] extends Function1[WorldState, (WorldState, A)]
private class SimpleAction[+A](expression: => A) extends IOAction[A]...
```

不像 `WorldState` 我們需要創建 `IOAction` 實例。
例如，`getString` 與 `putString` 放在單獨的文件但他們需要產生新的 `IOAction`。
我們只是要安全的做這件事。
這有點兩難，除非我們理解 `getString` 與 `putString` 做的事情拆成兩塊：一塊做基本 IO、另一塊接收一個世界狀態回傳下一個世界狀態。
用點工廠方法也有助於程式邏輯整潔。

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

`IOAction object` 是個產生 `SimpleAction` 的好工廠。
`SimpleAction` 的建構函數接受一個惰性表達式 (lazy expression) 作為參數，所以使用 `=> A` 的註記方式。
直到 `SimpleAction` 方法被呼叫前，表達式不會被拿來求值。
要呼叫 `SimpleAction` 的 `apply` 方法，必須傳入 `WorldState`。
回傳的東西是數組 (tuple)，包含新的世界狀態與表達式的結果。

這裏是 IO 方法現在看起來的樣子

```scala
//file RTConsole.scala
object RTConsole_v3 {
  def getString = IOAction_v3(Console.readLine)
  def putString(s: String) =
    IOAction_v3(Console.print(s))
}
```

最後，我們的 `HelloWorld` 類別一點都沒變。

```scala
class HelloWorld_v3 extends IOApplication_v3 {
  import RTConsole_v3._
  def iomain(args: Array[String]) = putString("Hello world")
}
```

稍加思考，現在沒有產生邪惡 `IOApplication` 的方式。
程式設計師沒有接觸 `WorldState` 的機會。
全部漏洞都被封起來。
`main` 只傳遞一個 `WorldState` 給 `IOAction` 的 `apply` 方法，我們不能用客製化定義的 `apply` 創建任意 `IOAction` 的子類別。

不幸地，我們得到了個組合的問題。
我們不能組合多個 `IOAction` 所以我們不能做像 「你的名字是什麼？」，「Bob」，「哈囉 Bob」 這麼簡單的事情。

嗯～ `IOAction` 是個用來裝表達式的容器，而 Monad 是容器。
`IOAction` 需要被組合，而 Monand 可以組合。
或許，只是或許...

## 女士先生，為您介紹神奇的 IO Monad (Ladies and Gentleman I Present the Mighty IO Monad)

`IOAction.apply` 工廠方法接受一個型別 `A` 的表達式，然後傳回一個 `IOAction[A]`。
這看起來像 "unit"。
但它不是，不過目前來說很接近了。
如果我們知道這個 Monad 的 `flatMap` 是什麼，那麼 Monad 法則告訴我們如何用它跟 "unit" 來產生 `map`。
但 `flatMap` 會是什麼？
函數簽名要看起來像 `def flatMap[B](f: A => IOAction[B]): IOAction[B]`。
但它要做什麼呢？

要它做的事情是，把一個動作串接到函數，然後回傳一個動作，一旦開始執行兩個動作就會依序發生。
換句話說，`getString.flatMap {y => putString(y)}` 應該得到一個新的 `IOAction` Monad，一旦 Monad 被啟動，首先啟動 `getString` 動作然後執行 `putString` 回傳的動作。
讓它轉一圈看看。

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

`IOAction` 工廠與 `SimpleAction` 照舊。
`IOAction` 類別得到 Monad 方法。
每條 Monad 法則，目前 `map` 只是用 `flatMap` 與 "unit" 來定義。
`flatMap` 把全部苦差事推遲到一個叫做 `ChainedAction` 的 `IOAction` 實作上。

`ChainedAction` 的訣竅在於它 `apply` 的方法。
首先用第一個世界狀態呼叫 `action1`。
這產生第二個世界狀態與一個中間結果。
`apply` 這函數被串接成，需要第一次呼叫的結果，然後返回一個產生下個動作 `action2` 的函數。
用第二個世界狀態呼叫 `action2`，最終產生一個數組。
記住，直到 `main` 傳入一個初始化的 `WorldState` 物件前，所有一切都不會發生。

## 測試驅動器 (A Test Drive)

某一點上，你可能懷疑為何 `getString` 與 `putString` 不改名叫做 `createGetStringAction`/`createPutStringAction`，因為事實上它們就是做這些事情。
有個答案是，如果把他們放到 "for" 表示式中，你看看會發生什麼事情。

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

就像 "for" 與 `getString`/`putString` 一起工作，用精簡的語言產生一個複雜的 `IOAction` 。

## 深呼吸一口氣 (Take a Deep Breath)

Now's a good moment to sum up what we've got. 
IOApplication is pure plumbing. Users subclass it and create a method called iomain which is called by main. 
What comes back is an IOAction - which could in fact be a single action or several actions chained together.
This IOAction is just "waiting" for a WorldState object before it can do its work.
The ChainedAction class is responsible for ensuring that the WorldState is changed and threaded through each chained action in turn.

getString and putString don't actually get or put Strings as their names might indicate.
Instead, they create IOActions.
But, since IOAction is a monad we can stick it into a "for" statement and the result looks as if getString/putString really do what they say the do.

It's a good start; we've almost got a perfectly good monad in IOAction.
We've got two problems.
The first is that, because unit changes the world state we're breaking the monad laws slightly (e.g. m flatMap unit === m).
That's kinda trivial in this case because it's invisible.
But we might as well fix it.

The second problem is that, in general, IO can fail and we haven't captured that just yet.

## IO 錯誤 (IO Errors)

In monadic terms, failure is represented by a zero.
So all we need to do is map the native concept of failure (exceptions) to our monad.
At this point I'm going to take a different tack from what I've been doing so far: I'll write one final version of the library with comments inline as I go.

The IOAction object remains a convenient module to hold several factories and private implementations (which could be anonymous classes, but it's easier to explain with names).
SimpleAction remains the same and IOAction's apply method is a factory for them.

```scala
//file RTIO.scala
object IOAction {
  private class SimpleAction[+A](expression: => A)
      extends IOAction[A] {
    def apply(state:WorldState) =
      (state.nextState, expression)
  }

  def apply[A](expression: => A):IOAction[A] =
    new SimpleAction(expression)
```

UnitAction is a class for unit actions - actions that return the specified value but don't change the world state. 
unit is a factory method for it. 
It's kind of odd to make a distinction from SimpleAction, but we might as well get in good monad habits now for monads where it does matter.

```scala
private class UnitAction[+A](value: A)
    extends IOAction[A] {
  def apply(state:WorldState) =
    (state, value)
}

def unit[A](value:A):IOAction[A] =
  new UnitAction(value)
```

FailureAction is a class for our zeros. 
It's an IOAction that always throws an exception. 
UserException is one such possible exception. The fail and ioError methods are factory methods for creating zeroes. 
Fail takes a string and results in an action that will raise a UserException whereas ioError takes an arbitrary exception and results in an action that will throw that exception.

```scala
private class FailureAction(e:Exception)
      extends IOAction[Nothing] {
    def apply(state:WorldState) = throw e
  }

  private class UserException(msg:String)
    extends Exception(msg)

  def fail(msg:String) =
    ioError(new UserException(msg))
  def ioError[A](e:Exception):IOAction[A] =
    new FailureAction(e)
}
```

IOAction's flatMap, and ChainedAction remain the same.
Map changes to actually call the unit method so that it complies with the monad laws.
I've also added two bits of convenience: >> and <<. 
Where flatMap sequences this action with a function that returns an action, >> and << sequence this action with another action. 
It's just a question of which result you get back.
>>, which can be pronounced "then", creates an action that returns the second result, so 'putString "What's your name" >> getString' creates an action that will display a prompt then return the user's response.
Conversely, <<, which can be called "before" creates an action that will return the result from the first action.

```scala
sealed abstract class IOAction[+A]
    extends Function1[WorldState, (WorldState, A)] {
  def map[B](f:A => B):IOAction[B] =
    flatMap {x => IOAction.unit(f(x))}
  def flatMap[B](f:A => IOAction[B]):IOAction[B]=
    new ChainedAction(this, f)

  private class ChainedAction[+A, B](
      action1: IOAction[B],
      f: B => IOAction[A]) extends IOAction[A] {
    def apply(state1:WorldState) = {
      val (state2, intermediateResult) =
        action1(state1);
      val action2 = f(intermediateResult)
      action2(state2)
    }
  }

  def >>[B](next: => IOAction[B]):IOAction[B] =
    for {
      _ <- this;
      second <- next
    } yield second

  def <<[B](next: => IOAction[B]):IOAction[A] =
    for {
      first <- this;
      _ <- next
    } yield first
```

Because we've got a zero now, it's possible to add a filter method by just following the monad laws.
But here I've created two forms of filter method.
One takes a user specified message to indicate why the filter didn't match whereas the other complies with Scala's required interface and uses a generic error message.

```scala
def filter(
    p: A => Boolean,
    msg:String):IOAction[A] =
  flatMap{x =>
    if (p(x)) IOAction.unit(x)
    else IOAction.fail(msg)}
def filter(p: A => Boolean):IOAction[A] =
  filter(p, "Filter mismatch")
```

A zero also means we can create a monadic plus.
As some infrastructure for creating it, HandlingAction is an action that wraps another action and if that action throws an exception then it sends that exception to a handler function.
onError is a factory method for creating HandlingActions.
Finally, "or" is the monadic plus.
It basically says that if this action fails with an exception then try the alternative action.

```scala
private class HandlingAction[+A](
    action:IOAction[A],
    handler: Exception => IOAction[A])
    extends IOAction[A] {
  def apply(state:WorldState) = {
    try {
      action(state)
    } catch {
      case e:Exception => handler(e)(state)
    }
  }
}

def onError[B >: A](
    handler: Exception => IOAction[B]):
    IOAction[B] =
  new HandlingAction(this, handler)

def or[B >: A](
    alternative:IOAction[B]):IOAction[B] =
  this onError {ex => alternative}
```

The final version of IOApplication stays the same

```scala
sealed trait WorldState{def nextState:WorldState}

abstract class IOApplication {
  private class WorldStateImpl(id:BigInt)
      extends WorldState {
    def nextState = new WorldStateImpl(id + 1)
  }
  final def main(args:Array[String]):Unit = {
    val ioaction = iomain(args)
    ioaction(new WorldStateImpl(0));
  }
  def iomain(args:Array[String]):IOAction[_]
}
```

RTConsole stays mostly the same, but I've added a putLine method as an analog to println.
I've also changed getString to be a val. Why not? It's always the same action.

```scala
//file RTConsole.scala
object RTConsole {
  val getString = IOAction(Console.readLine)
  def putString(s: String) =
    IOAction(Console.print(s))
  def putLine(s: String) =
    IOAction(Console.println(s))
}
```

And now a HelloWorld application to exercise some of this new functionality.
sayHello creates an action from a string.
If the string is a recognized name then the result is an appropriate (or inappropriate) greeting. Otherwise it's a failure action.

Ask is a convenience method that creates an action that will display a specified string then get one.
The >> operator ensures that the action's result will be the result of getString.

processsString takes an arbitrary string and, if it's 'quit' then it creates an action that will say goodbye and be done.
On any other string sayHello is called.
The result is combined with another action using 'or' in case sayHello fails.
Either way the action is sequenced with the loop action.

Loop is interesting.
It's defined as a val just because it can be - a def would work just as well.
So it's not quite a loop in the sense of being a recursive function, but it is a recursive value since it's defined in terms of processString which in turn is defined based on loop.

The iomain function kicks everything off by creating an action that will display an intro then do what the loop action specifies.

**Warning: because of the way the library is implemented this loop will eventually blow the stack. Do not use it in production code. Read the comments to see why.**

```scala
object HelloWorld extends IOApplication {
  import IOAction._
  import RTConsole._

  def sayHello(n:String) = n match {
    case "Bob" => putLine("Hello, Bob")
    case "Chuck" => putLine("Hey, Chuck")
    case "Sarah" => putLine("Helloooo, Sarah")
    case _ => fail("match exception")
  }

  def ask(q:String) =
    putString(q) >> getString

  def processString(s:String) = s match {
    case "quit" => putLine("Catch ya later")
    case _ => (sayHello(s) or
        putLine(s + ", I don't know you.")) >>

        loop
  }

  val loop:IOAction[Unit] =
    for {
      name <- ask("What's your name? ");
      _ <- processString(name)
    } yield ()

  def iomain(args:Array[String]) = {
    putLine(
        "This is an example of the IO monad.") >>
    putLine("Enter a name or 'quit'") >>
    loop
  }
}
```

## 第四部分結論 (Conclusion for Part 4)

In this article I've called the IO monad 'IOAction' to make it clear that instances are actions that are waiting to be performed. 
Many will find the IO monad of little practical value in Scala. 
That's okay, I'm not here to preach about referential transparency. 
However, the IO monad is one of the simplest monads that's clearly not a collection in any sense.

Still, instances of the IO monad can be seen as containers. 
But instead of containing values they contain expressions. 
flatMap and map in essence turn the embedded expressions into more complex expressions.

Perhaps a more useful mental model is to see instances of the IO monad as computations or functions.
flatMap can be seen as applying a function to the computation to create a more complex computation.

In the last part of this series I'll cover a way to unify the container and computation models.
But first I want to reinforce how useful monads can be by showing an application that uses an elephantine herd of monads to do something a bit more complicated.
