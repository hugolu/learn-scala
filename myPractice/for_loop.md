# for loop

Ref: [Scal for Loop](http://www.tutorialspoint.com/scala/scala_for_loop.htm)

## What is the difference between `for()` and `for{}`?
```scala
for (
	i <- 1 to 2;
	j <- 1 to 2
) println(i, j)                                 //> (1,1)
                                                //| (1,2)
                                                //| (2,1)
                                                //| (2,2)

for {
  i <- 1 to 2
  j <- 1 to 2
} println(i, j)                                 //> (1,1)
                                                //| (1,2)
                                                //| (2,1)
                                                //| (2,2)
```
