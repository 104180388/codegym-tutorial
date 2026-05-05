import java.util.Scanner;

public class TriangleApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Nhập cạnh a: ");
            double a = scanner.nextDouble();
            System.out.print("Nhập cạnh b: ");
            double b = scanner.nextDouble();
            System.out.print("Nhập cạnh c: ");
            double c = scanner.nextDouble();
            checkTriangle(a, b, c);
            System.out.println("Chúc mừng! Ba số " + a + ", " + b + ", " + c + " tạo thành một tam giác.");

        } catch (IllegalTriangleException e) {
            System.err.println("Lỗi tam giác: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi nhập liệu: Vui lòng nhập số thực hợp lệ.");
        } finally {
            scanner.close();
        }
    }

    public static void checkTriangle(double a, double b, double c) throws IllegalTriangleException {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalTriangleException("Các cạnh của tam giác phải là số dương.");
        }

        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalTriangleException("Tổng hai cạnh phải lớn hơn cạnh còn lại (Bất đẳng thức tam giác).");
        }
    }
}