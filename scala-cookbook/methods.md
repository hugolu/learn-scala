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
scala> class Foo {
     |   private[this] val num = 100
     |   def >(that: Foo) = this.num > that.num
     | }
<console>:12: error: value num is not a member of Foo
         def >(that: Foo) = this.num > that.num
                                            ^
```

### Private
```scala
```

### Protected
```scala
```

### Package
```scala
```

### Public
```scala
```

## Calling a Method on a Superclass

## Setting Default Values for Method Parameters

## Using Parameter Names When Calling a Method

## Defining a Method That Returns Multiple Items (Tuples)

## Forcing Callers to Leave Parentheses off Accessor Methods

## Creating Methods That Take Variable-Argument Fields

##  Declaring That a Method Can Throw an Exception

## Supporting a Fluent Style of Programming
