# Scala Type Bounds: Upper Bounds, Lower Bounds and View Bounds

參考連結：http://www.journaldev.com/9609/scala-typebounds-upper-lower-and-view-bounds

Type Bound是什麼?
- 在Scala中，Type Bound是加諸在型別參數(Type Parameter)或型別變數(Type Variable)的限制。藉由Type Bound，定義型別變數的限制。

Type Bound的好處
- 型別安全的應用開發

Type Bound的種類
- Upper Bounds
- Lower Bounds
- View Bounds

## Upper Bounds
定義
- Upper Bound宣告像是```[T <: S]```，```T```是型別參數，```S```是種型別，表示參數型別```T```必須是```S```型別或是子型別。

```scala
class Animal
class Dog extends Animal
class Puppy extends Dog

def display[T <: Dog](t: T) = println(t)        //> display: [T <: myTest.test2.Dog](t: T)Unit

//display(new Animal)                           //> inferred type arguments [Animal] do not conform to method display's type parameter bounds [T <: Dog]
display(new Dog)                                //> myTest.test2$Dog@1ea85692
display(new Puppy)                              //> myTest.test2$Puppy@3dcb9af7
```

## Lower Bounds

## View Bounds
