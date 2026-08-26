package com.codegym.casestudy.controller.api;

import com.codegym.casestudy.model.entity.Schedule;
import com.codegym.casestudy.model.enums.ScheduleStatus;
import com.codegym.casestudy.repository.ScheduleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    private final ScheduleRepository scheduleRepository;

    public ScheduleApiController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableSchedules(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndWorkDate(doctorId, date);

        List<Map<String, Object>> result = schedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.AVAILABLE && s.getCurrentPatients() < s.getMaxPatients())
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("timeSlot", s.getTimeSlot());
                    map.put("maxPatients", s.getMaxPatients());
                    map.put("currentPatients", s.getCurrentPatients());
                    map.put("availableSlots", s.getMaxPatients() - s.getCurrentPatients());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
