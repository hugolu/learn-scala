# Chapter 1 - Scala - a blended language

Scala 混合物件導向、函數式編成、型別系統，同時保持程式碼優雅、簡潔。

Scala 試著把三種不同的元素混合到一個語言裡。
- 函數式編程與物件導向
    - 函數編程：透過定義與組合*函數*來編寫程式
    - 物件導向：透過定義與組合*物件*來編寫程式
- 具有表達力的語法與靜態型別
    - 兼顧靜態型別的效能與安全性，同時避免囉唆的型別註釋與樣板語法。
- 進階的語言特色與豐富的 Java 整合
    - 提供許多 Java 所沒有的進階語言特色
    - 在 JVM 上執行，與 Java 語言整合，開發者可直接使用現有的 Java 函式庫

## 1.1 Functional programming meets object orientation

物件導向編程強調程式的**名詞**，把動詞附著其上。
- 一種由上而下的編程方式
    - 每個物件有識別(self/this)、行為(methods)、狀態(members)
    - 識別名詞與定義行為後，名詞間的互動隨即確定
    - 實作互動的問題在於互動需要存在於物件內

```scala
class Bird
class Cat {
    def catch(b: Bird): Unit = ...
    def eat(): Unit = ...
}

val cat = new Cat
val brid = new Brid

cat.catch(bird)
cat.eat()
```
- 物件導向式的寫法，著重在名詞與他們的動作 `Cat.eat()`, `Cat.catch()`...

函數式編成強調程式的**動詞**，與組合操作的方法。
- 一種由下而上的編程方式
    - 函數被看成數學，對輸入執行操作
    - 變數被視為不可變更，有利於併發式編程
    - 函數式編程盡可能延遲副作用，讓程式盡可能簡單

```scala
trait Cat
trait Bird
trait Catch
trait FullTummy

def catch(hunter: Cat, prey: Brid): Cat with Catch
ef eat(consumer: Cat with Catch): Cat with FunnTummy

val story = (catch _) andThen (eat _)
story(new Cat, new Bird)
```
> 這個範例把人搞混了

| 物件導向編程 | 函數式編程 |
|--------------|------------|
| 物件的組合   | 函數的組合 |
| 封裝互動     | 延遲副作用 |
| 迭代         | 遞歸       |
| 命令是流程   | 惰性求值   |
| N/A          | 模式匹配   |

### 1.1.1 Discovering existing functional concepts

Java 的寫法
```java
public interface JdbcTemplate {
    List query(PreparedStatementCreator psc, RowMapper rowMapper)
    ...
}

public interface PreparedStatementCreator {
    PreparedStatement createPreparedStatement(Connection con) throw SQLException;
}

publi interface RowManager {
    Object mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```

Scala 提供一級函數 - 讓我們可以改變 `JdbcTemplate` 查詢的方式
```scala
trait JdbcTemplate {
    def query(psc: Connector => PreparedStatement, rowMapper: (ResultSet, Int) => AnyRef): List[AnyRef]
}
```
- 行為變成 `query` 方法的參數
- Scala 的 `AnyRef` 等同於 `java.lang.Object`

因為 scala 支援泛型，可以修改介面讓使用者回傳特定型別
```scala
trait JdbcTemplate {
    def query[ResultItem](psc: Connection => PreparedStatement, rowMapper: (ResultSet, Int) => ResultItem): List[ResultItem]
}
```

### 1.1.2 Examining functional concepts in Google Collections

Java 的寫法
```java
interface Predicate<T> {
    public boolean apply(T input);
    public boolean equals(Object other);
}

class Iterables {
    public static <T> Iterable<T> filter(Iterable<T> unfiltered, Predicate<? super T> predicate) {...}
    public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate) {...}
}
```
- `filter()` filters using predicate
- `find()` finds using predicate

Scala 的寫法
```scala
object Iterables {
    def filter[T](unfiltered: Iterable[T], predicate: T => Boolean): Iterable[T] = {...}
    def find[T](iterable: Iterable[T], predicate: T => Boolean): T = {...}
}
```
- 沒有使用 `? super T` 型別標記
    - 因為 scala 在宣告時定義型別變異性
    - 變異性標記定義在 `Function1` 類別，不需要在每個使用類別的方法中宣告

組合 `predicate`
```scala
object Predicates {
    def or[T](f1: T => Boolean, f2: T => Boolean) = (t : T) => f1(t) || f2(t)
    def and[T](f1: T => Boolean, f2: T => Boolean) = (t : T) => f1(t) && f2(t)
    def notNull[T]: T => Boolean = _ != null
}
```
- combining functions
- delaying side effects - 直到傳入 `Iterable` 物件前，predicate 不會造成副作用

## 1.2 Static typing and expressiveness

靜態型別導致囉唆的程式碼 - 由 C 語言衍伸的語言，需要明確定義型別

### 1.2.1 Changing sides
### 1.2.2 Type inference
### 1.2.3 Dropping verbose syntax
### 1.2.4 Implicits are an old concept
### 1.2.5 Using Scala’s implicit keyword

## 1.3 Transparently working with the JVM
### 1.3.1 Java in Scala
### 1.3.2 Scala in Java
### 1.3.3 The benefits of a JVM

## 1.4 Summary
