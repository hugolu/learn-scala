# Scenario 0

```scala
  def sum0(xs: List[Int]): Int = {
    var s = 0
    for (x <- xs)
      s += x
    s
  }
  def sum1(xs: List[Int]): Int = xs.foldLeft(0)((a, x) => a + x)
```
- `sum0` 加總 `List[Int]` 內的元素 (方法自己實作)
- `sum1` 加總 `List[Int]` 內的元素 (使用`List.foldLeft`)
