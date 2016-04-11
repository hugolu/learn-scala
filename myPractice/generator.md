# Generator

```scala
package myTest

object generatorTest {

  val integers = new Generator[Int] {
    val rand = new java.util.Random
    def generate = rand.nextInt()
  }                                               //> integers  : myTest.Generator[Int]{val rand: java.util.Random} = myTest.gener
                                                  //| atorTest$$anonfun$main$1$$anon$1@6b87978e
  val booleans = integers.map(_ > 0)              //> booleans  : myTest.Generator[Boolean] = myTest.Generator$$anon$2@263ee6cc
  integers.generate                               //> res0: Int = 522551275
  integers.generate                               //> res1: Int = 1647180695
  integers.generate                               //> res2: Int = 147559894

  def makePairs[T, U](t: Generator[T], u: Generator[U]) =
    t flatMap (x => u map (y => (x, y)))          //> makePairs: [T, U](t: myTest.Generator[T], u: myTest.Generator[U])myTest.Gene
                                                  //| rator[(T, U)]

  booleans.generate                               //> res3: Boolean = false
  booleans.generate                               //> res4: Boolean = true
  booleans.generate                               //> res5: Boolean = false

  val pairs = makePairs(integers, booleans)       //> pairs  : myTest.Generator[(Int, Boolean)] = myTest.Generator$$anon$3@4ef617c
                                                  //| e
  pairs.generate                                  //> res6: (Int, Boolean) = (-2071354871,true)
  pairs.generate                                  //> res7: (Int, Boolean) = (78383039,true)
  pairs.generate                                  //> res8: (Int, Boolean) = (1310258250,false)
}

trait Generator[+T] {
  self =>

  def generate: T

  def map[S](f: T => S) = new Generator[S] {
    def generate = f(self.generate)
  }

  def flatMap[S](f: T => Generator[S]) = new Generator[S] {
    def generate = f(self.generate).generate
  }
}
```
