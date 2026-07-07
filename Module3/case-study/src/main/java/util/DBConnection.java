package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inote?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "myauthsql8659";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Lỗi: Không tìm thấy thư viện MySQL JDBC Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi: Sai thông tin kết nối hoặc Database chưa được bật!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ CHÚC MỪNG! Kết nối MySQL thành công!");
        } else {
            System.out.println("❌ Kết nối thất bại, hãy kiểm tra lại URL, Username hoặc Password.");
        }
    }
}
