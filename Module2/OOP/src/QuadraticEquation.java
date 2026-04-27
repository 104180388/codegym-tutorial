public class QuadraticEquation {
    private double a, b, c; // Nên để private theo chuẩn Đóng gói
    double delta;

    QuadraticEquation(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.delta = (this.b * this.b) - (4 * this.a * this.c);
    }

    public double getDiscriminant() {
        return this.delta;
    }

    public double getRoot1() {
        return (-this.b+Math.sqrt(this.delta))/(2*this.a);
    }

    public double getRoot2() {
        return (-this.b-Math.sqrt(this.delta))/(2*this.a);
    }

}