# Scala Object

### Foo.scala 內容
```scala
object Foo {
  def bar = "hello"
}
```

### 編譯過程
```shell
$ scalac Foo.scala
$ ls
Foo$.class Foo.class  Foo.scala
```
- 產生了兩個class: `Foo$.class`, `Foo.class`

### 透過 javap 觀察
```shell
$ javap Foo
Compiled from "Foo.scala"
public final class Foo {
  public static java.lang.String bar();
}
```
- 有一個靜態方法 `public static java.lang.String bar()`

```scala
$ javap Foo$
Compiled from "Foo.scala"
public final class Foo$ {
  public static final Foo$ MODULE$;
  public static {};
  public java.lang.String bar();
}
```
- 有一個靜態類別物件 `public static final Foo$ MODULE$` (`Foo$`是類別名稱)
- 有一段靜態初始化 `public static {}`
- 有一個靜態方法 `public java.lang.String bar()`

到目前還看不出來 `Foo` 與 `Foo$` 的關係，再透過 jad 反組譯一下

### 反組譯 class
```shell
$ jad Foo
Parsing Foo... Generating Foo.jad

$ cat Foo.jad
public final class Foo
{
    public static String bar()
    {
        return Foo$.MODULE$.bar();
    }
}
```
- 靜態方法 `bar()` 回傳呼叫 `Foo$.MODULE$.bar()` 的結果

```shell
$ jad Foo$
Parsing Foo$... Generating Foo$.jad

$ cat Foo$.jad
public final class Foo$
{
    public String bar()
    {
        return "hello";
    }

    private Foo$()
    {
    }

    public static final Foo$ MODULE$ = this;

    static
    {
        new Foo$();
    }
}
```
- `private Foo$(){}`：`Foo$`類別初始化沒做什麼事情
- `static { new Foo$() }`：`Foo$`類別在靜態初始化產生一個 `Foo$`物件
- `public static final Foo$ MODULE$ = this`：產生的物件存使用靜態變數 `MODULE$` 儲存
- `public String bar() { return "hello"; }`：真正做事的是 `Foo$.bar()`

### 討論
為什麼 scala 編譯器這麼大費周章產生兩個類別，`Foo$` 是真正做事的類別、`Foo`只是把提供呼叫的API？

