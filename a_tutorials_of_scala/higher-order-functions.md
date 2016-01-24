# Higher-order Functions

The higher-order functions are functions that take other functions as parameters, or whose result is a function.

```scala
class Decorator(left: String, right: String) {
  def layout[A](x: A) = left + x.toString() + right
}

def apply(f: Int => String, v: Int) = f(v)
val decorator = new Decorator("[", "]")
apply(decorator.layout, 7)
// res0: String = [7]
```
- ```decorator.layout``` is coerced automatically to a value of type ```Int => String``` as required by method apply. 
