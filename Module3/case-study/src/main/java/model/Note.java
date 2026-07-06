package model;

public interface Note {

    /**
     * Phương thức lưu ghi chú
     * @return true nếu thành công, false nếu thất bại
     */
    boolean save();

    /**
     * Phương thức xóa ghi chú
     * @return true nếu thành công, false nếu thất bại
     */
    boolean delete();
}