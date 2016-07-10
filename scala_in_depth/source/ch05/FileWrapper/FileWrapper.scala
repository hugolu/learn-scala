class FileWrapper(val file: java.io.File) {
    def /(next: String) = new FileWrapper(new java.io.File(file, next))
    override def toString = file.getCanonicalPath
}

object FileWrapper {
    implicit def wrap(file: java.io.File) = new FileWrapper(file)
    implicit def unwrap(wrapper: FileWrapper) = wrapper.file
}
