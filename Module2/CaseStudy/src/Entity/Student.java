package Entity;

class Student {
    private String name;
    private String studentId;
    private String className;

    public Student(String name, String studentId, String className) {
        this.name = name;
        this.studentId = studentId;
        this.className = className;
    }

    // Getters
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getClassName() { return className; }

    @Override
    public String toString() {
        return String.format("[ID: %s | Tên: %s | Lớp: %s]", studentId, name, className);
    }
}
