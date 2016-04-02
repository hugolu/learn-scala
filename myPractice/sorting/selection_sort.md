# Selection Sort

## Reference
- https://en.wikipedia.org/wiki/Selection_sort
- http://freefeast.info/general-it-articles/selection-sort-pseudo-code-of-selection-sort-selection-sort-in-data-structure/

##  The idea of Selection Sort
- First it finds the smallest element in the array.
- Exchange that smallest element with the element at the first position.
- Then find the second smallest element and exchange that element with the element at the second position.
- This process continues until the complete array is sorted.

## C code
```c
/* a[0] to a[n-1] is the array to sort */
int i,j;

/* advance the position through the entire array */
/*   (could do j < n-1 because single element is also min element) */
for (j = 0; j < n-1; j++) {
    /* find the min element in the unsorted a[j .. n-1] */

    /* assume the min is the first element */
    int iMin = j;
    /* test against elements after j to find the smallest */
    for ( i = j+1; i < n; i++) {
        /* if this element is less, then it is the new minimum */
        if (a[i] < a[iMin]) {
            /* found new minimum; remember its index */
            iMin = i;
        }
    }

    if(iMin != j) {
        swap(a[j], a[iMin]);
    }
}
```

## Scala code
用 Scala `Array` 透過 iteration 的方式實作

```scala
def swap(array: Array[Int], x: Int, y: Int) = {
  val temp = array(x)
  array(x) = array(y)
  array(y) = temp
}                                               //> swap: (array: Array[Int], x: Int, y: Int)Unit

def ssort(array: Array[Int]): Array[Int] = {
	val n = array.length
	for (i <- 0 until n - 1) {
		for (j <- i + 1 until n) {
			if (array(j) < array(i)) {
				swap(array, j, i)
			}
		}
	}
	array
}                                         //> ssort: (array: Array[Int])Array[Int]
ssort(Array(1,3,5,7,9,8,6,4,2))           //> res0: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)

def ssort2(array: Array[Int]): Array[Int] = {
	val n = array.length
	for {
		i <- 0 until n-1
		j <- i+1 until n
	} if (array(j) < array(i)) swap(array, i, j)
	array
}                                         //> ssort2: (array: Array[Int])Array[Int]
ssort2(Array(1,3,5,7,9,8,6,4,2))          //> res1: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
```
- 個人比較喜歡 `ssort2()`，因為比較簡潔，也不會內縮太多層
