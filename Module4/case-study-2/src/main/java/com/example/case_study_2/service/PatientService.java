package com.example.case_study_2.service;

import com.example.case_study_2.entity.Patient;
import com.example.case_study_2.entity.User;
import com.example.case_study_2.entity.enums.Gender;
import com.example.case_study_2.repository.PatientRepository;
import com.example.case_study_2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Hồ sơ bệnh nhân không tồn tại"));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.searchByNameOrPhone(keyword.trim());
    }

    @Transactional
    public Patient updatePatientProfile(Long userId, String fullName, LocalDate dob, Gender gender, String phone, String address) {
        Patient patient = getPatientByUserId(userId);
        patient.setFullName(fullName);
        patient.setDob(dob);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setAddress(address);

        User user = patient.getUser();
        if (user != null) {
            user.setFullName(fullName);
            user.setPhone(phone);
            userRepository.save(user);
        }

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient findOrCreateWalkInPatient(String fullName, String phone, LocalDate dob, Gender gender, String address) {
        Optional<Patient> existing = patientRepository.findByPhone(phone);
        if (existing.isPresent()) {
            return existing.get();
        }

        Patient patient = new Patient();
        patient.setUser(null); // Walk-in patient without online account
        patient.setFullName(fullName);
        patient.setPhone(phone);
        patient.setDob(dob != null ? dob : LocalDate.of(2000, 1, 1));
        patient.setGender(gender != null ? gender : Gender.OTHER);
        patient.setAddress(address);

        return patientRepository.save(patient);
    }
}
