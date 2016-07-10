object MatrixUtils {
    def multiply(a: Matrix, b: Matrix)(implicit threading: ThreadStrategy = SameThreadStrategy): Matrix = {

        assert(a.colRank == b.rowRank)

        val buffer = new Array[Array[Double]](a.rowRank)
        for (i <- 0 until a.rowRank) {
            buffer(i) = new Array[Double](b.colRank)
        }

        def computeValue(row: Int, col: Int): Unit = {
            val pairwiseElements = a.row(row).zip(b.col(col))
            val products = for ((x,y) <- pairwiseElements) yield x*y
            val result = products.sum
            buffer(row)(col) = result
        }

        val computations = for {
            i <- 0 until a.rowRank
            j <- 0 until b.colRank
        } yield threading.execute { () => computeValue(i, j) }

        computations.foreach(_())
        new Matrix(buffer)
    }
}

trait ThreadStrategy {
    def execute[A](func: Function0[A]): Function0[A]
}

/* Simple Strategy */
object SameThreadStrategy extends ThreadStrategy {
    def execute[A](func: Function0[A]) = func
}

/* Concurrent Strategy */

import java.util.concurrent.{Callable, Executors}

object ThreadPoolStrategy extends ThreadStrategy {
    val pool = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime.availableProcessors)

    def execute[A](func: Function0[A]) = {
        val future = pool.submit(new Callable[A] {
            def call(): A = {
                Console.println("Executing function on threads: " +
                    Thread.currentThread.getName)
                func()
            }
        })
        () => future.get()
    }
}
