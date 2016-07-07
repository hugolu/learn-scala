# Chapter 7 - 隱式轉換與型別系統結合應用

型別系統與隱式解析提供編寫有表達力且型別安全的軟件所需的工具。
- 隱式轉換把型別編碼進運行的物件
- 隱式轉換允許創建型別類別用來抽象多個類別的行為
- 隱式轉換能用來直接編碼型別約束，遞歸構造型別
- 結合型別構造器與型別邊界，隱式轉換和型別系統能被用來把複雜的問題直接編碼進型別系統
- 隱式轉換能用來保持信息，在保持抽象接口的同時把行為代理給特定類型的實現
- 編寫在需要時能重用的類別與方法

## 7.1 上下文邊界與視圖邊界 (Context bounds and view bounds)

視圖邊界 (view bound) 用來要求一個可用的隱式視圖來轉換另一個型別為另個型別。
```scala
def foo[A < % B](x : A) = x
```
- 參數 `x` 的型別為 `A`，在呼叫的地方必須存在隱式轉換 `A => B`

上下文邊界 (context bound) 聲明必須要有一個給定的型別的隱士值存在。
```scala
def foo[A : B](x : A) = x
```
- 參數 `x` 的型別為 `A`，且呼叫 `foo` 方法時必須有可用的隱式值 `B[A]` 存在。

### 7.1.1 何時使用隱式型別約束 (When to use implicit type constraints)

## 7.2  使用隱式轉換捕捉型別 (Capturing types with implicits)
### 7.2.1 捕捉型別用於運行時計算 (capturing types for runtime evaluation)
### 7.2.2 使用 Manifests (Using Manifests)
### 7.2.3 捕捉型別約束 (Capturing type constraints)
### 7.2.4 特定方法 (Specialized methods)

## 7.3 使用型別類別 (Use type classes)
### 7.3.1 作為型別類別的 FileLike (FileLike as a type class)
### 7.3.2 型別類別的好處 (The benefits of type classes)

## 7.4 用型別系統實現條件執行 (Conditional execution using the type system)
### 7.4.1 異構型別 List (Heterogeneous typed list)
### 7.4.2 IndexedView (IndexedView)

## 7.5結論 (Summary)
