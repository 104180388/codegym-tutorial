package com.codegym.casestudy.service.impl;

import com.codegym.casestudy.model.dto.AppointmentDTO;
import com.codegym.casestudy.model.entity.*;
import com.codegym.casestudy.model.enums.AppointmentStatus;
import com.codegym.casestudy.model.enums.ScheduleStatus;
import com.codegym.casestudy.repository.*;
import com.codegym.casestudy.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServicesRepository appointmentServicesRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceRepository serviceRepository;
    private final AccountRepository accountRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  AppointmentServicesRepository appointmentServicesRepository,
                                  ScheduleRepository scheduleRepository,
                                  DoctorRepository doctorRepository,
                                  ServiceRepository serviceRepository,
                                  AccountRepository accountRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServicesRepository = appointmentServicesRepository;
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.serviceRepository = serviceRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean isScheduleAvailable(Long scheduleId) {
        if (scheduleId == null) return true;
        return scheduleRepository.findById(scheduleId)
                .map(schedule -> schedule.getStatus() == ScheduleStatus.AVAILABLE 
                        && schedule.getCurrentPatients() < schedule.getMaxPatients())
                .orElse(false);
    }

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentDTO dto, String username) {
        Account patient = accountRepository.findByUsername(username)
                .orElseGet(() -> accountRepository.findAll().stream()
                        .filter(acc -> acc.getRole().name().equals("ROLE_PATIENT"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản bệnh nhân: " + username)));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bác sĩ với ID: " + dto.getDoctorId()));

        Schedule schedule = null;
        if (dto.getScheduleId() != null) {
            schedule = scheduleRepository.findById(dto.getScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca khám với ID: " + dto.getScheduleId()));

            if (!isScheduleAvailable(schedule.getId())) {
                throw new IllegalStateException("Ca khám này đã đầy hoặc không khả dụng!");
            }

            // Cộng số bệnh nhân đăng ký vào ca khám
            schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
            if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
                schedule.setStatus(ScheduleStatus.FULL);
            }
            scheduleRepository.save(schedule);
        }

        // Tính tổng tiền khám + các dịch vụ cận lâm sàng đi kèm
        double totalAmount = (doctor.getConsultationFee() != null) ? doctor.getConsultationFee() : 0.0;
        List<com.codegym.casestudy.model.entity.Service> selectedServices = new ArrayList<>();

        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            for (Long srvId : dto.getServiceIds()) {
                serviceRepository.findById(srvId).ifPresent(srv -> {
                    selectedServices.add(srv);
                });
            }
            for (com.codegym.casestudy.model.entity.Service srv : selectedServices) {
                if (srv.getPrice() != null) {
                    totalAmount += srv.getPrice();
                }
            }
        }

        LocalDate appDate = (schedule != null) ? schedule.getWorkDate() : (dto.getAppointmentDate() != null ? dto.getAppointmentDate() : LocalDate.now());
        String appTime = (schedule != null) ? schedule.getTimeSlot() : dto.getAppointmentTime();

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .schedule(schedule)
                .appointmentDate(appDate)
                .appointmentTime(appTime)
                .status(AppointmentStatus.PENDING)
                .notes(dto.getNotes())
                .totalAmount(totalAmount)
                .build();

        appointment = appointmentRepository.save(appointment);

        // Lưu từng dịch vụ chọn trước vào bảng trung gian AppointmentServices
        for (com.codegym.casestudy.model.entity.Service srv : selectedServices) {
            AppointmentServicesId appSrvId = new AppointmentServicesId(appointment.getId(), srv.getId());
            AppointmentServices appSrv = AppointmentServices.builder()
                    .id(appSrvId)
                    .appointment(appointment)
                    .service(srv)
                    .quantity(1)
                    .price(srv.getPrice())
                    .build();
            appointmentServicesRepository.save(appSrv);
        }

        return appointment;
    }

    @Override
    public List<Appointment> getPatientAppointments(String username) {
        return accountRepository.findByUsername(username)
                .map(acc -> appointmentRepository.findByPatientId(acc.getId()))
                .orElseGet(() -> appointmentRepository.findAll());
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(app -> {
            app.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(app);
        });
    }
}
