# Bubble Sort

## Reference
- [Bubble sort](https://en.wikipedia.org/wiki/Bubble_sort)

## Step-by-step example
Let us take the array of numbers "5 1 4 2 8", and sort the array from lowest number to greatest number using bubble sort. In each step, elements written in bold are being compared. Three passes will be required.

### First Pass
```
( 5 1 4 2 8 ) \to ( 1 5 4 2 8 ), Here, algorithm compares the first two elements, and swaps since 5 > 1.
( 1 5 4 2 8 ) \to ( 1 4 5 2 8 ), Swap since 5 > 4
( 1 4 5 2 8 ) \to ( 1 4 2 5 8 ), Swap since 5 > 2
( 1 4 2 5 8 ) \to ( 1 4 2 5 8 ), Now, since these elements are already in order (8 > 5), algorithm does not swap them.
```

### Second Pass
```
( 1 4 2 5 8 ) \to ( 1 4 2 5 8 )
( 1 4 2 5 8 ) \to ( 1 2 4 5 8 ), Swap since 4 > 2
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
```
Now, the array is already sorted, but the algorithm does not know if it is completed. The algorithm needs one whole pass without any swap to know it is sorted.

### Third Pass
```
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
( 1 2 4 5 8 ) \to ( 1 2 4 5 8 )
```

## Scala Code

Scala `List` 的特性不適合執行 bubble sort (需要 iteration，而非 recursion)，改用 Scala `Array` 實作。這個實作有 *side-effect* 會改變 `Array` 的內容。

```scala
val nums = Array(2, 4, 6, 8, 9, 7, 5, 3, 1)     //> nums  : Array[Int] = Array(2, 4, 6, 8, 9, 7, 5, 3, 1)

def bsort(array: Array[Int]): Array[Int] = {
  val n = array.length
  for {
    i <- 0 until n
    j <- 0 until (n - 1 - i)
    if (array(j) > array(j + 1))
  } swap(array, j, j + 1)
  array
}                                               //> bsort: (array: Array[Int])Array[Int]
def swap(array: Array[Int], x: Int, y: Int) = {
  val temp = array(x)
  array(x) = array(y)
  array(y) = temp
}                                               //> swap: (array: Array[Int], x: Int, y: Int)Unit

bsort(nums)                                     //> res0: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
```
