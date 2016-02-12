# Recursive Functions

## GCD

```scala
def gcd(a: Int, b:Int): Int = if (b == 0) a else gcd(b, a % b)

gcd(14, 21)
// if (21 == 0) 14 else gcd(21, 14%21)
// if (false) 14 else gcd(21, 14%21)
// gcd(21, 14%21)
// gcd(21, 14)
// if (14 == 0) 21 else gcd(14, 21%14)
// if (false) 21 else gcd(14, 21%14)
// gcd(14, 21%14)
// gcd(14, 7)
// if (7 == 0) 14 else gcd(7, 14%7)
// if (false) 14 else gcd(7, 14%7)
// gcd(7, 14%7)
// gcd(7, 0)
// if (0 == 0) 7 else gcd(0, 7%0)
// 7
```
- It's a tail recursive function.
- The compiler will optimize a tail recursive function. 

## Tail Recursion
Implementation Consideration: If a function calls itself as its last action, the function’s stack frame can be reused. This is called tail recursion.
- Tail recursive functions are iterative processes.

In general, if the last action of a function consists of calling a function (which may be the same), **one stack frame** would be sufficient for both functions. Such calls are called tail-calls.

## Factorial
```scala
def factorial(n: Int): Int = if (n == 0) 1 else n*factorial(n-1)

factorial(4)
// if (4 == 0) 1 else 4 * factorial(4-1)
// if (false) 1 else 4 * factorial(4-1)
// if (false) 1 else 4 * factorial(3)
// 4 * factorial(3)
// 4 * (3 * factorial(2))
// 4 * (3 * (2 * factorial(1))
// 4 * (3 * (2 * (1 * factorial(0))))
// 4 * (3 * (2 * (1 * 1)))
// 4 * (3 * (2 * 1))
// 4 * (3 * 2)
// 4 * 6
// 24
```
- It's not a tail recursive function.

```scala
def factorial(n:Int): Int = {
  def loop(acc: Int, n: Int): Int = if (n == 0) acc else loop(acc * n, n - 1)
  loop(1, n)
}
factorial(4)
```
- It's a tail recursive function.

___
## Evaluating a Function Application

One simple rule : One evaluates a function application ```f(e1 , ..., en )```
- by evaluating the expressions ```e1 , . . . , en``` resulting in the values ```v1, ..., vn```, then
- by replacing the application with the body of the function ```f```, in which
- the actual parameters ```v1 , ..., vn``` replace the formal parameters of ```f```.

This can be formalized as a rewriting of the program itself: 
```
def f(x1, ..., xn) = B; ... f(v1, ..., vn)
→
def f(x1, ..., xn) = B; ... [v1/x1, ..., vn/xn] B
```
Here, ```[v1/x1, ..., vn/xn] B``` means:
The expression ```B``` in which all occurrences of ```xi``` have been replaced by ```vi```.
```[v1/x1, ..., vn/xn]``` is called a **substitution**.
