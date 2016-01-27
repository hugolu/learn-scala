# Contravariance

參考連結 http://daily-scala.blogspot.tw/2010/03/contravariance.html

```scala
class A {}
class B extends A {}
class C extends B {}

class Output[-T] { def write(a: T) = () }
def writeObject(out: Output[B]) = out.write(new B)

writeObject(new Output[A])
writeObject(new Output[B])
writeObject(new Output[c])
```
