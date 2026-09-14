package com.example.module4test.service.impl;

import com.example.module4test.dto.GiaoDichDto;
import com.example.module4test.entity.GiaoDich;
import com.example.module4test.entity.KhachHang;
import com.example.module4test.repository.GiaoDichRepository;
import com.example.module4test.repository.KhachHangRepository;
import com.example.module4test.service.GiaoDichService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GiaoDichServiceImpl implements GiaoDichService {

    private final GiaoDichRepository giaoDichRepository;
    private final KhachHangRepository khachHangRepository;

    public GiaoDichServiceImpl(GiaoDichRepository giaoDichRepository, KhachHangRepository khachHangRepository) {
        this.giaoDichRepository = giaoDichRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiaoDich> findAll() {
        return giaoDichRepository.findAllWithKhachHang();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GiaoDich> findById(String maGiaoDich) {
        return giaoDichRepository.findByIdWithKhachHang(maGiaoDich);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String maGiaoDich) {
        return giaoDichRepository.existsById(maGiaoDich);
    }

    @Override
    public GiaoDich save(GiaoDichDto dto) {
        KhachHang khachHang = khachHangRepository.findById(dto.getMaKhachHang())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với mã: " + dto.getMaKhachHang()));

        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setMaGiaoDich(dto.getMaGiaoDich());
        giaoDich.setKhachHang(khachHang);
        giaoDich.setNgayGiaoDich(dto.getNgayGiaoDich());
        giaoDich.setLoaiDichVu(dto.getLoaiDichVu());
        giaoDich.setDonGia(dto.getDonGia());
        giaoDich.setDienTich(dto.getDienTich());

        return giaoDichRepository.save(giaoDich);
    }

    @Override
    public GiaoDich saveEntity(GiaoDich giaoDich) {
        return giaoDichRepository.save(giaoDich);
    }

    @Override
    public void deleteById(String maGiaoDich) {
        giaoDichRepository.deleteById(maGiaoDich);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiaoDich> search(String tenKhachHang, String loaiDichVu) {
        String nameSearch = (tenKhachHang != null) ? tenKhachHang.trim() : "";
        String serviceSearch = (loaiDichVu != null && !loaiDichVu.trim().isEmpty() && !loaiDichVu.equals("ALL")) ? loaiDichVu.trim() : "";

        if (nameSearch.isEmpty() && serviceSearch.isEmpty()) {
            return giaoDichRepository.findAllWithKhachHang();
        }
        return giaoDichRepository.search(nameSearch, serviceSearch);
    }
}
