package com.example.case_study_2.controller;

import com.example.case_study_2.config.CustomUserDetails;
import com.example.case_study_2.dto.ExaminationDto;
import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.Doctor;
import com.example.case_study_2.entity.ExaminationRecord;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.service.AppointmentService;
import com.example.case_study_2.service.DoctorService;
import com.example.case_study_2.service.ExaminationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ExaminationService examinationService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        List<Appointment> todayApps = appointmentService.getDoctorAppointmentsForToday(doctor.getId());

        long waitingCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN).count();
        long inProgressCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.IN_PROGRESS).count();
        long completedCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED || a.getStatus() == AppointmentStatus.AWAITING_PAYMENT).count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("waitingCount", waitingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("schedules", doctorService.getDoctorSchedules(doctor.getId()));
        return "doctor/dashboard";
    }

    @GetMapping("/schedule")
    public String schedule(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        model.addAttribute("doctor", doctor);
        model.addAttribute("schedules", doctorService.getDoctorSchedules(doctor.getId()));
        return "doctor/schedule";
    }

    @GetMapping("/queue")
    public String queue(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        model.addAttribute("doctor", doctor);
        model.addAttribute("queue", appointmentService.getDoctorQueueToday(doctor.getId()));
        return "doctor/queue";
    }

    @GetMapping("/examination/{id}")
    public String examinationPage(@PathVariable("id") Long appointmentId, Model model) {
        Appointment app = appointmentService.getAppointmentById(appointmentId);

        // Transition appointment to IN_PROGRESS when doctor opens exam
        if (app.getStatus() == AppointmentStatus.CHECKED_IN) {
            appointmentService.updateStatus(appointmentId, AppointmentStatus.IN_PROGRESS);
            app.setStatus(AppointmentStatus.IN_PROGRESS);
        }

        Optional<ExaminationRecord> existingRecord = examinationService.getRecordByAppointmentId(appointmentId);

        ExaminationDto dto = new ExaminationDto();
        dto.setAppointmentId(appointmentId);
        if (existingRecord.isPresent()) {
            ExaminationRecord rec = existingRecord.get();
            dto.setDiagnosis(rec.getDiagnosis());
            dto.setTreatmentPlan(rec.getTreatmentPlan());
            dto.setPrescriptionAdvice(rec.getPrescriptionAdvice());
            dto.setResultAttachmentUrl(rec.getResultAttachmentUrl());
            dto.setDoctorNotes(rec.getDoctorNotes());
        }

        model.addAttribute("appointment", app);
        model.addAttribute("examinationDto", dto);
        model.addAttribute("existingRecord", existingRecord.orElse(null));
        return "doctor/examination";
    }

    @PostMapping("/examination/{id}")
    public String saveExamination(@PathVariable("id") Long appointmentId,
                                  @Valid @ModelAttribute("examinationDto") ExaminationDto examinationDto,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "attachmentFile", required = false) MultipartFile file,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            Appointment app = appointmentService.getAppointmentById(appointmentId);
            model.addAttribute("appointment", app);
            return "doctor/examination";
        }

        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + filename);
                Files.write(filePath, file.getBytes());
                examinationDto.setResultAttachmentUrl("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        examinationService.saveExaminationRecord(examinationDto);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu kết quả khám bệnh và đơn thuốc. Đã chuyển bệnh nhân sang hàng chờ thanh toán!");
        return "redirect:/doctor/queue";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        model.addAttribute("doctor", doctor);
        return "doctor/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                @RequestParam("degree") String degree,
                                @RequestParam("experienceYears") Integer experienceYears,
                                @RequestParam("bio") String bio,
                                RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        doctorService.updateDoctorProfile(doctor.getId(), fullName, email, phone, degree, experienceYears, bio);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin chuyên môn thành công!");
        return "redirect:/doctor/profile";
    }
}
