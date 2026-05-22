package Entity;

public class Student {
    private String studentId;
    private String name;
    private String className;

    public Student(String studentId, String name, String className) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getClassName() { return className; }

    @Override
    public String toString() {
        return String.format("[ID: %s | Tên: %s | Lớp: %s]", studentId, name, className);
    }
}

