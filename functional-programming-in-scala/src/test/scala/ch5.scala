import org.scalatest.FunSuite
import fpinscala.Ch5._

class Ch5Tests extends FunSuite {
  test("Stream to List") {
    val s = Stream(1,2,3)
    val l = List(1,2,3)
    assert(s.toList == l)
    assert(s.toListTailrec == l)
    assert(s.toListFast == l)
  }
}
