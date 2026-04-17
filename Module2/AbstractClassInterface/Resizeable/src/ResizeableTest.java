public class ResizeableTest {
    public static void main(String[] args) {
        Resizeable resizeable = new Circle(3.5, "indigo", false);
        System.out.println(resizeable);
        resizeable.resize(50);
        System.out.println(resizeable);

        resizeable = new Rectangle(2.5, 3.8, "orange", true);
        System.out.println(resizeable);
        resizeable.resize(50);
        System.out.println(resizeable);
    }
}
