package com.codegym.casestudy.controller;

import com.codegym.casestudy.model.dto.ExaminationDTO;
import com.codegym.casestudy.model.entity.Appointment;
import com.codegym.casestudy.model.entity.AppointmentServices;
import com.codegym.casestudy.model.entity.MedicalRecord;
import com.codegym.casestudy.repository.AppointmentServicesRepository;
import com.codegym.casestudy.service.ExaminationService;
import com.codegym.casestudy.service.MedicalServiceService;
import com.codegym.casestudy.service.MedicineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final ExaminationService examinationService;
    private final MedicalServiceService medicalServiceService;
    private final MedicineService medicineService;
    private final AppointmentServicesRepository appointmentServicesRepository;

    public StaffController(ExaminationService examinationService,
                           MedicalServiceService medicalServiceService,
                           MedicineService medicineService,
                           AppointmentServicesRepository appointmentServicesRepository) {
        this.examinationService = examinationService;
        this.medicalServiceService = medicalServiceService;
        this.medicineService = medicineService;
        this.appointmentServicesRepository = appointmentServicesRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("appointments", examinationService.getQueueAppointments());
        return "staff/dashboard";
    }

    @GetMapping("/queue")
    public String queue(Model model) {
        model.addAttribute("appointments", examinationService.getQueueAppointments());
        return "staff/queue";
    }

    @GetMapping("/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            examinationService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Đã duyệt xác nhận lịch hẹn (CONFIRMED) thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi duyệt lịch: " + e.getMessage());
        }
        return "redirect:/staff/queue";
    }

    @GetMapping("/checkin/{id}")
    public String checkInAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            examinationService.checkInAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Đã tiếp nhận bệnh nhân vào phòng khám (CHECKED_IN) thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tiếp nhận: " + e.getMessage());
        }
        return "redirect:/staff/queue";
    }

    @GetMapping("/examination/{id}")
    public String showExaminationPage(@PathVariable("id") Long id, Model model) {
        Appointment appointment = examinationService.getAppointmentDetails(id);
        List<AppointmentServices> attachedServices = appointmentServicesRepository.findByAppointmentId(id);
        MedicalRecord existingRecord = examinationService.getMedicalRecordByAppointment(id);

        ExaminationDTO dto = ExaminationDTO.builder()
                .appointmentId(appointment.getId())
                .symptoms(existingRecord != null ? existingRecord.getSymptoms() : appointment.getNotes())
                .diagnosis(existingRecord != null ? existingRecord.getDiagnosis() : "")
                .treatmentPlan(existingRecord != null ? existingRecord.getTreatmentPlan() : "")
                .build();

        model.addAttribute("appointment", appointment);
        model.addAttribute("attachedServices", attachedServices);
        model.addAttribute("allServices", medicalServiceService.findAll());
        model.addAttribute("allMedicines", medicineService.findAll());
        model.addAttribute("examinationDTO", dto);
        return "staff/examination";
    }

    @GetMapping("/examination")
    public String showDefaultExaminationPage(Model model) {
        List<Appointment> queue = examinationService.getQueueAppointments();
        if (!queue.isEmpty()) {
            return "redirect:/staff/examination/" + queue.get(0).getId();
        }
        model.addAttribute("errorMessage", "Hiện chưa có ca khám nào trong hàng chờ.");
        return "staff/queue";
    }

    @PostMapping("/examination/save")
    public String processSaveExamination(@ModelAttribute("examinationDTO") ExaminationDTO dto,
                                         RedirectAttributes redirectAttributes) {
        try {
            examinationService.saveExamination(dto);
            redirectAttributes.addFlashAttribute("message", "Lưu hồ sơ bệnh án, kê đơn thuốc và hoàn thành ca khám (COMPLETED) thành công!");
            return "redirect:/staff/queue";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi lưu ca khám: " + e.getMessage());
            return "redirect:/staff/queue";
        }
    }
}
