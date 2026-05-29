package Entity;

public class StudentFactory {
    private static int counter = 1;

    public static Student createStudent(String name, String dateOfBirth, String gender, String phoneNumber, String className) {
        String studentId = "HS-" + counter++;
        return new Student(studentId, name, dateOfBirth, gender, phoneNumber, className);
    }
}