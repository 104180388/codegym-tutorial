import java.util.*;

public class ProductManager {
    // Bước 4: Thay ArrayList bằng LinkedList ở đây nếu cần
    private List<Product> productList = new ArrayList<>();

    // 1. Thêm sản phẩm
    public void addProduct(Product p) {
        productList.add(p);
    }

    // 2. Sửa thông tin theo ID
    public void editProduct(int id, String newName, double newPrice) {
        for (Product p : productList) {
            if (p.getId() == id) {
                p.setName(newName);
                p.setPrice(newPrice);
                return;
            }
        }
        System.out.println("Không tìm thấy sản phẩm ID: " + id);
    }

    // 3. Xóa sản phẩm theo ID
    public void deleteProduct(int id) {
        productList.removeIf(p -> p.getId() == id);
    }

    // 4. Hiển thị danh sách
    public void displayProducts() {
        for (Product p : productList) {
            System.out.println(p);
        }
    }

    // 5. Tìm kiếm theo tên
    public void searchByName(String name) {
        for (Product p : productList) {
            if (p.getName().equalsIgnoreCase(name)) {
                System.out.println(p);
            }
        }
    }

    // 6. Sắp xếp tăng dần theo giá
    public void sortAscending() {
        Collections.sort(productList, Comparator.comparingDouble(Product::getPrice));
    }

    // 7. Sắp xếp giảm dần theo giá
    public void sortDescending() {
        productList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
    }
}