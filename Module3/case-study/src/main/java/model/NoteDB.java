package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class NoteDB implements Note{
    private int id;
    private String title;
    private String content;
    private int typeId;

    public NoteDB() {
    }

    public NoteDB(String title) {
        this.title = title;
    }

    public NoteDB(int id, String title, String content, int typeId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        switch (this.typeId) {
            case 1: return "Công việc";
            case 2: return "Cá nhân";
            case 3: return "Học tập";
            default: return "Không xác định";
        }
    }

    @Override
    public boolean save() {
        String sql;
        boolean isInsert = (this.id == 0); // Xác định xem là thêm mới hay cập nhật

        if (isInsert) {
            sql = "INSERT INTO note (title, content, `type_id`) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE note SET title = ?, content = ?, `type_id` = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.title);
            ps.setString(2, this.content);
            ps.setInt(3, this.typeId);

            if (!isInsert) {
                ps.setInt(4, this.id); // Truyền ID cho mệnh đề WHERE nếu là Update
            }

            int rowAffected = ps.executeUpdate();

            if (isInsert && rowAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                    }
                }
            }

            return rowAffected > 0;

        } catch (Exception e) {
            System.out.println("Lỗi khi save NoteDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete() {
        if (this.id <= 0) {
            System.out.println("Không thể xóa ghi chú vì chưa có ID hợp lệ.");
            return false;
        }

        String sql = "DELETE FROM note WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, this.id);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;

        } catch (Exception e) {
            System.out.println("Lỗi khi delete NoteDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<NoteDB> searchInDB(String keyword, String typeIdStr) {
        List<NoteDB> resultList = new ArrayList<>();

        String sql = "SELECT * FROM note WHERE (title LIKE ? OR content LIKE ?)";

        boolean hasType = (typeIdStr != null && !typeIdStr.isEmpty());
        if (hasType) {
            sql += " AND type_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            if (hasType) {
                ps.setInt(3, Integer.parseInt(typeIdStr)); // Truyền type_id vào dấu hỏi số 3
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NoteDB n = new NoteDB(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getInt("type_id")
                    );
                    resultList.add(n);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi tìm kiếm dữ liệu trong DB: " + e.getMessage());
            e.printStackTrace();
        }
        return resultList;
    }
}
