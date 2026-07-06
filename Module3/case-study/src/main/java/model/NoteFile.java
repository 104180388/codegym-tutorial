package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lớp NoteFile triển khai chiến lược lưu trữ dữ liệu vào tệp tin JSON.
 */
public class NoteFile implements Note {

    // Đường dẫn lưu file tệp tin (Lưu ngay tại thư mục gốc của dự án)
    private static final String FILE_PATH = "notes.json";

    // Các thuộc tính bắt buộc của một Ghi chú
    private int id;
    private int typeId;
    private String title;
    private String content;

    // --- Các Hàm khởi tạo (Constructors) ---
    public NoteFile() {
    }

    public NoteFile(int id, String title, String content, int typeId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.typeId = typeId;
    }

    // --- Các hàm Getter / Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    /**
     * Chuyển đổi mã typeId thành chuỗi tên hiển thị giống mẫu ảnh
     */
    public String getType() {
        switch (this.typeId) {
            case 1: return "Công ty";
            case 2: return "Cá nhân";
            case 3: return "Học tập";
            default: return "Cá nhân";
        }
    }

    /**
     * Chuyển đổi ngược từ chuỗi tên trong file thành mã typeId
     */
    private void setTypeIdByString(String typeStr) {
        if ("Công ty".equals(typeStr)) this.typeId = 1;
        else if ("Cá nhân".equals(typeStr)) this.typeId = 2;
        else if ("Học tập".equals(typeStr)) this.typeId = 3;
        else this.typeId = 2; // Mặc định
    }

    // =================================================================
    // TRAO ĐỔI DỮ LIỆU VỚI TỆP TIN (HÀM BỔ TRỢ)
    // =================================================================

    /**
     * Đọc toàn bộ tệp tin notes.json và phân tích thành danh sách đối tượng NoteFile
     */
    private List<NoteFile> loadAllNotesFromFile() {
        List<NoteFile> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list; // Nếu file chưa tồn tại, trả về danh sách rỗng
        }

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sử dụng Regex để quét dữ liệu cấu trúc JSON trong ảnh một cách chính xác
        String json = jsonBuilder.toString();
        Pattern pattern = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*(\\d+)\\s*,\\s*\"title\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"content\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"type\"\\s*:\\s*\"([^\"]*)\"\\s*\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            int noteId = Integer.parseInt(matcher.group(1));
            String noteTitle = matcher.group(2).replace("\\\"", "\"");
            String noteContent = matcher.group(3).replace("\\\"", "\"");
            String noteTypeStr = matcher.group(4);

            NoteFile nf = new NoteFile();
            nf.setId(noteId);
            nf.setTitle(noteTitle);
            nf.setContent(noteContent);
            nf.setTypeIdByString(noteTypeStr);
            list.add(nf);
        }
        return list;
    }

    /**
     * Ghi đè toàn bộ danh sách đối tượng NoteFile vào tệp tin theo đúng mẫu JSON
     */
    private void saveAllNotesToFile(List<NoteFile> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"Notes\": [\n");
        for (int i = 0; i < list.size(); i++) {
            NoteFile n = list.get(i);
            sb.append("    {\n");
            sb.append("      \"id\": ").append(n.getId()).append(",\n");
            sb.append("      \"title\": \"").append(n.getTitle().replace("\"", "\\\"")).append("\",\n");
            sb.append("      \"content\": \"").append(n.getContent().replace("\"", "\\\"")).append("\",\n");
            sb.append("      \"type\": \"").append(n.getType()).append("\"\n");
            sb.append("    }");
            if (i < list.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n}");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), "UTF-8"))) {
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =================================================================
    // TRIỂN KHAI PHƯƠNG THỨC INTERFACE NOTE (STRATEGY PATTERN)
    // =================================================================

    /**
     * Thực hiện thêm mới hoặc sửa ghi chú trong tệp tin
     */
    @Override
    public boolean save() {
        try {
            List<NoteFile> allNotes = loadAllNotesFromFile();

            if (this.id == 0) {
                // HÀNH VI: THÊM MỚI (Tự tìm ID lớn nhất rồi cộng 1)
                int maxId = 0;
                for (NoteFile n : allNotes) {
                    if (n.getId() > maxId) {
                        maxId = n.getId();
                    }
                }
                this.id = maxId + 1; // Gán ID mới tự động tăng
                allNotes.add(this);  // Thêm chính đối tượng này vào danh sách
                System.out.println("📝 Đang ghi thêm ghi chú mới vào File JSON...");
            } else {
                // HÀNH VI: CẬP NHẬT (SỬA)
                boolean found = false;
                for (NoteFile n : allNotes) {
                    if (n.getId() == this.id) {
                        n.setTitle(this.title);
                        n.setContent(this.content);
                        n.setTypeId(this.typeId);
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
                System.out.println("✏️ Đang cập nhật ghi chú ID " + this.id + " trong File JSON...");
            }

            // Lưu lại toàn bộ danh sách đã chỉnh sửa vào file
            saveAllNotesToFile(allNotes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thực hiện xóa ghi chú khỏi tệp tin dựa vào ID
     */
    @Override
    public boolean delete() {
        if (this.id <= 0) return false;

        try {
            List<NoteFile> allNotes = loadAllNotesFromFile();
            boolean removed = false;

            // Tìm phần tử trùng khớp ID để xóa bỏ khỏi List
            for (int i = 0; i < allNotes.size(); i++) {
                if (allNotes.get(i).getId() == this.id) {
                    allNotes.remove(i);
                    removed = true;
                    break;
                }
            }

            if (removed) {
                System.out.println("🗑️ Đang xóa ghi chú ID " + this.id + " khỏi File JSON...");
                saveAllNotesToFile(allNotes); // Cập nhật lại file sau khi xóa
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<NoteFile> searchInFile(String keyword) {
        List<NoteFile> resultList = new ArrayList<>();
        NoteFile tempInstance = new NoteFile();

        // Gọi lại hàm loadAllNotesFromFile() chúng ta đã viết ở Bước 8 để lấy toàn bộ danh sách từ file JSON
        List<NoteFile> allNotes = tempInstance.loadAllNotesFromFile();

        // Lọc danh sách: Chỉ lấy những ghi chú có chứa từ khóa trong tiêu đề hoặc nội dung
        for (NoteFile n : allNotes) {
            if (n.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    n.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                resultList.add(n);
            }
        }
        return resultList;
    }
}
