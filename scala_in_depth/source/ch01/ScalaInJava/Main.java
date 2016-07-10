public class Main {
    public static void main(String[] args) {
        ScalaUtils.log("hello");
        ScalaUtils$.MODULE$.log("hello");

        System.out.println(ScalaUtils$.MODULE$.MAX_LOG_SIZE());
        System.out.println(ScalaUtils.MAX_LOG_SIZE());
    }
}
