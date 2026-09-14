package com.example.module4test.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class GiaoDichDto {

    @NotBlank(message = "Mã giao dịch không được để trống")
    @Pattern(regexp = "^MGD-\\d{4}$", message = "Mã giao dịch phải có định dạng MGD-XXXX (XXXX là 4 chữ số từ 0-9)")
    private String maGiaoDich;

    @NotBlank(message = "Vui lòng chọn khách hàng")
    private String maKhachHang;

    @NotNull(message = "Ngày giao dịch không được để trống")
    @Future(message = "Ngày giao dịch phải là ngày/tháng/năm và phải lớn hơn thời gian hiện tại")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayGiaoDich;

    @NotBlank(message = "Vui lòng chọn loại dịch vụ")
    private String loaiDichVu;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "500000.01", inclusive = true, message = "Đơn giá phải là số và phải lớn hơn 500.000 (VND)")
    private Double donGia;

    @NotNull(message = "Diện tích không được để trống")
    @DecimalMin(value = "20.01", inclusive = true, message = "Diện tích phải là số và phải lớn hơn 20 (m²)")
    private Double dienTich;

    public GiaoDichDto() {
    }

    public GiaoDichDto(String maGiaoDich, String maKhachHang, LocalDate ngayGiaoDich, String loaiDichVu, Double donGia, Double dienTich) {
        this.maGiaoDich = maGiaoDich;
        this.maKhachHang = maKhachHang;
        this.ngayGiaoDich = ngayGiaoDich;
        this.loaiDichVu = loaiDichVu;
        this.donGia = donGia;
        this.dienTich = dienTich;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public LocalDate getNgayGiaoDich() {
        return ngayGiaoDich;
    }

    public void setNgayGiaoDich(LocalDate ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public String getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }

    public Double getDienTich() {
        return dienTich;
    }

    public void setDienTich(Double dienTich) {
        this.dienTich = dienTich;
    }
}
