package com.example.case_study_2.controller;

import com.example.case_study_2.dto.TimeSlotDto;
import com.example.case_study_2.entity.Doctor;
import com.example.case_study_2.service.AppointmentService;
import com.example.case_study_2.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/available-slots")
    public ResponseEntity<List<TimeSlotDto>> getAvailableSlots(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotDto> slots = appointmentService.getAvailableTimeSlots(doctorId, serviceId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available-doctors")
    public ResponseEntity<?> getAvailableDoctors(
            @RequestParam(value = "serviceId", required = false) Long serviceId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime time) {
        try {
            List<Doctor> doctors = appointmentService.getAvailableDoctorsForSlot(serviceId, date, time);
            List<Map<String, Object>> result = doctors.stream()
                .filter(doc -> doc != null && doc.getUser() != null)
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("fullName", doc.getUser().getFullName());
                    map.put("degree", doc.getDegree() != null ? doc.getDegree() : "Bác sĩ chuyên khoa");
                    map.put("experienceYears", doc.getExperienceYears());
                    return map;
                }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/doctors-by-service")
    public ResponseEntity<?> getDoctorsByService(@RequestParam(value = "serviceId", required = false) Long serviceId) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByServiceId(serviceId);
            List<Map<String, Object>> result = doctors.stream()
                .filter(doc -> doc != null && doc.getUser() != null)
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("fullName", doc.getUser().getFullName());
                    map.put("degree", doc.getDegree() != null ? doc.getDegree() : "Bác sĩ chuyên khoa");
                    return map;
                }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

