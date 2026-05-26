import Entity.Student;
import Entity.StudentFactory;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class StudentService {
    private static StudentService instance;
    private LinkedList<Student> studentList = new LinkedList<>();

    private Map<String, Student> idMap = new HashMap<>();
    private Map<String, List<Student>> nameMap = new HashMap<>();
    private Map<String, List<Student>> classMap = new HashMap<>();

    private StudentService() {}

    public static StudentService getInstance() {
        if (instance == null) instance = new StudentService();
        return instance;
    }

    public void define(String name, String className) {
        Student s = StudentFactory.createStudent(name, className);

        int index = 0;
        for (Student existing : studentList) {
            if (s.getName().compareToIgnoreCase(existing.getName()) < 0) break;
            index++;
        }
        studentList.add(index, s);

        idMap.put(s.getStudentId(), s);
        nameMap.computeIfAbsent(s.getName(), k -> new ArrayList<>()).add(s);
        classMap.computeIfAbsent(s.getClassName(), k -> new ArrayList<>()).add(s);

        System.out.println("Đã thêm thành công: " + s);
    }

    public List<Student> lookup(String type, String keyword) {
        if (type.equals("id")) {
            Student s = idMap.get(keyword);
            return s != null ? List.of(s) : Collections.emptyList();
        }
        if (type.equals("name")) return nameMap.getOrDefault(keyword, Collections.emptyList());
        if (type.equals("class")) return classMap.getOrDefault(keyword, Collections.emptyList());
        return Collections.emptyList();
    }

    public boolean drop(String id) {
        Student s = idMap.remove(id);
        if (s != null) {
            studentList.remove(s);
            nameMap.get(s.getName()).remove(s);
            classMap.get(s.getClassName()).remove(s);
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
            writer.println("========== DANH SÁCH HỌC SINH ==========");
            writer.printf("%-10s | %-20s | %-10s%n", "Mã HS", "Họ tên", "Lớp");
            writer.println("----------------------------------------");

            for (Entity.Student s : studentList) {
                writer.printf("%-10s | %-20s | %-10s%n",
                        s.getStudentId(), s.getName(), s.getClassName());
            }

            System.out.println("Đã xuất danh sách ra file: " + filename);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    public void clearData() {
        studentList.clear();
        idMap.clear();
        nameMap.clear();
        classMap.clear();
    }

    public LinkedList<Entity.Student> getStudentList() {
        return studentList;
    }
}