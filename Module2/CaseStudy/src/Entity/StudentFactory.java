package Entity;

public class StudentFactory {
    private static int counter = 1;

    public static Student createStudent(String name, String className, String email, String phoneNumber) {
        String studentId = "HS-" + counter++;
        return new Student(studentId, name, className, email, phoneNumber);
    }
}