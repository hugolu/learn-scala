# Monoid

延伸閱讀：[herding cats — Monoid](http://eed3si9n.com/herding-cats/Monoid.html)

Monoid laws 

In addition to the semigroup law, monoid must satify two more laws:

- associativity (x |+| y) |+| z = x |+| (y |+| z)
- left identity Monoid[A].empty |+| x = x
- right identity x |+| Monoid[A].empty = x
