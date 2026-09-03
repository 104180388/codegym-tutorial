package com.example.case_study_2.controller;

import com.example.case_study_2.config.CustomUserDetails;
import com.example.case_study_2.dto.BookingDto;
import com.example.case_study_2.entity.ExaminationRecord;
import com.example.case_study_2.entity.Patient;
import com.example.case_study_2.entity.User;
import com.example.case_study_2.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ServiceManagementService serviceManagementService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/booking")
    public String bookingForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam(value = "serviceId", required = false) Long serviceId,
                              @RequestParam(value = "doctorId", required = false) Long doctorId,
                              Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());

        if (!model.containsAttribute("bookingDto")) {
            BookingDto dto = new BookingDto();
            dto.setPatientFullName(patient.getFullName());
            dto.setPatientDob(patient.getDob());
            dto.setPatientPhone(patient.getPhone());
            dto.setPatientEmail(user.getEmail());
            dto.setAppointmentDate(LocalDate.now());
            if (serviceId != null) {
                dto.setServiceId(serviceId);
            }
            if (doctorId != null) {
                dto.setDoctorId(doctorId);
            }
            model.addAttribute("bookingDto", dto);
        }

        model.addAttribute("patient", patient);
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("minDate", LocalDate.now());
        model.addAttribute("maxDate", LocalDate.now().plusDays(3));

        return "patient/booking";
    }

    @PostMapping("/booking")
    public String processBooking(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @Valid @ModelAttribute("bookingDto") BookingDto bookingDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("services", serviceManagementService.getAllActiveServices());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("minDate", LocalDate.now());
            model.addAttribute("maxDate", LocalDate.now().plusDays(3));
            return "patient/booking";
        }

        try {
            appointmentService.bookAppointment(bookingDto, patient);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch khám thành công! Vui lòng chờ nhân viên tiếp nhận.");
            return "redirect:/patient/appointments";
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patient", patient);
            model.addAttribute("services", serviceManagementService.getAllActiveServices());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("minDate", LocalDate.now());
            model.addAttribute("maxDate", LocalDate.now().plusDays(3));
            return "patient/booking";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("user", user);
        return "patient/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("dob") String dob,
                                @RequestParam("gender") String gender,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                RedirectAttributes redirectAttributes) {
        User user = userDetails.getUser();
        patientService.updatePatientProfile(
                user.getId(),
                fullName,
                LocalDate.parse(dob),
                com.example.case_study_2.entity.enums.Gender.valueOf(gender),
                phone,
                address
        );
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ cá nhân thành công!");
        return "redirect:/patient/profile";
    }

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());
        model.addAttribute("appointments", appointmentService.getPatientAppointments(patient.getId()));
        return "patient/appointments";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long id,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy lịch hẹn thành công.");
        return "redirect:/patient/appointments";
    }

    @GetMapping("/medical-history")
    public String medicalHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());
        model.addAttribute("records", examinationService.getMedicalHistoryByPatientId(patient.getId()));
        return "patient/medical-history";
    }

    @GetMapping("/prescriptions/{id}")
    public String prescriptionDetail(@PathVariable("id") Long id, Model model) {
        ExaminationRecord record = examinationService.getRecordById(id);
        model.addAttribute("record", record);
        return "patient/prescription-detail";
    }

    @GetMapping("/invoices")
    public String invoices(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        Patient patient = patientService.getPatientByUserId(user.getId());
        model.addAttribute("invoices", invoiceService.getInvoicesByPatientId(patient.getId()));
        return "patient/invoices";
    }
}
