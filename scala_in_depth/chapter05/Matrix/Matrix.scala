import scala.collection.mutable.ArrayBuffer

class Matrix(private val repr: Array[Array[Double]]) {
    def row(idx: Int): Seq[Double] = {
        repr(idx)
    }

    def col(idx: Int): Seq[Double] = {
        repr.foldLeft(ArrayBuffer[Double]()) { (buffer, currentRow) =>
            buffer.append(currentRow(idx))
            buffer
        } toArray
    }

    lazy val rowRank = repr.size
    lazy val colRank = if (rowRank > 0) repr(0).size else 0

    override def toString = "Matrix" + repr.foldLeft("") {
        (msg, row) => msg + row.mkString("\n|", " | ", "|")
    }
}
