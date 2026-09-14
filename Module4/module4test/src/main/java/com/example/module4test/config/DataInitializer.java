package com.example.module4test.config;

import com.example.module4test.entity.GiaoDich;
import com.example.module4test.entity.KhachHang;
import com.example.module4test.repository.GiaoDichRepository;
import com.example.module4test.repository.KhachHangRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KhachHangRepository khachHangRepository;
    private final GiaoDichRepository giaoDichRepository;

    public DataInitializer(KhachHangRepository khachHangRepository, GiaoDichRepository giaoDichRepository) {
        this.khachHangRepository = khachHangRepository;
        this.giaoDichRepository = giaoDichRepository;
    }

    @Override
    public void run(String... args) {
        if (khachHangRepository.count() == 0) {
            KhachHang kh1 = new KhachHang("KH-001", "Nguyễn Văn A", "0905973155", "nguyenvana@gmail.com");
            KhachHang kh2 = new KhachHang("KH-002", "Nguyễn Văn B", "0912345678", "nguyenvanb@gmail.com");
            KhachHang kh3 = new KhachHang("KH-003", "Nguyễn Văn C", "0987654321", "nguyenvanc@gmail.com");
            KhachHang kh4 = new KhachHang("KH-004", "Trần Thị Lan", "0934567890", "tranthilan@gmail.com");

            khachHangRepository.saveAll(Arrays.asList(kh1, kh2, kh3, kh4));

            if (giaoDichRepository.count() == 0) {
                GiaoDich gd1 = new GiaoDich("MGD-0001", kh1, LocalDate.now().plusDays(10), "Nhà đất", 2000000.0, 100.0);
                GiaoDich gd2 = new GiaoDich("MGD-0002", kh2, LocalDate.now().plusDays(15), "Đất", 3000000.0, 200.0);
                GiaoDich gd3 = new GiaoDich("MGD-0003", kh3, LocalDate.now().plusDays(20), "Nhà đất", 5000000.0, 100.0);

                giaoDichRepository.saveAll(Arrays.asList(gd1, gd2, gd3));
            }
        }
    }
}
