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

要測試 `x` 與 `y` 是否相等，必須定義任意序列的操作 `f` 引用 `x` 與 `y`，然後觀察結果

```scala
val x = new BankAccount
val y = new BankAccount
f(x, y)
```
```scala
val x = new BankAccount
val y = new BankAccount
f(x, x)
```

如果結果不同，那麼表示式 `x` 與 `y` 必然不同。反之，如果任何組合 `(S, S')` 都能產生相同結果，那麼 `x` 與 `y` 就一樣。

### 操作相等性的反例

如果`f` 檢查程序如下
```scala
def f(a: BankAccount, b: BankAccount) = {
  a deposit 30
  b withdraw 20
}
```

`f(x,y)`運算結果是
```scala
val x = new BankAccount
val y = new BankAccount
x deposit 30      //存款 30
y withdraw 20     //發生例外
```

`f(x,x)`運算結果是
```scala
val x = new BankAccount
val y = new BankAccount
x deposit 30      //存款 30
x withdraw 20     //提款 20
```

最終結果是不一樣的，所以得到 `x` 與 `y` 不同的結論。

### 建立操作相等性

另一方面，如果定義
```scala
val x = new BankAccount
val y = x
```

然後找不到任何操作可以區別 `x` 與 `y` 有何不同，這個例子中 `x` 與 `y` 就是相同。

