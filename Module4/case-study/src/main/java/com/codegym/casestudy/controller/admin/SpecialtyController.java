package com.codegym.casestudy.controller.admin;

import com.codegym.casestudy.model.entity.Specialty;
import com.codegym.casestudy.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public String listSpecialties(Model model) {
        model.addAttribute("specialties", specialtyService.findAll());
        if (!model.containsAttribute("specialtyForm")) {
            model.addAttribute("specialtyForm", new Specialty());
        }
        return "admin/specialties";
    }

    @PostMapping("/save")
    public String saveSpecialty(@ModelAttribute("specialtyForm") Specialty specialty, RedirectAttributes redirectAttributes) {
        specialtyService.save(specialty);
        redirectAttributes.addFlashAttribute("message", "Lưu thông tin chuyên khoa thành công!");
        return "redirect:/admin/specialties";
    }

    @GetMapping("/edit/{id}")
    public String editSpecialty(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return specialtyService.findById(id)
                .map(specialty -> {
                    model.addAttribute("specialties", specialtyService.findAll());
                    model.addAttribute("specialtyForm", specialty);
                    model.addAttribute("isEdit", true);
                    return "admin/specialties";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyên khoa với ID: " + id);
                    return "redirect:/admin/specialties";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteSpecialty(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            specialtyService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa chuyên khoa đang được liên kết dữ liệu!");
        }
        return "redirect:/admin/specialties";
    }
}
