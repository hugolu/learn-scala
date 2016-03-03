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

## Setting Default Values for Method Parameters

## Using Parameter Names When Calling a Method

## Defining a Method That Returns Multiple Items (Tuples)

## Forcing Callers to Leave Parentheses off Accessor Methods

## Creating Methods That Take Variable-Argument Fields

##  Declaring That a Method Can Throw an Exception

## Supporting a Fluent Style of Programming
