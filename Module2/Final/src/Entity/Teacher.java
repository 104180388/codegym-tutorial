package Entity;

public class Teacher extends Person {
    private String teacherId;
    public Teacher(String teacherId, String name, String dateOfBirth, String gender, String phoneNumber) {
        super(name, dateOfBirth, gender, phoneNumber);
        this.teacherId = teacherId;
    }

    public String getTeacherId() { return teacherId; }

    @Override
    public String toString() {
        return String.format("[GV ID: %s | Tên: %s | Ngày sinh: %s | Giới tính: %s | Số điện thoại: %s]",
                teacherId, getName(), getDateOfBirth(), getGender(), getPhoneNumber());
    }
}