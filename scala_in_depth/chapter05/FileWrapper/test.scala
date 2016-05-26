import FileWrapper.wrap

object Test extends App {
    val cur = new java.io.File(".")
    println(cur / "temp.txt")

    def useFile(file : java.io.File) = println(file.getCanonicalPath)
    useFile(cur / "temp.txt")
}
