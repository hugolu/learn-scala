# Function1

## Definition of Variance

Say ```C[T]``` is a parameterized type and ```A```, ```B``` are types such that ```A <: B```.
In genreal, there are three possible relationships between ```C[A]``` and ```C[B]```:

| Relationship | Variance Type |
|--------------|---------------|
| ```C[A] <: C[B]``` | C is covariant |
| ```C[A] >: C[B]``` | C is contravariant |
| neither ```C[A]``` or ```C[B]``` is a subtype of the other | C is nonvariant |
