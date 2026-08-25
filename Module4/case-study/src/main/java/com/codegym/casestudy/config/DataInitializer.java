 package com.codegym.casestudy.config;

import com.codegym.casestudy.model.entity.Account;
import com.codegym.casestudy.model.entity.Doctor;
import com.codegym.casestudy.model.entity.Specialty;
import com.codegym.casestudy.model.enums.Role;
import com.codegym.casestudy.repository.AccountRepository;
import com.codegym.casestudy.repository.DoctorRepository;
import com.codegym.casestudy.repository.SpecialtyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AccountRepository accountRepository,
                           SpecialtyRepository specialtyRepository,
                           DoctorRepository doctorRepository,
                           PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.specialtyRepository = specialtyRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (accountRepository.count() == 0) {
            System.out.println("========== BẮT ĐẦU KHỞI TẠO SEED DATA CHÓ HỆ THỐNG MEDCARE ==========");

            // 1. Khởi tạo Chuyên khoa
            Specialty specialtyInternal = Specialty.builder()
                    .name("Khoa Nội Tổng Quát")
                    .description("Chẩn đoán, điều trị các bệnh lý nội khoa tổng hợp, tim mạch, hô hấp và tiêu hóa.")
                    .image("https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=500&auto=format&fit=crop&q=60")
                    .build();

            Specialty specialtyPediatrics = Specialty.builder()
                    .name("Khoa Nhi")
                    .description("Chăm sóc và điều trị chuyên sâu các bệnh lý nhi khoa cho trẻ em từ sơ sinh đến 15 tuổi.")
                    .image("https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=500&auto=format&fit=crop&q=60")
                    .build();

            specialtyInternal = specialtyRepository.save(specialtyInternal);
            specialtyPediatrics = specialtyRepository.save(specialtyPediatrics);

            // 2. Khởi tạo Tài khoản ADMIN
            Account adminAccount = Account.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Quản trị viên Hệ thống")
                    .email("admin@medcare.com")
                    .phone("0901234567")
                    .gender("Nam")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .address("Hà Nội")
                    .role(Role.ROLE_ADMIN)
                    .active(true)
                    .build();
            accountRepository.save(adminAccount);
            System.out.println("-> Đã khởi tạo Admin Account: admin / password123");

            // 3. Khởi tạo Tài khoản DOCTOR (STAFF)
            Account doctorAccount = Account.builder()
                    .username("doctor")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("BS. Nguyễn Văn An")
                    .email("doctor.an@medcare.com")
                    .phone("0908888888")
                    .gender("Nam")
                    .dateOfBirth(LocalDate.of(1985, 5, 15))
                    .address("TP. Hồ Chí Minh")
                    .role(Role.ROLE_STAFF)
                    .active(true)
                    .build();
            doctorAccount = accountRepository.save(doctorAccount);

            Doctor doctor = Doctor.builder()
                    .account(doctorAccount)
                    .specialty(specialtyInternal)
                    .position("Bác sĩ Chuyên khoa II")
                    .degree("Tiến sĩ Y học")
                    .experienceYears(12)
                    .bio("Bác sĩ Nguyễn Văn An có 12 năm kinh nghiệm trong lĩnh vực khám nội khoa tổng quát và tim mạch.")
                    .consultationFee(300000.0)
                    .build();
            doctorRepository.save(doctor);
            System.out.println("-> Đã khởi tạo Doctor Account: doctor / password123");

            // 4. Khởi tạo Tài khoản PATIENT
            Account patientAccount = Account.builder()
                    .username("patient")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Trần Thị Bình")
                    .email("patient.binh@gmail.com")
                    .phone("0912345678")
                    .gender("Nữ")
                    .dateOfBirth(LocalDate.of(1995, 8, 20))
                    .address("Đà Nẵng")
                    .role(Role.ROLE_PATIENT)
                    .active(true)
                    .build();
            accountRepository.save(patientAccount);
            System.out.println("-> Đã khởi tạo Patient Account: patient / password123");

            System.out.println("========== HOÀN TẤT KHỞI TẠO SEED DATA MEDCARE! ==========");
        }
    }
}
