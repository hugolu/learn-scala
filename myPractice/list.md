# List

## constructors
All lists are constructed from
- the empty list ```Nil```, and
- the construction operation ```::``` (唸作 cons)

```scala
List(1, 2, 3)                                   //> res0: List[Int] = List(1, 2, 3)
1 :: (2 :: (3 :: Nil))                          //> res1: List[Int] = List(1, 2, 3)

List("apples", "oranges", "pears")              //> res2: List[String] = List(apples, oranges, pears)
"apples" :: ("oranges" :: ("pears" :: Nil))     //> res3: List[String] = List(apples, oranges, pears)

List()                                          //> res4: List[Nothing] = List()
Nil                                             //> res5: scala.collection.immutable.Nil.type = List()
```

## Right associatively
Convention: operators ending in ```:``` associate to the rigth
- ```A :: B :: C``` is interpreted as ```A :: (B :: C)```

Operators ending in ```:``` are also different in the they are seen as method calls of the *right-hand* operand.
- ```A :: B :: C``` is equivalent to ```Nil.::(C).::(B).::(A)```

```scala
List(1, 2, 3)                                   //> res0: List[Int] = List(1, 2, 3)
1 :: (2 :: (3 :: Nil))                          //> res1: List[Int] = List(1, 2, 3)
1 :: 2 :: 3 :: Nil                              //> res2: List[Int] = List(1, 2, 3)
Nil.::(3).::(2).::(1)                           //> res3: List[Int] = List(1, 2, 3)
```
