import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L; // Đảm bảo đồng bộ hóa phiên bản file
    private String id;
    private String name;
    private double price;
    private String manufacturer;
    private String description;

    public Product(String id, String name, double price, String manufacturer, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.description = description;
    }

    // Getters để hỗ trợ tìm kiếm và hiển thị
    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return String.format("| %-10s | %-20s | %-10.2f | %-15s | %-20s |",
                id, name, price, manufacturer, description);
    }
}