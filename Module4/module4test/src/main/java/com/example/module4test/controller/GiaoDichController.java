package com.example.module4test.controller;

import com.example.module4test.dto.GiaoDichDto;
import com.example.module4test.entity.GiaoDich;
import com.example.module4test.entity.KhachHang;
import com.example.module4test.service.GiaoDichService;
import com.example.module4test.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/", "/giao-dich"})
public class GiaoDichController {

    private final GiaoDichService giaoDichService;
    private final KhachHangService khachHangService;

    public GiaoDichController(GiaoDichService giaoDichService, KhachHangService khachHangService) {
        this.giaoDichService = giaoDichService;
        this.khachHangService = khachHangService;
    }

    @ModelAttribute("danhSachKhachHang")
    public List<KhachHang> getDanhSachKhachHang() {
        return khachHangService.findAll();
    }

    @GetMapping
    public String hienThiDanhSach(
            @RequestParam(name = "tenKhachHang", required = false, defaultValue = "") String tenKhachHang,
            @RequestParam(name = "loaiDichVu", required = false, defaultValue = "") String loaiDichVu,
            Model model) {
        List<GiaoDich> danhSachGiaoDich = giaoDichService.search(tenKhachHang, loaiDichVu);
        model.addAttribute("danhSachGiaoDich", danhSachGiaoDich);
        model.addAttribute("tenKhachHang", tenKhachHang);
        model.addAttribute("loaiDichVu", loaiDichVu);
        return "giao-dich/list";
    }

    @GetMapping("/them-moi")
    public String hienThiFormThemMoi(Model model) {
        if (!model.containsAttribute("giaoDichDto")) {
            model.addAttribute("giaoDichDto", new GiaoDichDto());
        }
        return "giao-dich/create";
    }

    @PostMapping("/them-moi")
    public String themMoiGiaoDich(
            @Valid @ModelAttribute("giaoDichDto") GiaoDichDto giaoDichDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate mã giao dịch trùng lặp
        if (giaoDichDto.getMaGiaoDich() != null && !giaoDichDto.getMaGiaoDich().trim().isEmpty()) {
            if (giaoDichService.existsById(giaoDichDto.getMaGiaoDich().trim())) {
                bindingResult.rejectValue("maGiaoDich", "duplicate", "Mã giao dịch đã tồn tại trong hệ thống");
            }
        }

        if (bindingResult.hasErrors()) {
            return "giao-dich/create";
        }

        try {
            giaoDichService.save(giaoDichDto);
            redirectAttributes.addFlashAttribute("message", "Thêm mới giao dịch thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/giao-dich";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "giao-dich/create";
        }
    }

    @GetMapping("/chi-tiet/{id}")
    public String xemChiTiet(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<GiaoDich> giaoDichOpt = giaoDichService.findById(id);
        if (giaoDichOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy giao dịch với mã: " + id);
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/giao-dich";
        }
        model.addAttribute("giaoDich", giaoDichOpt.get());
        return "giao-dich/detail";
    }

    @PostMapping("/xoa/{id}")
    public String xoaGiaoDich(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (giaoDichService.existsById(id)) {
            giaoDichService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa giao dịch thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Giao dịch không tồn tại hoặc đã bị xóa!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/giao-dich";
    }
}
