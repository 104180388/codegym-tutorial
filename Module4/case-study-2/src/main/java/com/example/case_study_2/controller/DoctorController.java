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

    @Autowired
    private com.example.case_study_2.service.AiDiagnosisService aiDiagnosisService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Appointment> allFromToday = appointmentService.getDoctorAppointmentsFromDate(doctor.getId(), today);
        List<Appointment> todayApps = appointmentService.getDoctorAppointmentsForToday(doctor.getId());

        long waitingCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN).count();
        long inProgressCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.IN_PROGRESS).count();
        long completedCount = todayApps.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED || a.getStatus() == AppointmentStatus.AWAITING_PAYMENT).count();
        long totalPatientsExamined = examinationService.getPatientsExaminedByDoctor(doctor.getId(), null).size();

        List<Appointment> upcomingUnexamined = allFromToday.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED 
                          && a.getStatus() != AppointmentStatus.AWAITING_PAYMENT 
                          && a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("doctor", doctor);
        model.addAttribute("waitingCount", waitingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalPatientsExamined", totalPatientsExamined);
        model.addAttribute("upcomingAppointments", upcomingUnexamined);
        model.addAttribute("schedules", doctorService.getDoctorSchedules(doctor.getId()));
        model.addAttribute("today", today);
        return "doctor/dashboard";
    }

    public static class DoctorPatientDto {
        private com.example.case_study_2.entity.Patient patient;
        private List<ExaminationRecord> records;

        public DoctorPatientDto(com.example.case_study_2.entity.Patient patient, List<ExaminationRecord> records) {
            this.patient = patient;
            this.records = records;
        }

        public com.example.case_study_2.entity.Patient getPatient() {
            return patient;
        }

        public List<ExaminationRecord> getRecords() {
            return records;
        }
    }

    @GetMapping("/patients")
    public String patients(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam(value = "keyword", required = false) String keyword,
                           Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        List<com.example.case_study_2.entity.Patient> patients = examinationService.getPatientsExaminedByDoctor(doctor.getId(), keyword);

        List<DoctorPatientDto> patientList = new java.util.ArrayList<>();
        for (com.example.case_study_2.entity.Patient p : patients) {
            List<ExaminationRecord> recs = examinationService.getRecordsByDoctorAndPatient(doctor.getId(), p.getId());
            patientList.add(new DoctorPatientDto(p, recs));
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientList", patientList);
        model.addAttribute("keyword", keyword);
        return "doctor/patients";
    }

    @GetMapping("/records/{id}")
    public String recordDetail(@PathVariable("id") Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        ExaminationRecord record = examinationService.getRecordById(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("record", record);
        return "doctor/record-detail";
    }

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate customDate,
                               Model model) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        java.time.LocalDate today = java.time.LocalDate.now();

        List<Appointment> allFromToday = appointmentService.getDoctorAppointmentsFromDate(doctor.getId(), today);

        // Filter: only appointments not yet examined by doctor
        List<Appointment> filtered = allFromToday.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.COMPLETED
                          && a.getStatus() != AppointmentStatus.AWAITING_PAYMENT
                          && a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());

        if (customDate != null) {
            filtered = filtered.stream()
                    .filter(a -> a.getAppointmentDate().isEqual(customDate))
                    .collect(java.util.stream.Collectors.toList());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            filtered = filtered.stream()
                    .filter(a -> (a.getPatient().getFullName() != null && a.getPatient().getFullName().toLowerCase().contains(kw))
                              || (a.getPatient().getPhone() != null && a.getPatient().getPhone().contains(kw)))
                    .collect(java.util.stream.Collectors.toList());
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", filtered);
        model.addAttribute("customDate", customDate);
        model.addAttribute("keyword", keyword);
        model.addAttribute("today", today);

        return "doctor/appointments";
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

    @PostMapping("/api/ai-diagnose")
    @ResponseBody
    public org.springframework.http.ResponseEntity<com.example.case_study_2.dto.AiDiagnoseResponseDto> aiDiagnose(
            @RequestBody com.example.case_study_2.dto.AiDiagnoseRequestDto request) {
        com.example.case_study_2.dto.AiDiagnoseResponseDto response = aiDiagnosisService.generateDiagnosis(request);
        return org.springframework.http.ResponseEntity.ok(response);
    }
}
