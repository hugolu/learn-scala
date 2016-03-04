# Methods

## Controlling Method Scope

| Scope | Access modifier | Description |
|-------|-----------------|-------------|
| Object-private | `private[this]` | The method is available only to the current instance of the class it’s declared in. |
| Private | `private` | The method is available to the current instance and other instances of the class it’s declared in. |
| Protected | `protected` | The method is available only to instances of the current class and subclasses of the current class. |
| Package | `private[model]` | The method is available to all classes beneath the `com.acme.coolapp.model` package. |
| Public | (no modifier) | The method is public. |

### Object-private
```scala
class Foo {
  private[this] val num = 100
  def myNum = num
  def >(that: Foo) = this.num > that.num	// won't compile
}
```
- The most restrictive access is to mark a method as object-private. When you do this, the method is available only to the current instance of the current object. Other instances of the same class cannot access the method.

### Private
```scala
class Foo {
  private val num = 100
  def myNum = num
  def >(that: Foo) = this.num > that.num
}

class FooSub extends Foo {
	def getNum = num	// won't compile
}
```
- A slightly less restrictive access is to mark a method private, which makes the method available to (a) the current class and (b) other instances of the current class. 

### Protected
```scala
class Foo {
  protected val num = 100
  def myNum = num
  def >(that: Foo) = this.num > that.num
}

class FooSub extends Foo {
	def getNum = num
}

class Bar {
	val foo = new Foo
	foo.num // won't compile
}
```
- Marking a method protected makes the method available to subclasses.

### Package
```scala
package com.acme.coolapp.model {
	class Foo {
		private[model] def doX {}
		private[coolapp] def doY {}
		private[acme] def doZ {}
	}
}

import com.acme.coolapp.model._
package com.acme.coolapp.view {
	class Bar {
		val foo = new Foo
		foo.doX // won't compile
		foo.doY
		foo.doZ
	}
}

package com.acme.common {
	class Bar {
		val foo = new Foo
		foo.doX // won't compile
		foo.doY // won't compile
		foo.doZ
	}
}
```
- To make a method available to all members of the current package—what would be called “package scope” in Java—mark the method as being private to the current package with the `private[packageName]` syntax.

### Public
```scala
package com.acme.coolapp.model {
	class Foo {
		def doX {}
	}
}

import com.acme.coolapp.model._
package org.xyz.bar {
	class Bar {
		val foo = new Foo
		foo.doX
	}
}
```
- If no access modifier is added to the method declaration, the method is public.

## Calling a Method on a Superclass

```scala
class Foo {
  def talk() = println("Foo...")
}

class Bar extends Foo {
  override def talk() = {
    super.talk()
    println("Bar...")
  }
}

var bar = new Bar                               //> bar  : myTest.test60.Bar = myTest.test60$$anonfun$main$1$Bar$1@3d8f5954
bar.talk                                        //> Foo...
                                                //| Bar...
```

### Controlling which trait you call a method from
```scala
trait Foo {
  def talk = "Foo..."
}

trait Bar extends Foo {
  override def talk = "Bar"
}

trait Buz extends Foo {
  override def talk = "Buz"
}

class Woo extends Foo with Bar with Buz {
  def talkSuper = super.talk
  def talkFoo = super[Foo].talk
  def talkBar = super[Bar].talk
  def talkBuz = super[Buz].talk
}

var woo = new Woo                               //> woo  : myTest.test60.Woo = myTest.test60$$anonfun$main$1$Woo$1@76de43f3
woo.talk                                        //> res0: String = Buz
woo.talkSuper                                   //> res1: String = Buz
woo.talkFoo                                     //> res2: String = Foo...
woo.talkBar                                     //> res3: String = Bar
woo.talkBuz                                     //> res4: String = Buz
```
- As shown, when a class inherits from multiple traits, and those traits have a common method name, you can choose which trait to run the method from with the `super[traitName].methodName` syntax.

```scala
class Foo { def say = "Foo" }

class Bar extends Foo { override def say = "Bar" }

class Qiz extends Bar {
  def saySuper = super.say
  def saySuperx2 = super.super.say // won't compile
  def sayBar = super[Bar].say
  def sayFoo = super[Foo].say // won't compile
}
```
- Note that when using this technique, you can’t continue to reach up through the parent class hierarchy __unless__ you directly extend the target class or trait using the `extends` or `with` keywords. 

## Setting Default Values for Method Parameters
```scala
class Foo {
  def cat(x: String = "111", y: String = "222") = x + y
}

var f = new Foo                                 //> f  : myTest.test61.Foo = myTest.test61$$anonfun$main$1$Foo$1@4437a770
f.cat()                                         //> res0: String = 111222
f.cat("???")                                    //> res1: String = ???222
f.cat(x = "???")                                //> res2: String = ???222
f.cat(y = "???")                                //> res3: String = 111???
f.cat("XXX", "YYY")                             //> res4: String = XXXYYY
```

### 反組譯
```scala
public class Foo
{
    public String cat(String x, String y)
    {
        return (new StringBuilder()).append(x).append(y).toString();
    }

    public String cat$default$1()
    {
        return "111";
    }

    public String cat$default$2()
    {
        return "222";
    }

    public Foo()
    {
    }
}
```
```scala
f().cat(f().cat$default$1(), f().cat$default$2());
f().cat("???", f().cat$default$2());
f().cat("???", f().cat$default$2());
String x$1 = "???";
String x$2 = f().cat$default$1();
f().cat(x$2, x$1);
f().cat("xxx", "yyy");
```

| Source | Decompiled Code |
|--------|---------------|
`f.cat()` | `f().cat(f().cat$default$1(), f().cat$default$2())` |
| `f.cat("???")` | `f().cat("???", f().cat$default$2())` |
| `f.cat(x = "???")` | `f().cat("???", f().cat$default$2())` |
| `f.cat(y = "???")` | `f().cat(x$2, x$1)` |
| `f.cat("XXX", "YYY")` | `f().cat("xxx", "yyy")` |

- Names are static: `public String cat(String x, String y)`
- Values are runtime: `f().cat(f().cat$default$1(), f().cat$default$2())`, `f().cat("???", f().cat$default$2())`...

## Using Parameter Names When Calling a Method
```scala
class Foo {
def cat(x: String = "111", y: String = "222") = x + y
}

var f = new Foo                                 //> f  : myTest.test61.Foo = myTest.test61$$anonfun$main$1$Foo$1@2e095b5c
f.cat(x = "???")                                //> res0: String = ???222
f.cat(y = "???")                                //> res1: String = 111???
```

根據 ⟪Scala in Depth⟫ 3.3.2 節 Working with named and default parameters 提出的警告：*Argument names become confusing with inheritance in the mix.*

```scala
class Foo {
  def cat(x: String = "xxx", y: String = "yyy") = x + y
}

class Bar extends Foo {
  override def cat(y: String = "YYY", x: String = "XXX") = x + y
}

var x = new Bar                                 //> x  : myTest.test61.Bar = myTest.test61$$anonfun$main$1$Bar$1@2faa819
x.cat(x = "???")                                //> res0: String = ???YYY

val y: Foo = new Bar                            //> y  : myTest.test61.Foo = myTest.test61$$anonfun$main$1$Bar$1@6b081032
y.cat(x = "???")                                //> res1: String = XXX???
```

```scala
public class Foo
{

    public String cat(String x, String y)
    {
        return (new StringBuilder()).append(x).append(y).toString();
    }

    public String cat$default$1()
    {
        return "xxx";
    }

    public String cat$default$2()
    {
        return "yyy";
    }

    public Foo()
    {
    }
}

public class Bar extends Foo
{

    public String cat(String y, String x)
    {
        return (new StringBuilder()).append(x).append(y).toString();
    }

    public String cat$default$1()
    {
        return "YYY";
    }

    public String cat$default$2()
    {
        return "XXX";
    }

    public Bar()
    {
    }
}
```

- for `Bar`, `x.cat(x = "???")` >> `x().cat(f().cat$default$1(), "???")` >> `"???YYY"`
- for `Foo`, `y.cat(x = "???")` >> `y().cat("???", f().cat$default$2())` >> `"XXX???"`

## Defining a Method That Returns Multiple Items (Tuples)

## Forcing Callers to Leave Parentheses off Accessor Methods

## Creating Methods That Take Variable-Argument Fields

##  Declaring That a Method Can Throw an Exception

## Supporting a Fluent Style of Programming
