package com.codegym.casestudy.controller;

import com.codegym.casestudy.model.dto.AppointmentDTO;
import com.codegym.casestudy.service.AppointmentService;
import com.codegym.casestudy.service.DoctorService;
import com.codegym.casestudy.service.MedicalServiceService;
import com.codegym.casestudy.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final AppointmentService appointmentService;
    private final SpecialtyService specialtyService;
    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;

    public PatientController(AppointmentService appointmentService,
                             SpecialtyService specialtyService,
                             DoctorService doctorService,
                             MedicalServiceService medicalServiceService) {
        this.appointmentService = appointmentService;
        this.specialtyService = specialtyService;
        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : "patient";
        model.addAttribute("appointments", appointmentService.getPatientAppointments(username));
        return "patient/dashboard";
    }

    @GetMapping("/booking")
    public String showBookingForm(Model model) {
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("services", medicalServiceService.findAll());
        if (!model.containsAttribute("appointmentDTO")) {
            model.addAttribute("appointmentDTO", new AppointmentDTO());
        }
        return "patient/booking";
    }

    @PostMapping("/booking")
    public String processBooking(@ModelAttribute("appointmentDTO") AppointmentDTO dto,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = (principal != null) ? principal.getName() : "patient";
            appointmentService.createAppointment(dto, username);
            redirectAttributes.addFlashAttribute("message", "Đăng ký đặt lịch khám thành công! Lịch hẹn của bạn đang ở trạng thái chờ xác nhận.");
            return "redirect:/patient/my-appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể đặt lịch: " + e.getMessage());
            return "redirect:/patient/booking";
        }
    }

    @GetMapping("/my-appointments")
    public String myAppointments(Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : "patient";
        model.addAttribute("appointments", appointmentService.getPatientAppointments(username));
        return "patient/my-appointments";
    }

    @GetMapping("/my-appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Đã gửi yêu cầu hủy lịch hẹn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hủy lịch: " + e.getMessage());
        }
        return "redirect:/patient/my-appointments";
    }
}
