# Functional Programming - Lots of emphasis on recursion, why?

original like: http://stackoverflow.com/questions/12659581/functional-programming-lots-of-emphasis-on-recursion-why
___
Using recursion we don't need a **mutable state** while solving some problem, and this make possible to specify a semantic in simpler terms. Thus solutions can be simpler, in a formal sense.
___
Pure functional programming means programming **without side effects**. Which means, if you write a loop for instance, the body of your loop can't produce side effects. Thus, if you want your loop to do something, it has to reuse the result of the previous iteration and produce something for the next iteration. Thus, the body of your loop is a function, taking as parameter the result of previous execution and calling itself for the next iteration with its own result. This does not have a huge advantage over directly writing a recursive function for the loop.

A program which doesn't do something trivial will have to iterate over something at some point. For functional programming this means the program has to use recursive functions.
___

To sum the value of a list
```scala
val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)  //> list  : List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

The iterative way - ```total``` is modified in each iteration
```scala
var total = 0                                   //> total  : Int = 0
for (n <- list) total += n
total                                           //> res0: Int = 55
```

The recursive way - nothing is mutable
```scala
def sum(xs: List[Int]): Int = if (xs.size == 0) 0 else xs.head + sum(xs.tail)
                                                //> sum: (xs: List[Int])Int
sum(list)                                       //> res1: Int = 55
```
