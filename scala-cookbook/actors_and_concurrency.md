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
## Starting an Actor
## Stopping Actors
## Shutting Down the Akka Actor System
## Monitoring the Death of an Actor with watch
## Simple Concurrency with Futures
## Sending a Message to an Actor and Waiting for a Reply
## Switching Between Different States with become
## Using Parallel Collections
