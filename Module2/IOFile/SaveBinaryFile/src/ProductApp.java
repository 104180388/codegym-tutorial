import java.io.*;
import java.util.*;

public class ProductApp {
    private static final String FILE_NAME = "products.bin";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n--- HỆ THỐNG QUẢN LÝ SẢN PHẨM (NHỊ PHÂN) ---");
            System.out.println("1. Thêm sản phẩm mới");
            System.out.println("2. Hiển thị tất cả sản phẩm");
            System.out.println("3. Tìm kiếm sản phẩm theo tên");
            System.out.println("4. Thoát");
            System.out.print("Chọn chức năng (1-4): ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1: addProduct(); break;
                case 2: showAllProducts(); break;
                case 3: searchProduct(); break;
                case 4: System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (choice != 4);
    }

    // Chức năng 1: Ghi thêm đối tượng vào file nhị phân
    private static void addProduct() {
        List<Product> list = readFromFile();

        System.out.print("Mã sản phẩm: "); String id = scanner.nextLine();
        System.out.print("Tên sản phẩm: "); String name = scanner.nextLine();
        System.out.print("Giá: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Hãng sản xuất: "); String brand = scanner.nextLine();
        System.out.print("Mô tả: "); String desc = scanner.nextLine();

        list.add(new Product(id, name, price, brand, desc));
        saveToFile(list);
        System.out.println("Đã lưu sản phẩm vào file nhị phân!");
    }

    // Chức năng 2: Đọc file nhị phân và hiển thị
    private static void showAllProducts() {
        List<Product> list = readFromFile();
        if (list.isEmpty()) {
            System.out.println("File dữ liệu trống.");
        } else {
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("| Mã SP      | Tên Sản Phẩm         | Giá        | Hãng SX         | Mô tả            |");
            System.out.println("----------------------------------------------------------------------------------");
            for (Product p : list) System.out.println(p);
            System.out.println("----------------------------------------------------------------------------------");
        }
    }

    // Chức năng 3: Tìm kiếm trong danh sách đã đọc từ file
    private static void searchProduct() {
        System.out.print("Nhập tên sản phẩm muốn tìm: ");
        String keyword = scanner.nextLine().toLowerCase();
        List<Product> list = readFromFile();
        boolean found = false;

        for (Product p : list) {
            if (p.getName().toLowerCase().contains(keyword)) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) System.out.println("Không tìm thấy kết quả nào.");
    }

    // --- LOGIC XỬ LÝ FILE (Kế thừa OutputStream) ---
    private static void saveToFile(List<Product> list) {
        // FileOutputStream là lớp dẫn xuất từ OutputStream
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    // --- LOGIC XỬ LÝ FILE (Kế thừa InputStream) ---
    private static List<Product> readFromFile() {
        List<Product> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;

        // FileInputStream là lớp dẫn xuất từ InputStream
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            list = (List<Product>) ois.readObject();
        } catch (EOFException e) {
            // Hết file, không cần xử lý
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        }
        return list;
    }
}