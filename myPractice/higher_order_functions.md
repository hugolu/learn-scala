# Higher Order Functions

```scala
def sumInts(a: Int, b: Int): Int = if (a > b) 0 else a + sumInts(a + 1, b)
                                                //> sumInts: (a: Int, b: Int)Int
sumInts(3, 5)                                   //> res0: Int = 12

def cube(a: Int): Int = a * a * a               //> cube: (a: Int)Int
cube(3)                                         //> res1: Int = 27
def sumCubes(a: Int, b: Int): Int = if (a > b) 0 else cube(a) + sumCubes(a + 1, b)
                                                //> sumCubes: (a: Int, b: Int)Int
sumCubes(3, 5)                                  //> res2: Int = 216

def factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n - 1)
                                                //> factorial: (n: Int)Int
factorial(3)                                    //> res3: Int = 6
def sumFactorials(a: Int, b: Int): Int = if (a > b) 0 else factorial(a) + sumFactorials(a + 1, b)
                                                //> sumFactorials: (a: Int, b: Int)Int
sumFactorials(3, 5)                             //> res4: Int = 150
```

## 使用 Function Types
```scala
def sum(f: Int => Int, a: Int, b: Int): Int = if (a > b) 0 else f(a) + sum(f, a + 1, b)
                                                //> sum: (f: Int => Int, a: Int, b: Int)Int

def id(n: Int): Int = n
def cube(n: Int): Int = n * n * n
def factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n - 1)

def sumInts(a: Int, b: Int): Int = sum(id, a, b)
                                                //> sumInts: (a: Int, b: Int)Int
def sumCubes(a: Int, b: Int): Int = sum(cube, a, b)
                                                //> sumCubes: (a: Int, b: Int)Int
def sumFactorials(a: Int, b: Int): Int = sum(factorial, a, b)
                                                //> sumFactorials: (a: Int, b: Int)Int

sumInts(3, 5)                                   //> res0: Int = 12
sumCubes(3, 5)                                  //> res1: Int = 216
sumFactorials(3, 5)                             //> res2: Int = 150
```

## 使用 Anonymous Functions
```scala
  def sum(f: Int => Int, a: Int, b: Int): Int = if (a > b) 0 else f(a) + sum(f, a + 1, b)
                                                  //> sum: (f: Int => Int, a: Int, b: Int)Int

  def sumInts(a: Int, b: Int): Int = sum(x => x, a, b)
                                                  //> sumInts: (a: Int, b: Int)Int
  def sumCubes(a: Int, b: Int): Int = sum(x => x * x * x, a, b)
                                                  //> sumCubes: (a: Int, b: Int)Int

  sumInts(3, 5)                                   //> res0: Int = 12
  sumCubes(3, 5)                                  //> res1: Int = 216

```

## Functions returning Functions
```scala
def sum(f: Int => Int): (Int, Int) => Int = {
  def sumF(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sumF(a + 1, b)
  sumF
}                                               //> sum: (f: Int => Int)(Int, Int) => Int

def factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n - 1)
                                                //> factorial: (n: Int)Int

def sumInts(a: Int, b: Int): Int = sum(x => x)(a, b)
                                                //> sumInts: (a: Int, b: Int)Int
def sumCubes(a: Int, b: Int): Int = sum(x => x * x * x)(a, b)
                                                //> sumCubes: (a: Int, b: Int)Int
def sumFactorials(a: Int, b: Int): Int = sum(factorial)(a, b)
                                                //> sumFactorials: (a: Int, b: Int)Int

sumInts(3, 5)                                   //> res0: Int = 12
sumCubes(3, 5)                                  //> res1: Int = 216
sumFactorials(3, 5)                             //> res2: Int = 150
```
