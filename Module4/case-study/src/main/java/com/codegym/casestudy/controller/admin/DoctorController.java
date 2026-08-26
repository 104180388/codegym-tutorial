package com.codegym.casestudy.controller.admin;

import com.codegym.casestudy.model.entity.Doctor;
import com.codegym.casestudy.service.DoctorService;
import com.codegym.casestudy.service.SpecialtyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;

    public DoctorController(DoctorService doctorService, SpecialtyService specialtyService) {
        this.doctorService = doctorService;
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        if (!model.containsAttribute("doctorForm")) {
            model.addAttribute("doctorForm", new Doctor());
        }
        return "admin/doctors";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute("doctorForm") Doctor doctor, RedirectAttributes redirectAttributes) {
        doctorService.save(doctor);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin bác sĩ & phòng khám thành công!");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/edit/{id}")
    public String editDoctor(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return doctorService.findById(id)
                .map(doctor -> {
                    model.addAttribute("doctors", doctorService.findAll());
                    model.addAttribute("specialties", specialtyService.findAll());
                    model.addAttribute("doctorForm", doctor);
                    model.addAttribute("isEdit", true);
                    return "admin/doctors";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bác sĩ với ID: " + id);
                    return "redirect:/admin/doctors";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bác sĩ đang có lịch khám!");
        }
        return "redirect:/admin/doctors";
    }

    @PostMapping("/schedule/create")
    public String createSchedule(@RequestParam("doctorId") Long doctorId,
                                 @RequestParam("workDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
                                 @RequestParam("timeSlot") String timeSlot,
                                 @RequestParam(value = "maxPatients", defaultValue = "10") Integer maxPatients,
                                 RedirectAttributes redirectAttributes) {
        try {
            doctorService.createSchedule(doctorId, workDate, timeSlot, maxPatients);
            redirectAttributes.addFlashAttribute("message", "Tạo ca làm việc (Schedule) cho Bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tạo ca làm việc: " + e.getMessage());
        }
        return "redirect:/admin/doctors";
    }
}
