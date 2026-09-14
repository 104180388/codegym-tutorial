package com.example.module4test.service;

import com.example.module4test.dto.GiaoDichDto;
import com.example.module4test.entity.GiaoDich;

import java.util.List;
import java.util.Optional;

public interface GiaoDichService {
    List<GiaoDich> findAll();
    Optional<GiaoDich> findById(String maGiaoDich);
    boolean existsById(String maGiaoDich);
    GiaoDich save(GiaoDichDto dto);
    GiaoDich saveEntity(GiaoDich giaoDich);
    void deleteById(String maGiaoDich);
    List<GiaoDich> search(String tenKhachHang, String loaiDichVu);
}
