package Entity;

public class StudentFactory {
    private static int counter = 1;

    public static Student createStudent(String name, String className) {
        String maHocSinh;

        String classNameUpperCase = className.toUpperCase();
        if (classNameUpperCase.contains("TOÁN") || classNameUpperCase.contains("TOAN")) {
            maHocSinh = "TOAN-" + counter;
        } else if (classNameUpperCase.contains("TIN")) {
            maHocSinh = "TIN-" + counter;
        } else {
            maHocSinh = "HS-" + counter;
        }

        counter++;

        return new Student(maHocSinh, name, className);
    }
}
