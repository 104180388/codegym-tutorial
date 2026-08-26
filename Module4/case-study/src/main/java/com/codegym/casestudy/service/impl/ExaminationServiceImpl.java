package com.codegym.casestudy.service.impl;

import com.codegym.casestudy.model.dto.ExaminationDTO;
import com.codegym.casestudy.model.entity.*;
import com.codegym.casestudy.model.enums.AppointmentStatus;
import com.codegym.casestudy.repository.*;
import com.codegym.casestudy.service.ExaminationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExaminationServiceImpl implements ExaminationService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServicesRepository appointmentServicesRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final MedicineRepository medicineRepository;
    private final ServiceRepository serviceRepository;

    public ExaminationServiceImpl(AppointmentRepository appointmentRepository,
                                  AppointmentServicesRepository appointmentServicesRepository,
                                  MedicalRecordRepository medicalRecordRepository,
                                  PrescriptionRepository prescriptionRepository,
                                  PrescriptionItemRepository prescriptionItemRepository,
                                  MedicineRepository medicineRepository,
                                  ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServicesRepository = appointmentServicesRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.medicineRepository = medicineRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional
    public void confirmAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(app -> {
            app.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(app);
        });
    }

    @Override
    @Transactional
    public void checkInAppointment(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(app -> {
            app.setStatus(AppointmentStatus.CHECKED_IN);
            appointmentRepository.save(app);
        });
    }

    @Override
    public List<Appointment> getQueueAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentDetails(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca khám với ID: " + appointmentId));
    }

    @Override
    public MedicalRecord getMedicalRecordByAppointment(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId).orElse(null);
    }

    @Override
    @Transactional
    public MedicalRecord saveExamination(ExaminationDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn với ID: " + dto.getAppointmentId()));

        // 1. Tạo/cập nhật MedicalRecord
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointment.getId())
                .orElseGet(() -> MedicalRecord.builder()
                        .appointment(appointment)
                        .patient(appointment.getPatient())
                        .doctor(appointment.getDoctor())
                        .build());

        medicalRecord.setSymptoms(dto.getSymptoms() != null ? dto.getSymptoms() : appointment.getNotes());
        medicalRecord.setDiagnosis(dto.getDiagnosis());
        medicalRecord.setTreatmentPlan(dto.getTreatmentPlan());
        medicalRecord.setReexaminationDate(dto.getReexaminationDate());

        medicalRecord = medicalRecordRepository.save(medicalRecord);

        // 2. Thêm dịch vụ cận lâm sàng chỉ định thêm nếu có
        if (dto.getAddServiceIds() != null && !dto.getAddServiceIds().isEmpty()) {
            double additionalCost = 0.0;
            for (Long srvId : dto.getAddServiceIds()) {
                Optional<com.codegym.casestudy.model.entity.Service> srvOpt = serviceRepository.findById(srvId);
                if (srvOpt.isPresent()) {
                    com.codegym.casestudy.model.entity.Service srv = srvOpt.get();
                    AppointmentServicesId appSrvId = new AppointmentServicesId(appointment.getId(), srv.getId());
                    if (!appointmentServicesRepository.existsById(appSrvId)) {
                        AppointmentServices appSrv = AppointmentServices.builder()
                                .id(appSrvId)
                                .appointment(appointment)
                                .service(srv)
                                .quantity(1)
                                .price(srv.getPrice())
                                .build();
                        appointmentServicesRepository.save(appSrv);
                        if (srv.getPrice() != null) {
                            additionalCost += srv.getPrice();
                        }
                    }
                }
            }
            if (appointment.getTotalAmount() != null) {
                appointment.setTotalAmount(appointment.getTotalAmount() + additionalCost);
            }
        }

        // 3. Kê đơn thuốc (Prescription & PrescriptionItem)
        if (dto.getMedicineIds() != null && !dto.getMedicineIds().isEmpty()) {
            final MedicalRecord finalRecord = medicalRecord;
            Prescription prescription = prescriptionRepository.findByMedicalRecordId(finalRecord.getId())
                    .orElseGet(() -> Prescription.builder()
                            .medicalRecord(finalRecord)
                            .build());

            prescription.setInstructions(dto.getInstructions());
            prescription = prescriptionRepository.save(prescription);

            double medicineTotalCost = 0.0;

            for (int i = 0; i < dto.getMedicineIds().size(); i++) {
                Long medId = dto.getMedicineIds().get(i);
                Integer qty = (dto.getQuantities() != null && i < dto.getQuantities().size()) ? dto.getQuantities().get(i) : 1;
                String dosage = (dto.getDosages() != null && i < dto.getDosages().size()) ? dto.getDosages().get(i) : "Uống theo chỉ dẫn";

                Optional<Medicine> medOpt = medicineRepository.findById(medId);
                if (medOpt.isPresent()) {
                    Medicine med = medOpt.get();
                    double itemPrice = (med.getPrice() != null ? med.getPrice() : 0.0) * qty;

                    PrescriptionItem item = PrescriptionItem.builder()
                            .prescription(prescription)
                            .medicine(med)
                            .quantity(qty)
                            .dosage(dosage)
                            .price(itemPrice)
                            .build();

                    prescriptionItemRepository.save(item);
                    medicineTotalCost += itemPrice;
                }
            }

            prescription.setTotalCost(medicineTotalCost);
            prescriptionRepository.save(prescription);
        }

        // 4. Chuyển trạng thái Appointment thành COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return medicalRecord;
    }
}
