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
import java.util.List;
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

    @Autowired
    private ShiftChangeService shiftChangeService;

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

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            authService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản người dùng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa tài khoản: " + e.getMessage());
        }
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
    public String reports(
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        Map<String, Object> report = reportService.getFilteredRevenueReport(filter, startDate, endDate);
        model.addAllAttributes(report);
        return "admin/reports";
    }

    @GetMapping("/reports/comparison")
    public String comparisonReport(
            @RequestParam(value = "month1", required = false, defaultValue = "7") int month1,
            @RequestParam(value = "year1", required = false, defaultValue = "2026") int year1,
            @RequestParam(value = "month2", required = false, defaultValue = "8") int month2,
            @RequestParam(value = "year2", required = false, defaultValue = "2026") int year2,
            Model model) {
        Map<String, Object> comparison = reportService.getMonthComparisonReport(month1, year1, month2, year2);
        model.addAllAttributes(comparison);
        return "admin/comparison";
    }

    @GetMapping("/patients")
    public String patients(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {
        Map<String, Object> patientReport = reportService.getPatientsAdminReport(keyword);
        model.addAllAttributes(patientReport);
        return "admin/patients";
    }

    @GetMapping("/shift-requests")
    public String shiftRequests(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            Model model) {
        List<com.example.case_study_2.entity.ShiftChangeRequest> requests;
        if ("ALL".equalsIgnoreCase(status) || status.isEmpty()) {
            requests = shiftChangeService.getAllRequests();
        } else {
            try {
                com.example.case_study_2.entity.enums.ShiftRequestStatus reqStatus = com.example.case_study_2.entity.enums.ShiftRequestStatus.valueOf(status);
                requests = shiftChangeService.getRequestsByStatus(reqStatus);
            } catch (Exception e) {
                requests = shiftChangeService.getAllRequests();
            }
        }

        List<com.example.case_study_2.entity.ShiftChangeRequest> all = shiftChangeService.getAllRequests();
        long totalCount = all.size();
        long pendingCount = all.stream().filter(r -> r.getStatus() == com.example.case_study_2.entity.enums.ShiftRequestStatus.PENDING).count();
        long approvedCount = all.stream().filter(r -> r.getStatus() == com.example.case_study_2.entity.enums.ShiftRequestStatus.APPROVED).count();
        long rejectedCount = all.stream().filter(r -> r.getStatus() == com.example.case_study_2.entity.enums.ShiftRequestStatus.REJECTED).count();

        model.addAttribute("requests", requests);
        model.addAttribute("currentStatus", status);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        return "admin/shift-requests";
    }

    @PostMapping("/shift-requests/{id}/approve")
    public String approveShiftRequest(
            @PathVariable("id") Long id,
            @RequestParam(value = "adminNotes", required = false) String adminNotes,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.case_study_2.config.CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            shiftChangeService.approveRequest(id, userDetails.getUser().getId(), adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Đã phê duyệt yêu cầu thành công! Lịch phân ca trực của bác sĩ đã được cập nhật tự động.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể phê duyệt yêu cầu: " + e.getMessage());
        }
        return "redirect:/admin/shift-requests";
    }

    @PostMapping("/shift-requests/{id}/reject")
    public String rejectShiftRequest(
            @PathVariable("id") Long id,
            @RequestParam(value = "adminNotes", required = false) String adminNotes,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.case_study_2.config.CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            shiftChangeService.rejectRequest(id, userDetails.getUser().getId(), adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối yêu cầu nghỉ/đổi ca.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể từ chối yêu cầu: " + e.getMessage());
        }
        return "redirect:/admin/shift-requests";
    }
}
