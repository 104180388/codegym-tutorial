public class TriangleClassifier {
    public static String triangleType(int a, int b, int c) {
        if (a + b > c && a + c > b && b + c > a) {
            if (a == b && b == c) {
                return "Equilateral triangle";
            }
            else if (a == b || a == c || b == c) {
                return "Isosceles triangle";
            }
            else {
                return "Just a triangle";
            }
        }
        else {
            return "Not a triangle";
        }

    }
}
