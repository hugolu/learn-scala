object Test1 extends App {
    implicit val ts = SameThreadStrategy

    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}

object Test2 extends App {
    implicit val ts = ThreadPoolStrategy

    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}

object Test3 extends App {
    val x = new Matrix(Array(Array(1,2,3), Array(4,5,6)))
    println(x)

    val y = new Matrix(Array(Array(1), Array(1), Array(1)))
    println(y)

    val z = MatrixUtils.multiply(x, y)
    println(z)
}
