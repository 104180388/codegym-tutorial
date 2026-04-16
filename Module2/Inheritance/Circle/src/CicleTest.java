public class CicleTest {
    public static void main(String[] args) {
        Circle circle = new Circle();
        circle.setRadius(5.0f);
        circle.setColor("Red");
        System.out.println("Circle radius: " + circle.getRadius());
        System.out.println("Circle color: " + circle.getColor());
        System.out.println("Circle area: " + circle.getArea());

        Cylinder cylinder = new Cylinder();
        cylinder.setRadius(4.0f);
        cylinder.setHeight(12.0f);
        cylinder.setColor("Blue");
        System.out.println("\nCylinder radius: " + cylinder.getRadius());
        System.out.println("Cylinder height: " + cylinder.getHeight());
        System.out.println("Cylinder color: " + cylinder.getColor());
        System.out.println("Cylinder volume: " + cylinder.getVolume());
    }
}
