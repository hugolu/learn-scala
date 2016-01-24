# Nested Functions

```scala
def filter(xs: List[Int], threshold: Int) = {
  def process(ys: List[Int]): List[Int] =
    if (ys.isEmpty) ys
    else if (ys.head < threshold) ys.head :: process(ys.tail)
    else process(ys.tail)
  process(xs)
}

println(filter(List(1, 9, 2, 8, 3, 7, 4), 5))
// List(1, 2, 3, 4)
```
- the nested function process refers to variable threshold defined in the outer scope as a parameter value of filter
- 要怎樣的思考方式才能寫出這樣的流程啊？
