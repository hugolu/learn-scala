# MapReduce

```scala
def product(f: Int => Int)(a: Int, b: Int): Int = if (a > b) 1 else f(a) * product(f)(a + 1, b)
                                                //> product: (f: Int => Int)(a: Int, b: Int)Int
product(x => x * x)(3, 4)                       //> res0: Int = 144

def fact(n: Int) = product(x => x)(1, n)        //> fact: (n: Int)Int
fact(5)                                         //> res1: Int = 120
```
- ```product``` 其中一個參數是 ```f``` 作用是將 ```Int```映射成 ```Int``` (map)
- ```product``` 的運算式，做的是把 ```f(a)``` 到 ```f(b)``` 的數值相乘，得到一個 ```Int``` (reduce)
- 所以可以另外實作 ```mapReduce```，將 ```produce``` 的實作抽出來，變成一個參數 ```reduce```
- 另外還要定義 ```reduce``` 的初始值 ```zero```

```scala
def mapReduce(map: Int => Int, reduce: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int =
  if (a > b) zero else reduce(map(a), mapReduce(map, reduce, zero)(a + 1, b))
                                                //> mapReduce: (map: Int => Int, reduce: (Int, Int) => Int, zero: Int)(a: Int, b: Int)Int

def product(f: Int => Int) = mapReduce(f, (x, y) => x * y, 1)_
                                                //> product: (f: Int => Int)(Int, Int) => Int
product(x => x * x)(3, 4)                       //> res0: Int = 144

def fact(n: Int): Int = mapReduce(x => x, (x, y) => x * y, 1)(1, n)
                                                //> fact: (n: Int)Int
fact(5)                                         //> res1: Int = 120
```
- ```produce``` 型別為 ```(f: Int => Int)(a: Int, b: Int) => Int```，```f``` 作用在```a ... b``` 的每個元素
- ```mapReduce``` 使用的 reducer 運算為 ```(x, y) => x * y```，把所有```f(n)```結果相乘得到一個最終的整數
