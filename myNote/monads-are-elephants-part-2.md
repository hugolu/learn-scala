出處：[Monads are Elephants Part 2](http://james-iry.blogspot.tw/2007/10/monads-are-elephants-part-2.html)

# 莫內是頭大象 之二 (Monads are Elephants Part 2)

In part 1, I introduced Scala's monads through the parable of the blind men and the elephant. Normally we're supposed to take the view that each blind man doesn't come close to understanding what an elephant is, but I presented the alternative view that if you heard all the blind men describe their experience then you'd quickly build a surprisingly good understanding of elephants.

In part 2 I'm going to poke at the beast some more by exploring Scala's monad related syntactic sugar: "for comprehensions."

## 來點 "For" (A Little "For")

A very simple "for" looks like this

```scala
val ns = List(1, 2)
val qs = for (n <- ns) yield n * 2
assert (qs == List(2, 4))
```

The "for" can be read as "for [each] n [in] ns yield n * 2." It looks like a loop, but it's not. This is our old friend map in disguise.

```scala
val qs = ns map {n => n * 2}
```

The rule here is simple

```scala
for (x <- expr) yield resultExpr
```

Expands to<sup>[1](#footnote1)</sup>

```scala
expr map {x => resultExpr}
```

And as a reminder, that's equivalent to

```scala
expr flatMap {x => unit(resultExpr)}
```

## 更多 "For" (More "For")

One expression in a "for" isn't terribly interesting. Let's add some more

```scala
val ns = List(1, 2)
val os = List (4, 5)
val qs = for (n <- ns; o <- os)
   yield n * o
assert (qs == List (1*4, 1*5, 2*4, 2*5))
```

This "for" could be read "for [each] n [in] ns [and each] o [in] os yield n * o. This form of "for" looks a bit like nested loops but it's just a bit of map and flatMap.

```scala
val qs = ns flatMap {n =>
   os map {o => n * o }}
```

It's worth while to spend a moment understanding why this works. Here's how it gets computed (red italics gets turned into bold green):

```scala
val qs = ns flatMap {n => 
   os map {o => n * o }}

val qs = ns flatMap {n => 
   List(n * 4, n * 5)}

val qs = 
   List(1 * 4, 1 * 5, 2 * 4, 2 * 5)
```

## 更多表達式 (Now With More Expression)
Let's kick it up a notch.

```scala
val qs =
   for (n <- ns; o <- os; p <- ps)
      yield n * o * p
```

This "for" gets expanded into

```scala
val qs = ns flatMap {n =>
   os flatMap {o =>
      {ps map {p => n * o * p}}}}
```
That looks pretty similar to our previous "for." That's because the rule is recursive

```scala
for(x1 <- expr1;...x <- expr)
   yield resultExpr
```

expands to

```scala
expr1 flatMap {x1 =>
   for(...;x <- expr) yield resultExpr
}
```
This rule gets applied repeatedly until only one expression remains at which point the map form of expansion is used. Here's how the compiler expands the "val qs = for..." statement (again red italics turns to bold green)

```scala
val qs = 
   for (n <- ns; o <- os; p <- ps)
   yield n * o * p

val qs = 
   ns flatMap {n => for(o <- os; p <- ps)
   yield n * o * p}

val qs = 
   ns flatMap {n => os flatMap {o => 
   for(p <- ps) yield n * o * p}}

val qs = 
   ns flatMap {n => os flatMap {o => 
   {ps map {p => n * o * p}}}
```

## 命令式的 "For" (An Imperative "For")

## 過濾式的 "For" (Filtering "For")

## 第二部分結論
