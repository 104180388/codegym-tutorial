package com.example.case_study_2.controller;

import com.example.case_study_2.dto.UserFormDto;
import com.example.case_study_2.entity.DoctorSchedule;
import com.example.case_study_2.entity.ServiceEntity;
import com.example.case_study_2.entity.User;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.repository.RoleRepository;
import com.example.case_study_2.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ServiceManagementService serviceManagementService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> stats = reportService.getAdminDashboardStats();
        model.addAllAttributes(stats);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", authService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("userFormDto", new UserFormDto());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user-form";
    }

    @PostMapping("/users/create")
    public String processCreateUser(@Valid @ModelAttribute("userFormDto") UserFormDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user-form";
        }

        try {
            authService.createUserInternal(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới tài khoản thành công!");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user-form";
        }
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        authService.toggleUserActiveStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái tài khoản thành công!");
        return "redirect:/admin/users";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", serviceManagementService.getAllServices());
        return "admin/services";
    }

    @PostMapping("/services/save")
    public String saveService(@ModelAttribute ServiceEntity service, RedirectAttributes redirectAttributes) {
        serviceManagementService.saveOrUpdateService(service);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ y tế thành công!");
        return "redirect:/admin/services";
    }

    @GetMapping("/schedules")
    public String doctorSchedules(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("schedules", doctorService.getAllSchedules());
        return "admin/doctor-schedules";
    }

    @PostMapping("/schedules/save")
    public String saveDoctorSchedule(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("workDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam("shift") String shift,
            RedirectAttributes redirectAttributes) {
        doctorService.addOrUpdateSchedule(doctorId, workDate, Shift.valueOf(shift));
        redirectAttributes.addFlashAttribute("successMessage", "Phân ca trực bác sĩ thành công!");
        return "redirect:/admin/schedules";
    }

    @PostMapping("/schedules/delete/{id}")
    public String deleteDoctorSchedule(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        doctorService.deleteSchedule(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa ca trực bác sĩ thành công!");
        return "redirect:/admin/schedules";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        Map<String, Object> stats = reportService.getAdminDashboardStats();
        model.addAllAttributes(stats);
        return "admin/reports";
    }

}
