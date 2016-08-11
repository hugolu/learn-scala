# 隱式轉換

參考自 [隐式转换](http://www.lai18.com/content/2331503.html)

## 應用情境
### 轉換為期望型別
看到 X 但需要 Y，編譯器查找將 X 轉換為 Y 的隱式轉換函數

```scala
implicit def doubleToInt(d: Double) = d.toInt

scala> val i: Int = 3.4       //i: Int = 3
```

### 轉換為方法接收者型別
呼叫型別 X 的 f() 方法，但 X 無法回應 f() 方法，但型別 Y 可以回應 f() 方法，編譯器將查找將 X 轉換為 Y 的隱式轉換函數

```scala
class A {}
class B { def foo = println("hello") }
implicit def a2b(a: A): B = new B

(new A).foo                           //> hello
```

#### 情境一：與新類型的交互
讓新類型更平滑的整合到現存型別中

```scala
case class Rational(n: Int, d: Int) {
    def +(that: Rational): Rational = Rational(this.n*that.d + that.n*this.d, this.d*that.d)
    def +(that: Int): Rational = Rational(this.n + that*this.d, this.d)
}

scala> Rational(3,2) + Rational(1,5)  //= Rational(17,10)
scala> Rational(3,2) + 1              //= Rational(5,2)
```
- `Rational` 重載 `+` 方法可以處理 `Int` 與 `Rational`
- 但 `Int` 的 `+` 方法無法處理 `Rational`

```scala
implicit def intToRational(x: Int): Rational = Rational(x, 1)

scala> 1 + Rational(3,2)              //= Rational(5,2)
```
- 將 `Int` 轉換為 `Rational`，然後呼叫 `+` 執行加法

#### 情境二：模擬添加新的方法
為整個**型別系統**添加方法

```scala
class ArrowAssoc[A](x: A) {
    def -->[B,C](y: Tuple2[B,C]): Tuple3[A,B,C] = Tuple3(x, y._1, y._2)
}
implicit def any2ArrowAssoc[A](x:A): ArrowAssoc[A] = new ArrowAssoc(x)

scala> 1 --> (2,3)                    //= (1,2,3)
scala> "scala" --> ("hello", "world") //= (scala,hello,world)
```

## 轉換規則

規則 | 說明
-----|------
標記規則      |只有標記 `implicit` 才能使用
用戶規則      |插入隱式轉換必須以單一標示符存在於作用域中，或定義於轉換目標或來源型別的伴隨物件中
無歧義規則    |不存在其他可插入的轉換
單一調用規則  |只會嘗試一個隱式轉換
顯式優先      |
