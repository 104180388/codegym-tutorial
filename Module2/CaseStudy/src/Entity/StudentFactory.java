package Entity;

public class StudentFactory {
    private static int counter = 1;

    public static Student createStudent(String name, String className) {
        String studentId;
        studentId = "HS-" + counter;
        counter++;
        return new Student(studentId, name, className);
    }
}
