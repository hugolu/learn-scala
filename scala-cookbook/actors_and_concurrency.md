# Actors and Concurrency

In general, actors give you the benefit of offering a high level of abstraction for achieving concurrency and parallelism.
- Lightweight, event-driven processes.
- Fault tolerance.
- Location transparency.

A few important things to know about Akka’s implementation of the Actor model:
- You can’t reach into an actor to get information about its state. When you instantiate an Actor in your code, Akka gives you an ActorRef, which is essentially a façade between you and the actor.
- Behind the scenes, Akka runs actors on real threads; many actors may share one thread.
- There are different mailbox implementations to choose from, including variations of unbounded, bounded, and priority mailboxes. You can also create your own mailbox type.
- Akka does not let actors scan their mailbox for specific messages.
- When an actor terminates (intentionally or unintentionally), messages in its mailbox go into the system’s “dead letter mailbox.”

## Getting Started with a Simple Actor

build.sbt:
```scala
name := "Hello Test #1"
version := "1.0"
scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/release"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.2"
```

echo.scala:
```scala
import akka.actor._

class EchoActor extends Actor {
  def receive = {
    case msg: String => println(msg)
  }
}

object EchoTest extends App {
  // ActorSystem is needed to get things started
  val system = ActorSystem("EchoTest")
  
  // Actor is created and started
  val actor = system.actorOf(Props[EchoActor])

  // send actor messages
  actor ! "hello"
  actor ! "hi"

  // shut down the world
  system.shutdown
}
```
- `EchoActor`'s behavior is implemented by defining a `receive` method
- In `EchoTest`, an `ActorSystem` is needed to get things started
- `Actor`s can be created at the `ActorSystem` level, or inside other `Actor`s
- `Actor`s are automatically started when they are created
- Messages are sent to `Actor`s with the `!` method
- `EchoActor` responds to the messages by echoing the message

```shell
$ sbt run
[info] Running EchoTest
hello
hi
```

## Creating an Actor Whose Class Constructor Requires Arguments

echo2.scala
```scala
import akka.actor._

class EchoActor(val name: String) extends Actor {
  def receive = {
    case msg: String => println(s"$name: msg")
  }
}

object EchoTest extends App {
  val system = ActorSystem("EchoTest")
  val actor = system.actorOf(Props(new EchoActor("Foo")))

  actor ! "hello"
  actor ! "hi"

  system.shutdown
}
```

```shell
$ sbt run
[info] Running EchoTest
Foo: hello
Foo: hi
```

## How to Communicate Between Actors

When an actor receives a message from another actor, it also receives an implicit reference named `sender`, and it can use that reference to send a message back to the originating actor.

```scala
mport akka.actor._

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

class Ping(pong: ActorRef) extends Actor {
  var count = 0
  def incrementAndPrint { count += 1; println("ping") }
  def receive = {
    case StartMessage =>
      incrementAndPrint
      pong ! PingMessage
    case PongMessage =>
      incrementAndPrint
      if (count > 3) {
        sender ! StopMessage
        println("ping stopped")
        context.stop(self)
      } else {
        sender ! PingMessage
      }
  }
}

class Pong extends Actor {
  def receive = {
    case PingMessage =>
      println("pong")
      sender ! PongMessage
    case StopMessage =>
      println("pong stopped")
      context.stop(self)
  }
}

object PingPongTest extends App {
  val system = ActorSystem("PingPongTest")
  val pong = system.actorOf(Props[Pong])
  val ping = system.actorOf(Props(new Ping(pong)))

  ping ! StartMessage

  Thread.sleep(100)
  system.shutdown
}
```
- To get things started, the `Ping` class needs an initial reference to the `Pong` actor
- The `context` object is implicitly available to all actors, and can be used to stop actors, among other uses.
- The `ping` and `pong` instances are ActorRef instances, as is the `sender` variable.

```shell
$ sbt run
[info] Running PingPongTest
ping
pong
ping
pong
ping
pong
ping
ping stopped
pong stopped
```

## Understanding the Methods in the Akka Actor Lifecycle

![Actor Liftcycle](http://doc.akka.io/docs/akka/current/_images/actor_lifecycle1.png)
- `preRestart()` called on new instance
- `postRestart()` called on old instance

```scala
import akka.actor._

case object ForceRestart

class Foo extends Actor {
  println("Foo> constructor")
  override def preStart { println("Foo> preStart") }
  override def postStop { println("Foo> postStop") }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    println(s"""Foo> preRestart, ${reason.getMessage}, ${message.getOrElse("")}""")
    super.preRestart(reason, message)
  }
  override def postRestart(reason: Throwable) {
    println(s"Foo> postRestart, ${reason.getMessage}");
    super.postRestart(reason)
  }
  def receive = {
    case ForceRestart => throw new Exception("Boom!")
    case _            => println("Foo> receive")
  }
}

object LiftcycleTest extends App {
  val system = ActorSystem("LifecycleTest")
  val actor = system.actorOf(Props[Foo])

  def sleep(time: Long) = { Thread.sleep(time) }

  println(">>>> sending a message")
  actor ! "hello"
  sleep(100)

  println(">>>> sending a restart")
  actor ! ForceRestart
  sleep(100)

  println(">>>> sending a message")
  actor ! "hi"
  sleep(100)

  println(">>>> shut down the world")
  system.shutdown
}
```

```
[info] Running LiftcycleTest
Foo> constructor
>>>> sending a message
Foo> preStart
Foo> receive
>>>> sending a restart
Foo> preRestart, Boom!, ForceRestart
Foo> postStop
[ERROR] [03/19/2016 21:32:40.707] [LifecycleTest-akka.actor.default-dispatcher-5] [akka://LifecycleTest/user/$a] Boom!
java.lang.Exception: Boom!
Foo> constructor
Foo> postRestart, Boom!
Foo> preStart
>>>> sending a message
Foo> receive
>>>> shut down the world
Foo> postStop
```
- 1st Foo: constructor -> preStart -> throw Exception -> preRestart -> postStop
- 2nd Foo: constructor -> postRestart -> preStart -> postStop

## Starting an Actor
- Akka actors are started asynchronously when they’re passed into the `actorOf` method using a `Props`.
- Within an actor, you create a child actor by calling the `context.actorOf` method.

```scala
import akka.actor._

case class CreateChild(name: String)
case class Name(name: String)

class Parent extends Actor {
  def receive = {
    case CreateChild(name) =>
      var child = context.actorOf(Props[Child], name=s"$name")
      child ! Name(name)
  }
}

class Child extends Actor {
  var name = "???"
  def receive = {
    case Name(name) => this.name = name
    case message: String => println(s"$name got a message: $message")
  }
}

object ParentChildTest extends App {
  val system = ActorSystem("ParentChildTest")
  val parent = system.actorOf(Props[Parent], name = "parent")

  parent ! CreateChild("foo")
  parent ! CreateChild("bar")

  println("sending foo a message")
  val foo = system.actorSelection("/user/parent/foo")
  foo ! "hello"

  Thread.sleep(100)
  system.shutdown
}
```

```
$ sbt run
[info] Running ParentChildTest
sending foo a message
foo got a message: hello
```

## Stopping Actors

- `stop`
  - The actor will continue to process its **current message** (if any), but no additional messages will be processed. See additional notes in the paragraphs that follow.
  - `actorSystem.stop(anActor)`
  - `context.stop(childActor)`
  - `context.stop(self)`
- `PoisonPill`
  - A `PoisonPill` message will stop an actor when the message is processed. A `PoisonPill` message is queued just like an ordinary message and will be handled after other messages queued ahead of it in its mailbox.
  - `actor ! PoisonPill`
- `gracefulStop`
  - Lets you attempt to terminate actors gracefully, waiting for them to timeout. The documentation states that this is a good way to terminate actors in a specific order.

### system.stop and context.stop
```scala
import akka.actor._

class Foo extends Actor {
  override def postStop = { println("postStop") }
  def receive = { case _ => println("got a message") }
}

object SystemStopTest extends App {
  val system = ActorSystem("SystemStopTest")
  val actor = system.actorOf(Props[Foo])

  println(">>> sending a message")
  actor ! "hello"
  Thread.sleep(10)

  println(">>> stopping actor")
  system.stop(actor)
  Thread.sleep(10)

  system.shutdown
}
```
```
$ sbt run
[info] Running SystemStopTest
>>> sending a message
got a message
>>> stopping actor
postStop
```

### PoisonPill message
```scala
import akka.actor._

class Foo extends Actor {
  override def postStop = { println("postStop") }
  def receive = { case _ => println("got a message") }
}

object PoisonPillTest extends App {
  val system = ActorSystem("PoisonPillTest")
  val actor = system.actorOf(Props[Foo])

  actor ! "hello"
  println("before PoisonPill")
  system.stop(actor)
  println("after PoisonPill")
  actor ! "hello"

  system.shutdown
}
```
```shell
$ sbt run
[info] Running PoisonPillTest
before PoisonPill
got a message
after PoisonPill
postStop
```

### gracefulStop
```scala
import akka.actor._
import akka.pattern.gracefulStop
import scala.concurrent.{Future, Await, ExecutionContext}
import scala.concurrent.duration._

class Foo extends Actor {
  override def postStop { println("postStop") }
  def receive = { case _ => println("got a message") }
}

object GracefulStopTest extends App {
  val system = ActorSystem("GracefulStopTest")
  val actor = system.actorOf(Props[Foo])

  try {
    val toStop: Future[Boolean] = gracefulStop(actor, 2 second)
    Await.result(toStop, 3 second)
  } catch {
    case e: Exception => e.printStackTrace
  } finally {
    system.shutdown
  }
}
```
```
$ sbt run
[info] Running GracefulStopTest
postStop
```
- `gracefulStop(actorRef, timeout)` returns a Future that will be completed with success when existing messages of the target actor has been processed and the actor has been terminated.
- If the actor isn’t terminated within the timeout, the `Future` results in an ActorTimeoutException.

```scala
class Foo extends Actor {
  override def postStop { Thread.sleep(3000); println("postStop") }
  def receive = { case _ => println("got a message") }
}
```
```
$ sbt run
[info] Running GracefulStopTest
akka.pattern.AskTimeoutException: Ask timed out on [Actor[akka://GracefulStopTest/user/$a#1157456389]] after [2000 ms]. Sender[null] sent message of type "akka.actor.PoisonPill$".
postStop
```

### “Killing” an actor
The Akka documentation states that sending a Kill message to an actor, “will restart the actor through regular supervisor semantics.” With the default supervisory strategy, the Kill message does what its name states, terminating the target actor.

```scala
import akka.actor._

class Foo extends Actor {
  def receive = { case _ => println("got a message") }

  override def preStart = println("preStart")
  override def postStop = println("postStop")
  override def preRestart(reason: Throwable, message: Option[Any]) = println("preRestart")
  override def postRestart(reason: Throwable) = println("postRestart")
}

object KillTest extends App {
  val system = ActorSystem("KillTest")
  val actor = system.actorOf(Props[Foo])

  actor ! "hello"
  actor ! Kill

  Thread.sleep(100)
  system.shutdown
}
```
```
$ sbt run
[info] Running KillTest
preStart
got a message
[ERROR] [03/19/2016 23:21:00.297] [KillTest-akka.actor.default-dispatcher-3] [akka://KillTest/user/$a] Kill (akka.actor.ActorKilledException)
postStop
```

## Shutting Down the Akka Actor System

```scala
import akka.actor._

object ShutdownTest extends App {
  var system = ActorSystem("ShutdownTest")

  system.shutdown
}
```

## Monitoring the Death of an Actor with watch

```scala
import akka.actor._

class Child extends Actor {
  def receive = {
    case _ => println("Child: got a message")
  }
}

class Parent extends Actor {
  val child = context.actorOf(Props[Child], name = "child")
  context.watch(child)

  def receive = {
    case Terminated(child)  => println("Parent: my child is killed")
    case _                  => println("Parent: got a message")
  }
}

object DeathWatchTest extends App {
  val system = ActorSystem("DeathWatchTest")
  val parent = system.actorOf(Props[Parent], name = "parent")

  val child = system.actorSelection("/user/parent/child")
  child ! PoisonPill

  Thread.sleep(100)
  system.shutdown
}
```
- Use the `watch` method of an actor’s context object to declare that the actor should be notified when an actor it’s monitoring is stopped.

```
[info] Running DeathWatchTest
Parent: my child is killed
```

- Using the `watch` method lets an actor be notified when another actor is stopped (such as with the `PoisonPill` message), or if it’s killed with a `Kill` message or `gracefulStop`. 
- If the child actor throws an exception, which is not killed, the parent won't be notified.

```scala
import akka.actor._

case object Explode

class Child extends Actor {
  println("Child: constructor")

  def receive = {
    case Explode => throw new Exception("Boom!")
    case _ => println("Child: got a message")
  }

  override def preStart = println("Child: preStart")
  override def postStop = println("Child: postStop")
  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("Child: preRestart")
    super.preRestart(reason, message)
  }
  override def postRestart(reason: Throwable) {
    println("Child: PostRestart")
    super.postRestart(reason)
  }
}

class Parent extends Actor {
  val child = context.actorOf(Props[Child], name = "child")
  context.watch(child)

  def receive = {
    case Terminated(child)  => println("Parent: my child is killed")
    case _                  => println("Parent: got a message")
  }
}

object DeathWatchTest extends App {
  val system = ActorSystem("DeathWatchTest")
  val parent = system.actorOf(Props[Parent], name = "parent")

  val child = system.actorSelection("/user/parent/child")
  child ! Explode

  Thread.sleep(100)
  system.shutdown
}
```

```
[info] Running DeathWatchTest
Child: constructor
Child: preStart
Child: preRestart
Child: postStop
[ERROR] [03/21/2016 08:57:04.640] [DeathWatchTest-akka.actor.default-dispatcher-4] [akka://DeathWatchTest/user/parent/child] Boom!
java.lang.Exception: Boom!

Child: constructor
Child: PostRestart
Child: preStart
Child: postStop
```
- `Parent: my child is killed`, the message doesn't occur

## Simple Concurrency with Futures

A `future` gives you a simple way to run an algorithm concurrently

```scala
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object FutureTest extends App {
  def sleep(time: Long) = { Thread.sleep(time) }

  val future = Future {
    sleep(500)
    1 + 1
  }

  val result = Await.result(future, 1 second)
  println(result)

  sleep(500)
}
```
- The `ExecutionContext.Implicits.global` imports the “default global execution context.”
- A `Future` is created by passing a block of code you want to run. The code will be executed at some point of the future.
- The `Await.result` method call declares that it will wait for up to `one second` for the Future to return. If the Future doesn’t return within that time, it throws a `java.util.concurrent.TimeoutException`.

```
$ sbt run
[info] Running FutureTest
2
```

### Run one thing, but don’t block—use callback

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.util.Random

object FutureTest extends App {
  def sleep(time: Long) = { Thread.sleep(time) }

  val future = Future {
    sleep(Random.nextInt(500))
      42
  }

  future.onComplete {
    case Success(value) => println(s"answer=$value")
    case Failure(e)     => e.printStackTrace
  }

  println("A..."); sleep(100)
  println("B..."); sleep(100)
  println("C..."); sleep(100)
  println("D..."); sleep(100)
  println("E..."); sleep(100)
  println("F..."); sleep(100)

  sleep(1000)
}
```
- The `f.onComplete` method call sets up the callback. Whenever the Future completes, it makes a callback to onComplete, at which time that code will be executed.

```
[info] Running FutureTest
A...
B...
C...
D...
answer=42
E...
F...
```

### The `onSuccess` and `onFailure` callback methods
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
//import scala.util.{Failure, Success}
import scala.util.Random

object FutureTest extends App {
  def sleep(time: Long) = { Thread.sleep(time) }

  val future = Future {
    sleep(Random.nextInt(500))
    if(Random.nextInt(500) > 250) throw new Exception("Yikes!") else 42
  }

  future onSuccess {
    case result         => println(s"answer=$result")
  }
  future onFailure {
    case e              => e.printStackTrace
  }

  println("A..."); sleep(100)
  println("B..."); sleep(100)
  println("C..."); sleep(100)
  println("D..."); sleep(100)
  println("E..."); sleep(100)
  println("F..."); sleep(100)

  sleep(1000)
}
```
- `f.onComplete` for `Success` and `Failure` = `f.onSuccess` + `f.onFailure`

### Creating a method to return a `Future[T]`
```scala
import scala.concurrent.{Future, future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FutureTest extends App {
  def sleep(time: Long) = { Thread.sleep(time) }

  def longRunningComputation(i: Int): Future[Int] = future {
    sleep(100)
    i + 1
  }

  longRunningComputation(11) onComplete {
    case Success(result)  => println(s"answer: $result")
    case Failure(e)       => e.printStackTrace
  }

  sleep(1000)
}
```
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FutureTest extends App {
  def sleep(time: Long) = { Thread.sleep(time) }

  def longRunningComputation(i: Int): Future[Int] = Future {
    sleep(100)
    i + 1
  }

  longRunningComputation(11) onComplete {
    case Success(result)  => println(s"answer: $result")
    case Failure(e)       => e.printStackTrace
  }

  sleep(1000)
}
```
- The `future` method shown in this example is another way to create a future. It starts the computation asynchronously and returns a `Future[T]` that will hold the result of the computation. This is a common way to define methods that return a future.
- `Future` or `future`??

```
[info] Running FutureTest
answer: 12
```

### Run multiple things; something depends on them; join them together
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object Cloud {
  def runAlgorithm(i: Int): Future[Int] = Future {
    Thread.sleep(Random.nextInt(500))
    val result = i + 10
    println(s"returning result from cloud: $result")
    result
  }
}

object RunningMultipleFutures extends App {
  println("starting futures")
  val result1 = Cloud.runAlgorithm(10)
  val result2 = Cloud.runAlgorithm(20)
  val result3 = Cloud.runAlgorithm(30)

  println("befor for-comprehension")
  val result = for {
    r1 <- result1
    r2 <- result2
    r3 <- result3
  } yield (r1+r2+r3)

  println("before onSuccess")
  result onSuccess {
    case result => println(s"total=$result")
  }

  println("before end of the world")
  Thread.sleep(2000)
}
```
- The for comprehension is used as a way to *join* the results back together. When all three futures return, their `Int` values are assigned to the variables `r1`, `r2`, and `r3`, and the sum of those three values is returned from the yield expression, and assigned to the result variable.
- Notice that result can’t just be printed after the for comprehension. That’s because the for comprehension returns a new future, so `result` has the type `Future[Int]`.

```
[info] Running RunningMultipleFutures
starting futures
befor for-comprehension
before onSuccess
before end of the world
returning result from cloud: 30
returning result from cloud: 20
returning result from cloud: 40
total=90
```
- When this code is run, the output is **nondeterministic**.

## Sending a Message to an Actor and Waiting for a Reply

```scala
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

case object AskNameMessage

class Foo extends Actor {
  def receive = {
    case AskNameMessage => sender ! "Foo"
  }
}

object AskTest extends App {
  // create system and actor
  val system = ActorSystem("AskTest")
  val actor = system.actorOf(Props[Foo], name = "foo")

  // implicit timeout for ask
  implicit val timeout = Timeout(1 second)

  // ways to ask information
  val future1 = actor ? AskNameMessage
  val result1 = Await.result(future1, 2 second)
  println(result1)

  val future2 = ask(actor, AskNameMessage)
  val result2 = Await.result(future2, 2 second)
  println(result2)

  val future3 = ask(actor, AskNameMessage).mapTo[String]
  val result3 = Await.result(future3, 2 second)
  println(result3)

  val future4 = ask(actor, AskNameMessage)
  val result4 = Await.result(future4, 2 second).asInstanceOf[String]
  println(result4)

  system.shutdown
}
```

## Switching Between Different States with become

```scala
import akka.actor._

case object SwitchFooState
case object SwitchBarState

class Something extends Actor {
  import context._

  def fooState: Receive = {
    case SwitchBarState => become(barState)
    case num: Int => println(s"foo: got a number $num")
  }

  def barState: Receive = {
    case SwitchFooState => become(fooState)
    case str: String => println(s"bar: got a string $str")
  }

  def receive = {
    case _ => println("???: got a message)")
  }

  // fooState by default
  become(fooState)
}

object BecomeTest extends App {
  var system = ActorSystem("BecomeTest")
  var actor = system.actorOf(Props[Something])

  actor ! "hello"
  actor ! 123
  actor ! SwitchBarState
  actor ! "hello"
  actor ! 123

  system.shutdown
}
```
- It’s important to note that the different states can only receive the messages they’re programmed for, and those messages can be different in the different states. 
  - `fooState` only processes `SwitchBarState` and `Int`
  - `barState` only processes `SwitchFooState` and `String`

```
$ sbt run
[info] Running BecomeTest
foo: got a number 123
bar: got a string hello
```

## Using Parallel Collections
