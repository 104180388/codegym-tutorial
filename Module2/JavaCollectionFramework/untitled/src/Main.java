public class Main {
    public static void main(String[] args) {
        ProductManager pm = new ProductManager();

        pm.addProduct(new Product(1, "iPhone 15", 2000));
        pm.addProduct(new Product(2, "Samsung S24", 1800));
        pm.addProduct(new Product(3, "Xiaomi 14", 1200));

        System.out.println("--- Danh sách ban đầu ---");
        pm.displayProducts();

        System.out.println("\n--- Sau khi sắp xếp giá tăng dần ---");
        pm.sortAscending();
        pm.displayProducts();

        System.out.println("\n--- Tìm kiếm sản phẩm 'iPhone 15' ---");
        pm.searchByName("iPhone 15");
    }
}