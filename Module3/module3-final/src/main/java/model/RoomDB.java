package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class RoomDB implements Room {
    private int maPhongTro;
    private String tenNguoiThue;
    private String soDienThoai;
    private Date ngayBatDau;
    private int maHinhThucThanhToan;
    private String ghiChu;
    private String tenHinhThucThanhToan;

    public RoomDB() {}

    public RoomDB(int maPhongTro, String tenNguoiThue, String soDienThoai, Date ngayBatDau, int maHinhThucThanhToan, String ghiChu) {
        this.maPhongTro = maPhongTro;
        this.tenNguoiThue = tenNguoiThue;
        this.soDienThoai = soDienThoai;
        this.ngayBatDau = ngayBatDau;
        this.maHinhThucThanhToan = maHinhThucThanhToan;
        this.ghiChu = ghiChu;
    }

    public int getMaPhongTro() { return maPhongTro; }
    public void setMaPhongTro(int maPhongTro) { this.maPhongTro = maPhongTro; }
    public String getTenNguoiThue() { return tenNguoiThue; }
    public void setTenNguoiThue(String tenNguoiThue) { this.tenNguoiThue = tenNguoiThue; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public int getMaHinhThucThanhToan() { return maHinhThucThanhToan; }
    public void setMaHinhThucThanhToan(int maHinhThucThanhToan) { this.maHinhThucThanhToan = maHinhThucThanhToan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getTenHinhThucThanhToan() { return tenHinhThucThanhToan; }
    public void setTenHinhThucThanhToan(String tenHinhThucThanhToan) { this.tenHinhThucThanhToan = tenHinhThucThanhToan; }

    public static List<RoomDB> searchInDB(String keyword) {
        List<RoomDB> list = new ArrayList<>();
        String sql = "SELECT p.*, h.TenHinhThuc FROM PhongTro p " +
                "LEFT JOIN HinhThucThanhToan h ON p.MaHinhThucThanhToan = h.MaHinhThuc " +
                "WHERE CONCAT('PT-00', maPhongTro) LIKE ? " +
                "OR p.TenNguoiThue LIKE ? " +
                "OR p.SoDienThoai LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomDB r = new RoomDB(
                            rs.getInt("MaPhongTro"),
                            rs.getString("TenNguoiThue"),
                            rs.getString("SoDienThoai"),
                            rs.getDate("NgayBatDau"),
                            rs.getInt("MaHinhThucThanhToan"),
                            rs.getString("GhiChu")
                    );
                    r.setTenHinhThucThanhToan(rs.getString("TenHinhThuc"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public boolean save() {
        String sql = (this.maPhongTro == 0)
                ? "INSERT INTO PhongTro (TenNguoiThue, SoDienThoai, NgayBatDau, MaHinhThucThanhToan, GhiChu) VALUES (?, ?, ?, ?, ?)"
                : "UPDATE PhongTro SET TenNguoiThue=?, SoDienThoai=?, NgayBatDau=?, MaHinhThucThanhToan=?, GhiChu=? WHERE MaPhongTro=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, this.tenNguoiThue);
            ps.setString(2, this.soDienThoai);
            ps.setDate(3, this.ngayBatDau);
            ps.setInt(4, this.maHinhThucThanhToan);
            ps.setString(5, this.ghiChu);
            if (this.maPhongTro > 0) {
                ps.setInt(6, this.maPhongTro);
            }

            int row = ps.executeUpdate();
            if (this.maPhongTro == 0 && row > 0) {
                try (ResultSet gks = ps.getGeneratedKeys()) {
                    if (gks.next()) this.maPhongTro = gks.getInt(1);
                }
            }
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete() {
        String sql = "DELETE FROM PhongTro WHERE MaPhongTro = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, this.maPhongTro);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMultiple(String[] ids) {
        if (ids == null || ids.length == 0) return false;

        StringBuilder sql = new StringBuilder("DELETE FROM PhongTro WHERE MaPhongTro IN (");
        for (int i = 0; i < ids.length; i++) {
            sql.append("?");
            if (i < ids.length - 1) sql.append(",");
        }
        sql.append(")");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.length; i++) {
                ps.setInt(i + 1, Integer.parseInt(ids[i]));
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}