object FunctionUtil {
    def testFunction(f: Int => Int) = f(5)
}

abstract class AbstractFunctionIntIntForJava extends (Int => Int) {
}
