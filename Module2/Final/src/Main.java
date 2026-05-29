import java.util.*;

public class Main {
    public static void main(String[] args) {
        StudentController controller = new StudentController();
        Scanner scanner = new Scanner(System.in);

        Map<Integer, String> menuMapping = new HashMap<>();
        menuMapping.put(1, "define");
        menuMapping.put(2, "lookupName");
        menuMapping.put(3, "drop");
        menuMapping.put(4, "displayAll");
        menuMapping.put(5, "export");

        while (true) {
            System.out.println("\n------ QUẢN LÝ HỌC SINH ------");
            System.out.println("1. Thêm học sinh");
            System.out.println("2. Tìm kiếm học sinh theo Tên");
            System.out.println("3. Xóa học sinh(theo ID)");
            System.out.println("4. Hiển thị danh sách");
            System.out.println("5. Xuất danh sách ra file .txt");
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
                StudentService service = StudentService.getInstance();
                System.out.print("Tên: ");
                String name = scanner.nextLine();
                if (!name.matches("^[a-zA-Z\\s]{4,50}$")) {
                    System.out.println("LỖI: Tên phải có từ 4-50 kí tự");
                    continue;
                }

                System.out.print("Lớp: ");
                String className = scanner.nextLine();
                if (!className.matches("^(10|11|12)[A-E]$")) {
                    System.out.println("LỖI: Khối (10-12) + Chữ cái (A-E). Ví dụ: 11A.");
                    continue;
                }

                System.out.print("Ngày sinh (Ví dụ: 01/01/2000): ");
                String dateOfBirth = scanner.nextLine();
                if (!dateOfBirth.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
                    System.out.println("LỖI: Ngày sinh không hợp lệ.");
                    continue;
                }

                System.out.print("Giới tính (Nam/Nữ): ");
                String gender = scanner.nextLine();
                if (!gender.matches("^(Nam|Nữ)$")) {
                    System.out.println("LỖI: Giới tính không hợp lệ.");
                    continue;
                }

                System.out.print("Số điện thoại (10 số): ");
                String phoneNumber = scanner.nextLine();
                if (!phoneNumber.matches("^09[01]\\d{7}$")) {
                    System.out.println("LỖI: Số điện thoại phải có 10 chữ số và bắt đầu bằng 090 hoặc 091.");
                    continue;
                }
                if (service.isPhoneExists(phoneNumber)) {
                    System.out.println("LỖI: Số điện thoại này đã tồn tại");
                    continue;
                }

                request = new Request(action, null, Map.of("name", name, "className", className, "dateOfBirth", dateOfBirth, "gender", gender, "phoneNumber", phoneNumber));
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
                System.out.print("Nhập thông tin học sinh: ");
                String key = scanner.nextLine();
                request = new Request(action, key, null);
            }

            controller.execute(request);
        }
    }
}