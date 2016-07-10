import ScalaSecurityImplicits._
import java.security._

object Test extends App {
    AccessController.doPrivileged( () => println("This is privileged.") )
}
