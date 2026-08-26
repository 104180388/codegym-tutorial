package com.codegym.casestudy.controller.admin;

import com.codegym.casestudy.model.entity.Service;
import com.codegym.casestudy.service.MedicalServiceService;
import com.codegym.casestudy.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;
    private final SpecialtyService specialtyService;

    public MedicalServiceController(MedicalServiceService medicalServiceService, SpecialtyService specialtyService) {
        this.medicalServiceService = medicalServiceService;
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("services", medicalServiceService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        if (!model.containsAttribute("serviceForm")) {
            model.addAttribute("serviceForm", new Service());
        }
        return "admin/services";
    }

    @PostMapping("/save")
    public String saveService(@ModelAttribute("serviceForm") Service service, RedirectAttributes redirectAttributes) {
        medicalServiceService.save(service);
        redirectAttributes.addFlashAttribute("message", "Lưu dịch vụ y tế thành công!");
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String editService(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return medicalServiceService.findById(id)
                .map(service -> {
                    model.addAttribute("services", medicalServiceService.findAll());
                    model.addAttribute("specialties", specialtyService.findAll());
                    model.addAttribute("serviceForm", service);
                    model.addAttribute("isEdit", true);
                    return "admin/services";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy dịch vụ với ID: " + id);
                    return "redirect:/admin/services";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            medicalServiceService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa dịch vụ đang được sử dụng!");
        }
        return "redirect:/admin/services";
    }
}
