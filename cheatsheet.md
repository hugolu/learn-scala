# Cheet Sheet

## For-Expressions

| For-expression | Translation |
|----------------|-------------|
| `for (x <- e1) yield e2` | `e1.map(x => e2)` |
| `for (x <- e1 if f; s) yield e2` | `for (x <- e1.withFilter(x => f); s) yield e2` |
| `for (x <- e1; y <- e2; s) yield e3` | `e1 flatMap(x => for (y <- e2; s) yield e3)` |
