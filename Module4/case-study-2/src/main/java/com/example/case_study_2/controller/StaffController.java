package com.example.case_study_2.controller;

import com.example.case_study_2.config.CustomUserDetails;
import com.example.case_study_2.dto.BookingDto;
import com.example.case_study_2.dto.InvoiceDto;
import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.Invoice;
import com.example.case_study_2.entity.Patient;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Gender;
import com.example.case_study_2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ServiceManagementService serviceManagementService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Appointment> allApps = appointmentService.getAllAppointments();
        long pendingCount = allApps.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long checkedInCount = allApps.stream().filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN).count();
        long awaitingPaymentCount = allApps.stream().filter(a -> a.getStatus() == AppointmentStatus.AWAITING_PAYMENT).count();

        List<Invoice> invoices = invoiceService.getAllInvoices();
        BigDecimal todayRevenue = invoices.stream()
                .filter(i -> i.getPaymentTime() != null && i.getPaymentTime().toLocalDate().isEqual(LocalDate.now()))
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("checkedInCount", checkedInCount);
        model.addAttribute("awaitingPaymentCount", awaitingPaymentCount);
        model.addAttribute("todayRevenue", todayRevenue);
        return "staff/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "staff/appointments";
    }

    @PostMapping("/appointments/approve/{id}")
    public String approveAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        appointmentService.updateStatus(id, AppointmentStatus.CONFIRMED);
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt lịch hẹn thành công!");
        return "redirect:/staff/appointments";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long id,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, reason != null ? reason : "Lễ tân từ chối/hủy lịch");
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy lịch hẹn.");
        return "redirect:/staff/appointments";
    }

    @GetMapping("/checkin")
    public String checkinPage(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        return "staff/checkin";
    }

    @PostMapping("/checkin/{id}")
    public String processCheckin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Appointment app = appointmentService.checkInAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tiếp đón bệnh nhân thành công! Số STT: " + app.getQueueNumber());
        return "redirect:/staff/checkin";
    }

    @PostMapping("/checkin/walk-in")
    public String createWalkInAppointment(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("dob") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam("gender") String gender,
            @RequestParam("address") String address,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("symptoms") String symptoms,
            RedirectAttributes redirectAttributes) {

        Patient patient = patientService.findOrCreateWalkInPatient(fullName, phone, dob, Gender.valueOf(gender), address);

        BookingDto dto = new BookingDto();
        dto.setServiceId(serviceId);
        dto.setDoctorId(doctorId);
        dto.setAppointmentDate(LocalDate.now());
        dto.setAppointmentTime(LocalTime.now());
        dto.setSymptoms(symptoms);

        Appointment app = appointmentService.bookAppointment(dto, patient);
        appointmentService.checkInAppointment(app.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Đã tiếp đón bệnh nhân vãng lai! Số STT khám: " + app.getQueueNumber());
        return "redirect:/staff/checkin";
    }

    @GetMapping("/patients")
    public String patients(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("patients", patientService.searchPatients(keyword));
        return "staff/patients";
    }

    @GetMapping("/billing")
    public String billing(Model model) {
        List<Appointment> awaitingApps = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AWAITING_PAYMENT)
                .toList();
        model.addAttribute("appointments", awaitingApps);
        return "staff/billing";
    }

    @GetMapping("/invoices/{id}")
    public String invoiceDetail(@PathVariable("id") Long appointmentId,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        Appointment app = appointmentService.getAppointmentById(appointmentId);
        Invoice invoice = invoiceService.createOrGetInvoiceForAppointment(appointmentId, userDetails.getUser().getId());

        model.addAttribute("appointment", app);
        model.addAttribute("invoice", invoice);
        return "staff/invoice-detail";
    }

    @PostMapping("/invoices/{id}/pay")
    public String processPayment(@PathVariable("id") Long appointmentId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("serviceFee") BigDecimal serviceFee,
                                 @RequestParam(value = "surcharge", required = false) BigDecimal surcharge,
                                 @RequestParam(value = "discount", required = false) BigDecimal discount,
                                 @RequestParam("paymentMethod") String paymentMethod,
                                 RedirectAttributes redirectAttributes) {
        InvoiceDto dto = new InvoiceDto();
        dto.setAppointmentId(appointmentId);
        dto.setServiceFee(serviceFee);
        dto.setSurcharge(surcharge != null ? surcharge : BigDecimal.ZERO);
        dto.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        dto.setPaymentMethod(com.example.case_study_2.entity.enums.PaymentMethod.valueOf(paymentMethod));

        invoiceService.processPayment(dto, userDetails.getUser().getId());
        redirectAttributes.addFlashAttribute("successMessage", "Thanh toán hóa đơn thành công! Trạng thái khám bệnh đã HOÀN THÀNH.");
        return "redirect:/staff/billing";
    }
}
