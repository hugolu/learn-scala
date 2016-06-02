object Test extends App {
    def test1 = {
        val x = new IntStore(5)
        val handle = x.observe(println)

        x.set(2)
        x.unobserve(handle)
        x.set(4)

        val y = new IntStore(2)
        //y.unobserve(handle)   // won't compile
    }

    def test2 = {
        val x = new IntStore(5)
        val y = new IntStore(2)

        val callback = println(_: Any)

        val handle1 = x.observe(callback)
        val handle2 = y.observe(callback)
        println(handle1 == handle2)

        //y.unobserve(handle1)  // won't compile
    }

    test1
    test2
}
