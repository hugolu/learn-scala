# Monoid

延伸閱讀：[herding cats — Monoid](http://eed3si9n.com/herding-cats/Monoid.html)

### Monoid 法則

除了滿足 Semigroup 法則，Monoid 必須滿足兩個額外的法則：

| 定律 | 公式 |
|------|------|
| 結合率 (associativity) | (x |+| y) |+| z = x |+| (y |+| z) |
| 左恆等 (left identity) | Monoid[A].empty |+| x = x |
| 右恆等 (right identity)| x |+| Monoid[A].empty = x |
