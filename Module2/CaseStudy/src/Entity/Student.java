package Entity;

public class Student {
    private String name;
    private String studentId;
    private String className;

    protected Student(String name, String id, String className) {
        this.name = name;
        this.studentId = id;
        this.className = className;
    }

    public void hienThiThongTin() {
        System.out.println("Name: " + name + " | StudentId: " + studentId + " | Class: " + className);
    }
}
