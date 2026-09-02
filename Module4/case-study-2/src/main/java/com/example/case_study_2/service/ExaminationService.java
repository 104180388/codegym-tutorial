package com.example.case_study_2.service;

import com.example.case_study_2.dto.ExaminationDto;
import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.ExaminationRecord;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.repository.AppointmentRepository;
import com.example.case_study_2.repository.ExaminationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExaminationService {

    @Autowired
    private ExaminationRecordRepository recordRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Optional<ExaminationRecord> getRecordByAppointmentId(Long appointmentId) {
        return recordRepository.findByAppointmentId(appointmentId);
    }

    public ExaminationRecord getRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kết quả bệnh án / đơn thuốc"));
    }

    public List<ExaminationRecord> getMedicalHistoryByPatientId(Long patientId) {
        return recordRepository.findByPatientId(patientId);
    }

    @Transactional
    public ExaminationRecord saveExaminationRecord(ExaminationDto dto) {
        Appointment app = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch hẹn"));

        ExaminationRecord record = recordRepository.findByAppointmentId(dto.getAppointmentId())
                .orElse(new ExaminationRecord());

        record.setAppointment(app);
        record.setDiagnosis(dto.getDiagnosis());
        record.setTreatmentPlan(dto.getTreatmentPlan());
        record.setPrescriptionAdvice(dto.getPrescriptionAdvice());
        if (dto.getResultAttachmentUrl() != null && !dto.getResultAttachmentUrl().isEmpty()) {
            record.setResultAttachmentUrl(dto.getResultAttachmentUrl());
        }
        record.setDoctorNotes(dto.getDoctorNotes());

        ExaminationRecord saved = recordRepository.save(record);

        // Transition appointment status to AWAITING_PAYMENT
        app.setStatus(AppointmentStatus.AWAITING_PAYMENT);
        appointmentRepository.save(app);

        return saved;
    }
}
