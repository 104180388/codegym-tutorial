package com.example.case_study_2.service;

import com.example.case_study_2.dto.RegisterDto;
import com.example.case_study_2.dto.UserFormDto;
import com.example.case_study_2.entity.*;
import com.example.case_study_2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerPatient(RegisterDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không khớp");
        }
        if (dto.getPhone() == null || !dto.getPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại phải bao gồm đúng 10 chữ số (bắt đầu bằng số 0, ví dụ: 0909999888)");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");
        }


        Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy vai trò ROLE_PATIENT"));

        User user = new User();
        user.setRole(patientRole);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setDob(dto.getDob());
        patientRepository.save(patient);

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
    }

    @Transactional
    public void toggleUserActiveStatus(Long id) {
        User user = getUserById(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
    }

    @Transactional
    public User createUserInternal(UserFormDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không hợp lệ"));

        User user = new User();
        user.setRole(role);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "Password@123"));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        User savedUser = userRepository.save(user);

        if ("ROLE_DOCTOR".equals(role.getName())) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctor.setDegree(dto.getDegree());
            doctor.setExperienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0);
            doctor.setBio(dto.getBio());
            doctorRepository.save(doctor);
        } else if ("ROLE_STAFF".equals(role.getName())) {
            Staff staff = new Staff();
            staff.setUser(savedUser);
            staff.setStaffCode(dto.getStaffCode() != null ? dto.getStaffCode() : "STF-" + savedUser.getId());
            staff.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : "Lễ tân / Thu ngân");
            staffRepository.save(staff);
        }

        return savedUser;
    }
}
