package com.example.module4test.repository;

import com.example.module4test.entity.GiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiaoDichRepository extends JpaRepository<GiaoDich, String> {

    @Query("SELECT g FROM GiaoDich g JOIN FETCH g.khachHang ORDER BY g.maGiaoDich ASC")
    List<GiaoDich> findAllWithKhachHang();

    @Query("SELECT g FROM GiaoDich g JOIN FETCH g.khachHang WHERE g.maGiaoDich = :maGiaoDich")
    Optional<GiaoDich> findByIdWithKhachHang(@Param("maGiaoDich") String maGiaoDich);

    @Query("SELECT g FROM GiaoDich g JOIN FETCH g.khachHang k WHERE " +
           "(:tenKhachHang IS NULL OR :tenKhachHang = '' OR LOWER(k.tenKhachHang) LIKE LOWER(CONCAT('%', :tenKhachHang, '%'))) AND " +
           "(:loaiDichVu IS NULL OR :loaiDichVu = '' OR g.loaiDichVu = :loaiDichVu) " +
           "ORDER BY g.maGiaoDich ASC")
    List<GiaoDich> search(@Param("tenKhachHang") String tenKhachHang, @Param("loaiDichVu") String loaiDichVu);
}
