# The Option Type

參考連結
- http://danielwestheide.com/blog/2012/12/19/the-neophytes-guide-to-scala-part-5-the-option-type.html

## Creating an option
```scala
// None
scala> val greeting: Option[String] = Option(null)
greeting: Option[String] = None

scala> val greeting: Option[String] = None
greeting: Option[String] = None

scala> greeting.isDefined
res0: Boolean = false

// Some
scala> val greeting: Option[String] = Option("Hello")
greeting: Option[String] = Some(Hello)

scala> val greeting: Option[String] = Some("Hello")
greeting: Option[String] = Some(Hello)

scala> greeting.isDefined
res1: Boolean = true
```

## Providing a default value
```scala
scala> case class User(
     |   id: Int,
     |   firstName: String,
     |   lastName: String,
     |   age: Int,
     |   gender: Option[String])
defined class User

scala> val user = User(2, "Johanna", "Doe", 30, None)
user: User = User(2,Johanna,Doe,30,None)

scala> println("Gender: " + user.gender.getOrElse("not specified"))
Gender: not specified
```

## Pattern matching
```scala
scala> case class User(
     |   id: Int,
     |   firstName: String,
     |   lastName: String,
     |   age: Int,
     |   gender: Option[String])
defined class User

scala> val user = User(2, "Johanna", "Doe", 30, None)
user: User = User(2,Johanna,Doe,30,None)

scala> val gender = user.gender match {
     | case Some(gender) => gender
     | case None => "not specified"
     | }
gender: String = not specified
```
