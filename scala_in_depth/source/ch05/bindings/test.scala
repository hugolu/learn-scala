package test

object Test {
    def main(args: Array[String]) {
        testSamePackage()
        testWildcardImport()
        testExplicitImport()
        testInlineDefinition()
    }

    def testSamePackage() = {
        println(x)
    }

    object Wildcard {
        def x = "Wildcard Import x"
    }
    def testWildcardImport() = {
        import Wildcard._
        println(x)
    }

    object Explicit {
        def x = "Explicit Import x"
    }
    def testExplicitImport() = {
        import Explicit.x
        import Wildcard._
        println(x)
    }

    def testInlineDefinition() = {
        val x = "Inline definition x"
        import Explicit.x
        import Wildcard._
        println(x)
    }
}
