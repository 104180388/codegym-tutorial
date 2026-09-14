package com.example.module4test.service;

import com.example.module4test.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangService {
    List<KhachHang> findAll();
    Optional<KhachHang> findById(String maKhachHang);
    KhachHang save(KhachHang khachHang);
}
