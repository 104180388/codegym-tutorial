import java.util.*;

public class Main {
    public static void main(String[] args) {
        StudentController controller = new StudentController();
        Scanner scanner = new Scanner(System.in);

        Map<Integer, String> menuMapping = new HashMap<>();
        menuMapping.put(1, "define");
        menuMapping.put(2, "lookupId");
        menuMapping.put(3, "lookupName");
        menuMapping.put(4, "lookupClass");
        menuMapping.put(5, "drop");
        menuMapping.put(6, "displayAll");
        menuMapping.put(7, "export");

        while (true) {
            System.out.println("\n========= QUẢN LÝ HỌC SINH =========");
            System.out.println("1. Thêm học sinh");
            System.out.println("2. Tìm kiếm theo ID");
            System.out.println("3. Tìm kiếm theo Tên");
            System.out.println("4. Tìm kiếm theo Lớp");
            System.out.println("5. Xóa học sinh");
            System.out.println("6. Hiển thị danh sách");
            System.out.println("7. Xuất danh sách ra file .txt");
            System.out.println("0. Thoát");
            System.out.print("Lựa chọn: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) break;

            String action = menuMapping.get(choice);
            if (action == null) {
                System.out.println("Lựa chọn không hợp lệ!");
                continue;
            }

            Request request = null;

            if (action.equals("define")) {
                System.out.print("Tên: "); String name = scanner.nextLine();
                System.out.print("Lớp: "); String className = scanner.nextLine();
                request = new Request(action, null, Map.of("name", name, "className", className));
            }
            else if (action.equals("export")) {
                System.out.print("Nhập tên file (VD: students.txt): ");
                String filename = scanner.nextLine();
                request = new Request(action, null, Map.of("filename", filename));
            }
            else if (action.equals("displayAll")) {
                request = new Request(action, null, null);
            }
            else {
                System.out.print("Nhập từ khóa: ");
                String key = scanner.nextLine();
                request = new Request(action, key, null);
            }

            controller.execute(request);
        }
    }
}