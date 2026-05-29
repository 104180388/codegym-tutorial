package Entity;

public class Student extends Person {
    private String studentId;
    private String className;

    public Student(String studentId, String name, String dateOfBirth, String gender, String phoneNumber, String className) {
        super(name, dateOfBirth, gender, phoneNumber);
        this.studentId = studentId;
        this.className = className;
    }

    public String getStudentId() { return studentId; }
    public String getClassName() { return className; }

    @Override
    public String toString() {
        return String.format("[ID: %s | Tên: %s | Ngày sinh: %s | Giới tính: %s | Số điện thoại: %s | Lớp: %s]",
                studentId, getName(), getDateOfBirth(), getGender(), getPhoneNumber(), className);
    }
}