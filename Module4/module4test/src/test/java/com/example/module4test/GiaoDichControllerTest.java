package com.example.module4test;

import com.example.module4test.entity.KhachHang;
import com.example.module4test.repository.GiaoDichRepository;
import com.example.module4test.repository.KhachHangRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class GiaoDichControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private GiaoDichRepository giaoDichRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testHienThiDanhSachGiaoDich() throws Exception {
        mockMvc.perform(get("/giao-dich"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/list"))
                .andExpect(model().attributeExists("danhSachGiaoDich"))
                .andExpect(model().attributeExists("danhSachKhachHang"));
    }

    @Test
    public void testHienThiFormThemMoi() throws Exception {
        mockMvc.perform(get("/giao-dich/them-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeExists("giaoDichDto"))
                .andExpect(model().attributeExists("danhSachKhachHang"));
    }

    @Test
    public void testThemMoi_ValidateFormTrong() throws Exception {
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "")
                        .param("maKhachHang", "")
                        .param("loaiDichVu", "")
                        .param("donGia", "")
                        .param("dienTich", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "maGiaoDich", "maKhachHang", "loaiDichVu", "ngayGiaoDich", "donGia", "dienTich"));
    }

    @Test
    public void testThemMoi_ValidateMaGiaoDichSaiDinhDang() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "ABC-1234")
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Đất")
                        .param("ngayGiaoDich", futureDate.toString())
                        .param("donGia", "1000000")
                        .param("dienTich", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "maGiaoDich"));
    }

    @Test
    public void testThemMoi_ValidateMaGiaoDichTrungLap() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "MGD-0001")
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Đất")
                        .param("ngayGiaoDich", futureDate.toString())
                        .param("donGia", "1000000")
                        .param("dienTich", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "maGiaoDich"));
    }

    @Test
    public void testThemMoi_ValidateNgayGiaoDichQuaKhu() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "MGD-9999")
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Đất")
                        .param("ngayGiaoDich", pastDate.toString())
                        .param("donGia", "1000000")
                        .param("dienTich", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "ngayGiaoDich"));
    }

    @Test
    public void testThemMoi_ValidateDonGiaNhoHon500k() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "MGD-9999")
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Đất")
                        .param("ngayGiaoDich", futureDate.toString())
                        .param("donGia", "500000")
                        .param("dienTich", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "donGia"));
    }

    @Test
    public void testThemMoi_ValidateDienTichNhoHon20() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", "MGD-9999")
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Đất")
                        .param("ngayGiaoDich", futureDate.toString())
                        .param("donGia", "1000000")
                        .param("dienTich", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/create"))
                .andExpect(model().attributeHasFieldErrors("giaoDichDto", "dienTich"));
    }

    @Test
    public void testThemMoi_ThanhCongVaRedirect() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        String maMoi = "MGD-8888";
        if (giaoDichRepository.existsById(maMoi)) {
            giaoDichRepository.deleteById(maMoi);
        }

        mockMvc.perform(post("/giao-dich/them-moi")
                        .param("maGiaoDich", maMoi)
                        .param("maKhachHang", "KH-001")
                        .param("loaiDichVu", "Nhà đất")
                        .param("ngayGiaoDich", futureDate.toString())
                        .param("donGia", "2500000")
                        .param("dienTich", "80"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/giao-dich"))
                .andExpect(flash().attribute("message", "Thêm mới giao dịch thành công!"));
    }

    @Test
    public void testXemChiTietGiaoDich() throws Exception {
        mockMvc.perform(get("/giao-dich/chi-tiet/MGD-0001"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/detail"))
                .andExpect(model().attributeExists("giaoDich"))
                .andExpect(model().attribute("giaoDich", hasProperty("maGiaoDich", is("MGD-0001"))));
    }

    @Test
    public void testTimKiemGiaoDichTheoTen() throws Exception {
        mockMvc.perform(get("/giao-dich").param("tenKhachHang", "Nguyễn Văn A"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/list"))
                .andExpect(model().attribute("tenKhachHang", "Nguyễn Văn A"))
                .andExpect(model().attributeExists("danhSachGiaoDich"));
    }

    @Test
    public void testTimKiemGiaoDichTheoLoaiDichVu() throws Exception {
        mockMvc.perform(get("/giao-dich").param("loaiDichVu", "Đất"))
                .andExpect(status().isOk())
                .andExpect(view().name("giao-dich/list"))
                .andExpect(model().attribute("loaiDichVu", "Đất"))
                .andExpect(model().attributeExists("danhSachGiaoDich"));
    }

    @Test
    public void testXoaGiaoDich() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        String maXoa = "MGD-7777";
        KhachHang kh = khachHangRepository.findById("KH-001").orElse(null);
        if (kh != null) {
            com.example.module4test.entity.GiaoDich gd = new com.example.module4test.entity.GiaoDich(
                    maXoa, kh, futureDate, "Đất", 1500000.0, 50.0
            );
            giaoDichRepository.save(gd);
        }

        mockMvc.perform(post("/giao-dich/xoa/" + maXoa))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/giao-dich"))
                .andExpect(flash().attribute("message", "Xóa giao dịch thành công!"));
    }
}
