# Currying
Methods may define multiple parameter lists. When a method is called with a fewer number of parameter lists, then this will yield a function taking the missing parameter lists as its arguments.

```scala
def filter(xs: List[Int], p: Int => Boolean): List[Int] = {
  if (xs.isEmpty)
    xs
  else if (p(xs.head))
    xs.head :: filter(xs.tail, p)
  else
    filter(xs.tail, p)
}

val nums = List(1, 2, 3, 4, 5, 6, 7, 8)

def modN(n: Int)(x: Int) = ((x % n) == 0)
println(nums.filter(modN(2)))
//List(2, 4, 6, 8)
println(nums.filter(modN(3)))
//List(3, 6)

println(filter(nums, modN(2)))
//List(2, 4, 6, 8)
println(filter(nums, modN(3)))
//List(3, 6)

def modM(m: Int, x: Int) = ((x % m) == 0)
println(nums.filter(modM(2, _)))
//List(2, 4, 6, 8)
println(nums.filter(modM(3, _)))
//List(3, 6)

println(filter(nums, modM(2, _)))
//List(2, 4, 6, 8)
println(filter(nums, modM(3, _)))
//List(3, 6)
```
