# MapReduce

```scala
def product(f: Int => Int)(a: Int, b: Int): Int = if (a > b) 1 else f(a) * product(f)(a + 1, b)
                                                //> product: (f: Int => Int)(a: Int, b: Int)Int
product(x => x * x)(3, 4)                       //> res0: Int = 144

def fact(n: Int) = product(x => x)(1, n)        //> fact: (n: Int)Int
fact(5)                                         //> res1: Int = 120
```

```scala
def mapReduce(map: Int => Int, reduce: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int = if (a > b) zero else reduce(map(a), mapReduce(map, reduce, zero)(a + 1, b))
                                                //> mapReduce: (map: Int => Int, reduce: (Int, Int) => Int, zero: Int)(a: Int, b: Int)Int

def product(f: Int => Int) = mapReduce(f, (x, y) => x * y, 1)_
                                                //> product: (f: Int => Int)(Int, Int) => Int
product(x => x * x)(3, 4)                       //> res0: Int = 144

def fact(n: Int): Int = mapReduce(x => x, (x, y) => x * y, 1)(1, n)
                                                //> fact: (n: Int)Int
fact(5)                                         //> res1: Int = 120
  ```
