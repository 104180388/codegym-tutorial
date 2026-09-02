package com.example.case_study_2.service;

import com.example.case_study_2.dto.BookingDto;
import com.example.case_study_2.dto.TimeSlotDto;
import com.example.case_study_2.entity.*;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    public List<TimeSlotDto> getAvailableTimeSlots(Long doctorId, LocalDate appointmentDate) {
        List<TimeSlotDto> slots = new ArrayList<>();
        if (doctorId == null || appointmentDate == null) {
            return slots;
        }

        // Fetch schedules for the doctor on that date
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndWorkDate(doctorId, appointmentDate);

        Set<LocalTime> possibleTimes = new TreeSet<>();
        for (DoctorSchedule sched : schedules) {
            if (sched.getShift() == Shift.MORNING) {
                possibleTimes.add(LocalTime.of(8, 0));
                possibleTimes.add(LocalTime.of(8, 30));
                possibleTimes.add(LocalTime.of(9, 0));
                possibleTimes.add(LocalTime.of(9, 30));
                possibleTimes.add(LocalTime.of(10, 0));
                possibleTimes.add(LocalTime.of(10, 30));
                possibleTimes.add(LocalTime.of(11, 0));
            } else if (sched.getShift() == Shift.AFTERNOON) {
                possibleTimes.add(LocalTime.of(13, 30));
                possibleTimes.add(LocalTime.of(14, 0));
                possibleTimes.add(LocalTime.of(14, 30));
                possibleTimes.add(LocalTime.of(15, 0));
                possibleTimes.add(LocalTime.of(15, 30));
                possibleTimes.add(LocalTime.of(16, 0));
            }
        }

        // Default slots if no specific schedule set for demo purpose
        if (possibleTimes.isEmpty()) {
            possibleTimes.add(LocalTime.of(8, 0));
            possibleTimes.add(LocalTime.of(8, 30));
            possibleTimes.add(LocalTime.of(9, 0));
            possibleTimes.add(LocalTime.of(9, 30));
            possibleTimes.add(LocalTime.of(10, 0));
            possibleTimes.add(LocalTime.of(10, 30));
            possibleTimes.add(LocalTime.of(13, 30));
            possibleTimes.add(LocalTime.of(14, 0));
            possibleTimes.add(LocalTime.of(14, 30));
            possibleTimes.add(LocalTime.of(15, 0));
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (LocalTime time : possibleTimes) {
            boolean available = true;

            // If today, filter out past time slots
            if (appointmentDate.isEqual(today) && time.isBefore(currentTime)) {
                available = false;
            }

            // Check if slot already booked
            if (available) {
                long bookedCount = appointmentRepository.countBookedSlot(doctorId, appointmentDate, time);
                if (bookedCount > 0) {
                    available = false;
                }
            }

            slots.add(new TimeSlotDto(time.format(formatter), time.format(formatter), available));
        }

        return slots;
    }

    @Transactional
    public Appointment bookAppointment(BookingDto dto, Patient patient) {
        // Rule: Check if patient has any unfinished appointment
        if (patient.getId() != null) {
            long unfinished = appointmentRepository.countUnfinishedAppointmentsByPatient(patient.getId());
            if (unfinished > 0) {
                throw new IllegalStateException("Bạn đang có lịch hẹn khám chưa hoàn thành. Vui lòng hoàn thành hoặc hủy lịch hẹn hiện tại trước khi đặt lịch mới!");
            }
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ đã chọn"));

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ y tế đã chọn"));

        // Check if slot is available
        long bookedCount = appointmentRepository.countBookedSlot(dto.getDoctorId(), dto.getAppointmentDate(), dto.getAppointmentTime());
        if (bookedCount > 0) {
            throw new IllegalStateException("Khung giờ này đã bị đặt kín. Vui lòng chọn khung giờ khác!");
        }

        Appointment app = new Appointment();
        app.setAppointmentCode("APT-" + System.currentTimeMillis() % 1000000);
        app.setPatient(patient);
        app.setDoctor(doctor);
        app.setService(service);
        app.setAppointmentDate(dto.getAppointmentDate());
        app.setAppointmentTime(dto.getAppointmentTime());
        app.setSymptoms(dto.getSymptoms());
        app.setStatus(AppointmentStatus.PENDING);

        return appointmentRepository.save(app);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    public List<Appointment> getDoctorAppointmentsForToday(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, LocalDate.now());
    }

    public List<Appointment> getDoctorQueueToday(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId,
                LocalDate.now(),
                Arrays.asList(AppointmentStatus.CHECKED_IN, AppointmentStatus.IN_PROGRESS)
        );
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn"));
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment app = getAppointmentById(appointmentId);
        app.setStatus(AppointmentStatus.CANCELLED);
        app.setCancellationReason(reason != null ? reason : "Bệnh nhân hủy lịch");
        appointmentRepository.save(app);
    }

    @Transactional
    public void updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment app = getAppointmentById(appointmentId);
        app.setStatus(status);
        appointmentRepository.save(app);
    }

    @Transactional
    public Appointment checkInAppointment(Long appointmentId) {
        Appointment app = getAppointmentById(appointmentId);
        Integer maxQueue = appointmentRepository.findMaxQueueNumberForDoctorAndDate(
                app.getDoctor().getId(), app.getAppointmentDate());
        int nextQueue = (maxQueue != null ? maxQueue : 0) + 1;

        app.setQueueNumber(nextQueue);
        app.setStatus(AppointmentStatus.CHECKED_IN);
        return appointmentRepository.save(app);
    }
}
