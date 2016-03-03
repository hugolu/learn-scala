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
```
class Foo {
  private[this] val num = 100
  def myNum = num
  def >(that: Foo) = this.num > that.num	// won't compile
}
```

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
	foo.num
}
```

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

## Calling a Method on a Superclass

## Setting Default Values for Method Parameters

## Using Parameter Names When Calling a Method

## Defining a Method That Returns Multiple Items (Tuples)

## Forcing Callers to Leave Parentheses off Accessor Methods

## Creating Methods That Take Variable-Argument Fields

##  Declaring That a Method Can Throw an Exception

## Supporting a Fluent Style of Programming
