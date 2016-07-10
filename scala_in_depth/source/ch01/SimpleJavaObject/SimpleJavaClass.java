public class SimpleJavaClass {
    private String name;
    public SimpleJavaClass(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static SimpleJavaClass create(String name) {
        return new SimpleJavaClass(name);
    }
}
