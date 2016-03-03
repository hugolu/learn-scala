# Methods

## Controlling Method Scope

| Access modifier | Description |
|-----------------|-------------|
| private[this] | The method is available only to the current instance of the class it’s declared in. |
| private | The method is available to the current instance and other instances of the class it’s declared in. |
| protected | The method is available only to instances of the current class and subclasses of the current class. |
| private[model] | The method is available to all classes beneath the com.acme.coolapp.model package. |
| private[coolapp] | The method is available to all classes beneath the com.acme.coolapp package. |
| private[acme] | The method is available to all classes beneath the com.acme package. |
| (no modifier) | The method is public. |

## Calling a Method on a Superclass

## Setting Default Values for Method Parameters

## Using Parameter Names When Calling a Method

## Defining a Method That Returns Multiple Items (Tuples)

## Forcing Callers to Leave Parentheses off Accessor Methods

## Creating Methods That Take Variable-Argument Fields

##  Declaring That a Method Can Throw an Exception

## Supporting a Fluent Style of Programming
