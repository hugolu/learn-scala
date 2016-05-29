object Test extends App {
    def method(implicit x: foo.Foo) = println(x)
    method
}
