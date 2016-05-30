object Test extends App {
    def test1() {
        import complexmath._

        println(ComplexNumber(1, 0) * ComplexNumber(0, 1))
        println(ComplexNumber(1, 0) + ComplexNumber(0, 1))

        println(i * 1.0)
    }
    test1()

    def test2() {
        import complexmath.i
        println(i * 5.0 + 1.0)
    }
    test2()

    def test3() {
        import complexmath.i
        println(1.0 + 5.0 * i)
    }
    test3()

    def test4() {
        import complexmath.i
        implicit def doubleToReal(x: Double) = new {
            def real = "For Reals(" + x + ")"
        }

        println(5.0 real)
    }
    test4()
}
