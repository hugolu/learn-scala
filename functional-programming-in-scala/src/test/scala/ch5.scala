import org.scalatest.FunSuite
import fpinscala.Ch5._

class Ch5Tests extends FunSuite {
  test("Stream to List") {
    val s = Stream(1,2,3)
    val l = List(1,2,3)
    assert(s.toList == l)
    assert(s.toListTailrec == l)
    assert(s.toListFast == l)
    assert(s.toListViaFoldRight == l)
    assert(s.toListViaFoldRightViaFoldLeft == l)
  }

  test("take first N elements") {
    val s = Stream(1,2,3,4,5)
    assert(s.take(3).toList == List(1,2,3))
    assert(s.take(6).toList == List(1,2,3,4,5))
  }

  test("drop first N elements") {
    val s = Stream(1,2,3,4,5)
    assert(s.drop(3).toList == List(4,5))
    assert(s.drop(6) == Empty)
  }

  test("take while predicate") {
    val s = Stream(1,2,3,4,5)
    assert(s.takeWhile(_ < 4).toList == List(1,2,3))
  }
}
