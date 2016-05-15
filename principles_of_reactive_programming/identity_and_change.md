# Identity and Change 

賦值 (assignment) 為決定兩個表示式是否相同帶來新的問題。

```scala
val x = E
val y = E
```

`E` 是任意表示式，假設 `x` 與 `y` 相同合情合理。所以也可以寫成

```scala
val x = E
val y = x
```

這個特性就是所謂的*引用透明性* (referential transparency)。

但一旦允許賦值，兩個公式就不一樣了。例如
```scala
val x = new BankAccount
val y = new BankAccount
```

`x` 與 `y` 當然不一樣。

### 操作相等性 (Operational Equivalence)

"相等"的精確意思由"操作相等性"的特型來定義。

假設有 `x` 與 `y` 兩個定義，假如沒有任何可能的測試可以分辨兩者，`x` 與 `y` 就具有操作相等性。
