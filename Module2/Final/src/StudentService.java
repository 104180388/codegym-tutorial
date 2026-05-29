import Entity.Student;
import Entity.StudentFactory;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class StudentService {
    private static StudentService instance;
    private LinkedList<Student> studentList = new LinkedList<>();
    private Map<String, List<Student>> nameMap = new HashMap<>();
    private Map<String, Student> idMap = new HashMap<>();
    private Map<String, Student> phoneMap = new HashMap<>();

    private StudentService() {}

    public static StudentService getInstance() {
        if (instance == null) instance = new StudentService();
        return instance;
    }

    public boolean isPhoneExists(String phoneNumber) {
        return phoneMap.containsKey(phoneNumber);
    }

    public void define(String name, String dateOfBirth, String gender, String phoneNumber, String className) {
        Student s = StudentFactory.createStudent(name, dateOfBirth, gender, phoneNumber, className);

        int index = 0;
        for (Student existing : studentList) {
            if (s.getName().compareToIgnoreCase(existing.getName()) < 0) break;
            index++;
        }
        studentList.add(index, s);
        nameMap.computeIfAbsent(s.getName(), k -> new ArrayList<>()).add(s);
        idMap.put(s.getStudentId(), s);
        phoneMap.put(phoneNumber, s);

        System.out.println("Đã thêm thành công: " + s);
    }

    public List<Student> lookup(String type, String keyword) {
        if (type.equals("name")) return nameMap.getOrDefault(keyword, Collections.emptyList());
        return Collections.emptyList();
    }

    public boolean drop(String id) {
        Student s = idMap.remove(id);
        if (s != null) {
            studentList.remove(s);
            nameMap.get(s.getName()).remove(s);
            return true;
        }
        return false;
    }

    public void displayAll() {
        if (studentList.isEmpty()) {
            System.out.println("Danh sách hiện tại đang trống.");
        } else {
            System.out.println("--- DANH SÁCH HỌC SINH ---");
            studentList.forEach(System.out::println);
            System.out.println("---------------------------------------------");
        }
    }

    public void exportToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("---- DANH SÁCH HỌC SINH ----");
            writer.printf("%-10s | %-25s | %-12s | %-10s | %-15s | %-10s%n",
                    "Mã HS", "Họ tên", "Ngày sinh", "Giới tính", "Số ĐT", "Lớp");
            writer.println("------------------------------------------------------------------------------------------------------------------------");

            for (Entity.Student s : studentList) {
                writer.printf("%-10s | %-25s | %-12s | %-10s | %-15s | %-10s%n",
                        s.getStudentId(),
                        s.getName(),
                        s.getDateOfBirth(),
                        s.getGender(),
                        s.getPhoneNumber(),
                        s.getClassName());
            }
            writer.println("-----------------------------");
            System.out.println("Đã xuất danh sách ra file: " + filename);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

}