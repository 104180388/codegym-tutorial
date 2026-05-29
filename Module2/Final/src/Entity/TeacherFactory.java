package Entity;

public class TeacherFactory {
    private static int counter = 1;

    public static Teacher createTeacher(String name, String dateOfBirth, String gender, String phoneNumber) {
        String teacherId = "GV-" + counter++;
        return new Teacher(teacherId, name, dateOfBirth, gender, phoneNumber);
    }
}