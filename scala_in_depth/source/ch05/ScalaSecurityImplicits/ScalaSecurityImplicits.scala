import java.security._

object ScalaSecurityImplicits {
    implicit def functionToPrivilegedAction[A](func: Function0[A]) =
        new PrivilegedAction[A] {
            override def run() = func()
        }
}
