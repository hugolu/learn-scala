# What's the difference between A<:B and +B in Scala?

出處 http://stackoverflow.com/questions/4531455/whats-the-difference-between-ab-and-b-in-scala/4531696#4531696

```Q[A <: B]``` means that class ```Q``` can take any class ```A``` that is a subclass of ```B```.
```scala
scala> class Animal {}
defined class Animal

scala> class Bird extends Animal {}
defined class Bird

scala> class Chicken extends Bird {}
defined class Chicken

scala> class Box[T <: Bird] {}
defined class Box

scala> val box1: Box[Animal] = new Box
<console>:13: error: inferred type arguments [Animal] do not conform to class Box's type parameter bounds [T <: Bird]
       val box1: Box[Animal] = new Box
                               ^

scala> val box2: Box[Bird] = new Box
box2: Box[Bird] = Box@7b35fdf2

scala> val box3: Box[Chicken] = new Box
box3: Box[Chicken] = Box@34ea79af
```

```Q[+B]``` means that ```Q``` can take any class, but if ```A``` is a subclass of ```B```, then ```Q[A]``` is considered to be a subclass of ```Q[B]```.
```scala
scala> class Animal {}
defined class Animal

scala> class Bird extends Animal {}
defined class Bird

scala> class Chicken extends Bird {}
defined class Chicken

scala> class Box[+T] {}
defined class Box

scala> val box1: Box[Bird] = new Box[Animal]
<console>:13: error: type mismatch;
 found   : Box[Animal]
 required: Box[Bird]
       val box1: Box[Bird] = new Box[Animal]
                             ^

scala> val box2: Box[Bird] = new Box[Bird]
box2: Box[Bird] = Box@33a85a34

scala> val box3: Box[Bird] = new Box[Chicken]
box3: Box[Bird] = Box@6eeab440
```

```Q[+A <: B]``` means that container ```Q``` can only take subclasses of ```B``` as well as propagating the subclass relationship.
```scala
scala> class Animal {}
defined class Animal

scala> class Bird extends Animal {}
defined class Bird

scala> class Chicken extends Bird {}
defined class Chicken

scala> class Box[+T <: Bird] {}
defined class Box

scala> val box11: Box[Animal] = new Box[Animal]
<console>:13: error: type arguments [Animal] do not conform to class Box's type parameter bounds [+T <: Bird]
       val box11: Box[Animal] = new Box[Animal]
                  ^
<console>:13: error: type arguments [Animal] do not conform to class Box's type parameter bounds [+T <: Bird]
       val box11: Box[Animal] = new Box[Animal]
                                    ^

scala> val box12: Box[Animal] = new Box[Bird]
<console>:13: error: type arguments [Animal] do not conform to class Box's type parameter bounds [+T <: Bird]
       val box12: Box[Animal] = new Box[Bird]
                  ^

scala> val box13: Box[Animal] = new Box[Chicken]
<console>:14: error: type arguments [Animal] do not conform to class Box's type parameter bounds [+T <: Bird]
       val box13: Box[Animal] = new Box[Chicken]
                  ^

scala> val box21: Box[Bird] = new Box[Animal]
<console>:13: error: type mismatch;
 found   : Box[Animal]
 required: Box[Bird]
       val box21: Box[Bird] = new Box[Animal]
                              ^

scala> val box22: Box[Bird] = new Box[Bird]
box22: Box[Bird] = Box@10681952

scala> val box23: Box[Bird] = new Box[Chicken]
box23: Box[Bird] = Box@17f5b261

scala> val box31: Box[Chicken] = new Box[Animal]
<console>:14: error: type mismatch;
 found   : Box[Animal]
 required: Box[Chicken]
       val box31: Box[Chicken] = new Box[Animal]
                                 ^

scala> val box32: Box[Chicken] = new Box[Bird]
<console>:14: error: type mismatch;
 found   : Box[Bird]
 required: Box[Chicken]
       val box32: Box[Chicken] = new Box[Bird]
                                 ^

scala> val box33: Box[Chicken] = new Box[Chicken]
box33: Box[Chicken] = Box@62facbec
```
