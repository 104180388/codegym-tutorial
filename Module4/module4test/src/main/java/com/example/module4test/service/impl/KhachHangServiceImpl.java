package com.example.module4test.service.impl;

import com.example.module4test.entity.KhachHang;
import com.example.module4test.repository.KhachHangRepository;
import com.example.module4test.service.KhachHangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;

    public KhachHangServiceImpl(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KhachHang> findById(String maKhachHang) {
        return khachHangRepository.findById(maKhachHang);
    }

    @Override
    public KhachHang save(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }
}
