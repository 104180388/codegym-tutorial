package com.example.case_study_2.service;

import com.example.case_study_2.dto.BookingDto;
import com.example.case_study_2.dto.TimeSlotDto;
import com.example.case_study_2.entity.*;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    public List<TimeSlotDto> getAvailableTimeSlots(Long doctorId, Long serviceId, LocalDate appointmentDate) {
        List<TimeSlotDto> slots = new ArrayList<>();
        if (appointmentDate == null) {
            return slots;
        }

        // If specific doctor is chosen
        if (doctorId != null) {
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
                possibleTimes.add(LocalTime.of(15, 30));
                possibleTimes.add(LocalTime.of(16, 0));
            }

            LocalDate today = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (LocalTime time : possibleTimes) {
                boolean available = true;

                if (appointmentDate.isEqual(today) && time.isBefore(currentTime)) {
                    available = false;
                }

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

        // Standard time slots when user selects Service and Date before Doctor
        List<LocalTime> standardTimes = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0),
                LocalTime.of(13, 30),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                LocalTime.of(16, 0)
        );

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (LocalTime time : standardTimes) {
            boolean available = true;
            if (appointmentDate.isEqual(today) && time.isBefore(currentTime)) {
                available = false;
            } else {
                List<Doctor> docs = getAvailableDoctorsForSlot(serviceId, appointmentDate, time);
                if (docs.isEmpty()) {
                    available = false;
                }
            }
            slots.add(new TimeSlotDto(time.format(formatter), time.format(formatter), available));
        }

        return slots;
    }

    public List<TimeSlotDto> getAvailableTimeSlots(Long doctorId, LocalDate appointmentDate) {
        return getAvailableTimeSlots(doctorId, null, appointmentDate);
    }

    public List<Doctor> getAvailableDoctorsForSlot(Long serviceId, LocalDate appointmentDate, LocalTime appointmentTime) {
        if (appointmentDate == null || appointmentTime == null) {
            return Collections.emptyList();
        }

        List<Doctor> candidateDoctors;
        if (serviceId != null) {
            candidateDoctors = doctorRepository.findByServicesId(serviceId);
        } else {
            candidateDoctors = doctorRepository.findAll();
        }

        Shift shift = appointmentTime.isBefore(LocalTime.of(12, 0)) ? Shift.MORNING : Shift.AFTERNOON;

        List<Doctor> available = new ArrayList<>();
        for (Doctor doc : candidateDoctors) {
            if (doc == null || doc.getUser() == null) continue;

            List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndWorkDate(doc.getId(), appointmentDate);
            boolean worksShift = false;
            if (schedules.isEmpty()) {
                worksShift = true;
            } else {
                worksShift = schedules.stream().anyMatch(s -> s.getShift() == shift);
            }

            if (worksShift) {
                long bookedCount = appointmentRepository.countBookedSlot(doc.getId(), appointmentDate, appointmentTime);
                if (bookedCount == 0) {
                    available.add(doc);
                }
            }
        }
        return available;
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

    @Transactional
    public int autoCancelOverdueAppointments() {
        return appointmentRepository.autoCancelOverdueAppointments(LocalDate.now());
    }

    @PostConstruct
    @Transactional
    public void onStartupAutoCancelOverdueAppointments() {
        try {
            int count = autoCancelOverdueAppointments();
            if (count > 0) {
                System.out.println("[Startup Hook] Đã tự động đổi trạng thái " + count + " lịch hẹn đã qua ngày thành 'Đã hủy'.");
            }
        } catch (Exception e) {
            System.err.println("[Startup Hook] Lỗi khi tự động hủy lịch hẹn quá hạn: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * ?") // Chạy mỗi đầu giờ
    @Transactional
    public void scheduledAutoCancelOverdueAppointments() {
        int count = autoCancelOverdueAppointments();
        if (count > 0) {
            System.out.println("[Scheduled Task] Đã tự động đổi trạng thái " + count + " lịch hẹn đã qua ngày thành 'Đã hủy'.");
        }
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        if (patientId == null) return Collections.emptyList();
        autoCancelOverdueAppointments();
        return appointmentRepository.findPatientAppointmentsActiveAndCompleted(patientId);
    }

    public List<Appointment> getDoctorAppointmentsForToday(Long doctorId) {
        autoCancelOverdueAppointments();
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, LocalDate.now());
    }

    public List<Appointment> getDoctorAppointmentsFromDate(Long doctorId, LocalDate startDate) {
        if (doctorId == null) return Collections.emptyList();
        autoCancelOverdueAppointments();
        LocalDate fromDate = startDate != null ? startDate : LocalDate.now();
        return appointmentRepository.findDoctorAppointmentsFromDate(doctorId, fromDate);
    }

    public List<Appointment> getDoctorQueueToday(Long doctorId) {
        autoCancelOverdueAppointments();
        return appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId,
                LocalDate.now(),
                Arrays.asList(AppointmentStatus.CHECKED_IN, AppointmentStatus.IN_PROGRESS)
        );
    }

    public List<Appointment> getPendingAppointments() {
        autoCancelOverdueAppointments();
        return appointmentRepository.findPendingAppointments();
    }

    public List<Appointment> getAllAppointmentsForStaff() {
        autoCancelOverdueAppointments();
        return appointmentRepository.findAllForStaffManagement();
    }

    public List<Appointment> getAllAppointments() {
        autoCancelOverdueAppointments();
        return appointmentRepository.findAllForStaffManagement();
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
