package com.codegym.casestudy.controller.admin;

import com.codegym.casestudy.model.entity.Medicine;
import com.codegym.casestudy.service.MedicineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public String listMedicines(Model model) {
        model.addAttribute("medicines", medicineService.findAll());
        if (!model.containsAttribute("medicineForm")) {
            model.addAttribute("medicineForm", new Medicine());
        }
        return "admin/medicines";
    }

    @PostMapping("/save")
    public String saveMedicine(@ModelAttribute("medicineForm") Medicine medicine, RedirectAttributes redirectAttributes) {
        medicineService.save(medicine);
        redirectAttributes.addFlashAttribute("message", "Lưu thông tin thuốc thành công!");
        return "redirect:/admin/medicines";
    }

    @GetMapping("/edit/{id}")
    public String editMedicine(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return medicineService.findById(id)
                .map(medicine -> {
                    model.addAttribute("medicines", medicineService.findAll());
                    model.addAttribute("medicineForm", medicine);
                    model.addAttribute("isEdit", true);
                    return "admin/medicines";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thuốc với ID: " + id);
                    return "redirect:/admin/medicines";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            medicineService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thuốc khỏi danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa thuốc đang có trong đơn thuốc!");
        }
        return "redirect:/admin/medicines";
    }
}
