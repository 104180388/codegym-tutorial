package Entity;

public class Student {
    private String studentId;
    private String name;
    private String className;
    private String email;
    private String phoneNumber;

    public Student(String studentId, String name, String className, String email, String phoneNumber) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getClassName() { return className; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public String toString() {
        return String.format("[ID: %s | Tên: %s | Lớp: %s | Email: %s | Số điện thoại: %s]", studentId, name, className, email, phoneNumber);
    }
}

