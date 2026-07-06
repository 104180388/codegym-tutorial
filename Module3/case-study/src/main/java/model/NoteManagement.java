package model;

import java.util.List;

public class NoteManagement {

    // 1. Thuộc tính private kiểu Interface CỦA CHÚNG TA (Đúng UML)
    private Note note;

    public NoteManagement() {
        this.note = new NoteDB(); // Mặc định ban đầu chạy với DB
    }

    // Hàm khởi tạo linh hoạt để truyền chiến lược từ ngoài vào (Dependency Injection)
    public NoteManagement(Note note) {
        this.note = note;
    }

    // Hàm thay đổi chiến lược lưu trữ khi ứng dụng đang chạy (Runtime)
    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * Thêm mới một Ghi chú (Đúng UML)
     */
    public void addNote(String title, String content, int typeId) {
            NoteDB dbNote = new NoteDB(0, title, content, typeId);
            dbNote.save(); // Gọi hàm save() đa hình từ chiến lược
    }

    /**
     * Xóa một Ghi chú theo ID (Đúng UML)
     */
    public void removeNote(int noteId) {
            NoteDB dbNote = new NoteDB();
            dbNote.setId(noteId);
            dbNote.delete(); // Gọi hàm delete() cấu hình trong NoteDB
    }

    /**
     * Tìm kiếm ghi chú và trả về mảng Note[] (Đúng UML)
     */
    public Note[] searchNotes(String keyword, String typeIdStr) {
        if (this.note == null) {
            return new Note[0];
        }

        if (this.note instanceof NoteDB) {
            List<NoteDB> dbList = NoteDB.searchInDB(keyword, typeIdStr); // Truyền thêm tham số ở đây
            return dbList.toArray(new Note[0]);
        }


        return new Note[0];
    }

    public boolean changeNoteStore(String storeType) {

        if ("DB".equalsIgnoreCase(storeType)) {
            this.note = new NoteDB();
            return true;
        }

        // Nếu truyền vào giá trị khác (không hợp lệ), hủy cấu hình lưu trữ
        this.note = null;
        return false;
    }

    public Note getNote() {
        return this.note;
    }
}