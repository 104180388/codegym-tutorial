package com.example.module4test.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "giao_dich")
public class GiaoDich {

    @Id
    @Column(name = "ma_giao_dich", length = 50)
    private String maGiaoDich;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ngay_giao_dich", nullable = false)
    private LocalDate ngayGiaoDich;

    @Column(name = "loai_dich_vu", nullable = false, length = 100)
    private String loaiDichVu;

    @Column(name = "don_gia", nullable = false)
    private Double donGia;

    @Column(name = "dien_tich", nullable = false)
    private Double dienTich;

    public GiaoDich() {
    }

    public GiaoDich(String maGiaoDich, KhachHang khachHang, LocalDate ngayGiaoDich, String loaiDichVu, Double donGia, Double dienTich) {
        this.maGiaoDich = maGiaoDich;
        this.khachHang = khachHang;
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

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
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
