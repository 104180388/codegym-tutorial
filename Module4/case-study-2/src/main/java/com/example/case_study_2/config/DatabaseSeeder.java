package com.example.case_study_2.config;

import com.example.case_study_2.entity.*;
import com.example.case_study_2.entity.enums.Gender;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedServices();
        seedUsersAndProfiles();
        ensureDoctorServicesSeeded();
        ensureWeeklySchedulesSeeded();
        ensureValidPasswords();
    }

    private void ensureWeeklySchedulesSeeded() {
        if (doctorRepository.count() >= 6) {
            Doctor dr1 = doctorRepository.findById(1L).orElse(null);
            Doctor dr2 = doctorRepository.findById(2L).orElse(null);
            Doctor dr3 = doctorRepository.findById(3L).orElse(null);
            Doctor dr4 = doctorRepository.findById(4L).orElse(null);
            Doctor dr5 = doctorRepository.findById(5L).orElse(null);
            Doctor dr6 = doctorRepository.findById(6L).orElse(null);

            if (dr1 != null && dr2 != null && dr3 != null && dr4 != null && dr5 != null && dr6 != null) {
                seedWeeklySchedules(dr1, dr2, dr3, dr4, dr5, dr6);
            }
        }
    }


    private void ensureDoctorServicesSeeded() {
        doctorRepository.findById(1L).ifPresent(dr1 -> {
            if (dr1.getServices().isEmpty()) {
                assignDoctorServices(dr1, 1L, 6L);
            }
        });
        doctorRepository.findById(2L).ifPresent(dr2 -> {
            if (dr2.getServices().isEmpty()) {
                assignDoctorServices(dr2, 2L);
            }
        });
        doctorRepository.findById(3L).ifPresent(dr3 -> {
            if (dr3.getServices().isEmpty()) {
                assignDoctorServices(dr3, 3L, 4L);
            }
        });
        doctorRepository.findById(4L).ifPresent(dr4 -> {
            if (dr4.getServices().isEmpty()) {
                assignDoctorServices(dr4, 5L);
            }
        });
        doctorRepository.findById(5L).ifPresent(dr5 -> {
            if (dr5.getServices().isEmpty()) {
                assignDoctorServices(dr5, 6L, 1L);
            }
        });
        doctorRepository.findById(6L).ifPresent(dr6 -> {
            if (dr6.getServices().isEmpty()) {
                assignDoctorServices(dr6, 7L);
            }
        });
    }

    private void assignDoctorServices(Doctor doctor, Long... serviceIds) {
        for (Long sId : serviceIds) {
            serviceRepository.findById(sId).ifPresent(s -> doctor.getServices().add(s));
        }
        doctorRepository.save(doctor);
    }

    private void ensureValidPasswords() {
        String defaultPassHash = passwordEncoder.encode("Password@123");
        for (User u : userRepository.findAll()) {
            if (!passwordEncoder.matches("Password@123", u.getPassword())) {
                u.setPassword(defaultPassHash);
                userRepository.save(u);
            }
        }
    }



    private void seedRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(1L, "ROLE_ADMIN", "Quản trị viên hệ thống"));
            roleRepository.save(new Role(2L, "ROLE_DOCTOR", "Bác sĩ chuyên môn"));
            roleRepository.save(new Role(3L, "ROLE_STAFF", "Nhân viên lễ tân / Thu ngân"));
            roleRepository.save(new Role(4L, "ROLE_PATIENT", "Bệnh nhân / Người dùng"));
        }
    }

    private void seedServices() {
        if (serviceRepository.count() == 0) {
            createService(1L, "GENERAL_INTERNAL", "Khám nội tổng quát",
                    "Tiếp nhận thăm khám lâm sàng đầu tiên, kiểm tra các chỉ số cơ bản (huyết áp, nhịp tim, đường huyết) và phân luồng chuyên khoa.",
                    "Nên mang theo các kết quả khám bệnh hoặc đơn thuốc cũ nếu có.",
                    new BigDecimal("150000.00"), 20);

            createService(2L, "SPECIALTY_EXAM", "Khám chuyên khoa (Tai Mũi Họng / Da Liễu / Răng Hàm Mặt)",
                    "Khám chuyên sâu theo bác sĩ và phòng ban cụ thể.",
                    "Vệ sinh sạch sẽ vùng cần khám trước khi đến phòng khám.",
                    new BigDecimal("200000.00"), 30);

            createService(3L, "BLOOD_TEST", "Xét nghiệm máu & sinh hóa (Hematology & Biochemistry)",
                    "Gói xét nghiệm chỉ số máu, chức năng gan, thận, mỡ máu; yêu cầu quản lý trạng thái chờ kết quả và đính kèm file/phiếu kết quả.",
                    "Nhịn ăn tối thiểu 8 tiếng trước khi lấy mẫu máu (chỉ được uống nước lọc).",
                    new BigDecimal("350000.00"), 15);

            createService(4L, "ULTRASOUND_XRAY", "Chẩn đoán hình ảnh (Siêu âm & X-Quang)",
                    "Quản lý lịch hẹn theo phòng máy/thiết bị chuyên dụng (tránh trùng lịch phòng chụp chiếu).",
                    "Uống nhiều nước và nhịn tiểu đối với siêu âm ổ bụng/tiểu khung.",
                    new BigDecimal("250000.00"), 25);

            createService(5L, "ENDOSCOPY", "Nội soi tiêu hóa (Dạ dày - Đại tràng)",
                    "Dịch vụ đặc thù cần lưu ý chuẩn bị trước (nhịn ăn, gây mê); thích hợp để gắn thêm hướng dẫn tự động khi bệnh nhân đặt lịch.",
                    "Nhịn ăn tuyệt đối ít nhất 6-8 tiếng, không dùng đồ uống có màu.",
                    new BigDecimal("600000.00"), 45);

            createService(6L, "VACCINATION", "Tiêm chủng & Tư vấn Vaccine",
                    "Quản lý lịch tiêm theo phác đồ, theo dõi hạn sử dụng và số lô vaccine trong kho.",
                    "Mang theo sổ tiêm chủng cá nhân và theo dõi sức khỏe trước ngày tiêm.",
                    new BigDecimal("100000.00"), 20);

            createService(7L, "NUTRITION_CONSULT", "Khám & Tư vấn dinh dưỡng",
                    "Đặt lịch tư vấn theo khung giờ cố định (slot 30-45 phút), quản lý phác đồ ăn uống và theo dõi chỉ số cơ thể qua từng lần tái khám.",
                    "Ghi lại nhật ký thói quen ăn uống trong 3 ngày gần nhất.",
                    new BigDecimal("250000.00"), 40);
        }
    }

    private void createService(Long id, String code, String name, String desc, String prep, BigDecimal price,
            int duration) {
        ServiceEntity s = new ServiceEntity();
        s.setId(id);
        s.setServiceCode(code);
        s.setName(name);
        s.setDescription(desc);
        s.setPreparationGuide(prep);
        s.setPrice(price);
        s.setEstimatedDurationMin(duration);
        s.setIsActive(true);
        serviceRepository.save(s);
    }

    private void seedUsersAndProfiles() {
        if (userRepository.count() == 0) {
            String defaultPass = passwordEncoder.encode("Password@123");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
            Role doctorRole = roleRepository.findByName("ROLE_DOCTOR").get();
            Role staffRole = roleRepository.findByName("ROLE_STAFF").get();
            Role patientRole = roleRepository.findByName("ROLE_PATIENT").get();

            // 1. Admin
            User admin = createUser(1L, adminRole, "admin", defaultPass, "admin@clinic.vn", "0901000001",
                    "Quản Trị Viên Hệ Thống");

            // 2. Staff
            User staffUser = createUser(2L, staffRole, "staff01", defaultPass, "letan@clinic.vn", "0902000002",
                    "Nguyễn Thị Mai");
            Staff staff = new Staff();
            staff.setUser(staffUser);
            staff.setStaffCode("STF-001");
            staff.setDepartment("Lễ tân & Thu ngân tiếp đón");
            staffRepository.save(staff);

            // 3. Doctors
            Doctor dr1 = createDoctor(3L, doctorRole, "dr_tuan", defaultPass, "tuan.le@clinic.vn", "0903000003",
                    "BS. CKI Lê Minh Tuấn",
                    "Bác sĩ Chuyên khoa I", 10,
                    "Hơn 10 năm kinh nghiệm khám và điều trị bệnh nội tổng quát, tim mạch, hô hấp.");

            Doctor dr2 = createDoctor(4L, doctorRole, "dr_huong", defaultPass, "huong.tran@clinic.vn", "0903000004",
                    "ThS. BS Trần Thu Hương",
                    "Thạc sĩ, Bác sĩ Chuyên khoa Tai Mũi Họng", 8,
                    "Chuyên gia chẩn đoán và điều trị bệnh lý đường hô hấp trên, viêm xoang, amidan.");

            Doctor dr3 = createDoctor(5L, doctorRole, "dr_hung", defaultPass, "hung.pham@clinic.vn", "0903000005",
                    "BS. CKII Phạm Quang Hùng",
                    "Bác sĩ Chuyên khoa II - Chẩn đoán hình ảnh", 15,
                    "Kinh nghiệm dày dặn trong siêu âm doppler tim mạch, siêu âm ổ bụng và đọc phim X-quang.");

            Doctor dr4 = createDoctor(6L, doctorRole, "dr_lan", defaultPass, "lan.nguyen@clinic.vn", "0903000006",
                    "ThS. BS Nguyễn Mai Lan",
                    "Thạc sĩ, Bác sĩ Nội soi tiêu hóa", 12,
                    "Chuyên sâu nội soi can thiệp dạ dày, đại tràng không đau, tầm soát sớm polyp và ung thư.");

            Doctor dr5 = createDoctor(7L, doctorRole, "dr_duc", defaultPass, "duc.vu@clinic.vn", "0903000007",
                    "BS. CKI Vũ Anh Đức",
                    "Bác sĩ Chuyên khoa I - Y học dự phòng", 7,
                    "Tư vấn phác đồ tiêm chủng chuẩn quốc tế cho trẻ em và người lớn, theo dõi phản ứng sau tiêm.");

            Doctor dr6 = createDoctor(8L, doctorRole, "dr_nga", defaultPass, "nga.hoang@clinic.vn", "0903000008",
                    "ThS. BS Hoàng Thúy Nga",
                    "Thạc sĩ Dinh dưỡng lâm sàng", 9,
                    "Tư vấn điều trị dinh dưỡng cho bệnh nhân tiểu đường, tim mạch, trẻ biếng ăn và quản lý cân nặng.");

            // 4. Patient (Online Account)
            User patientUser = createUser(9L, patientRole, "patient01", defaultPass, "patient01@gmail.com",
                    "0909999888", "Trần Văn Nam");
            Patient patient1 = new Patient();
            patient1.setUser(patientUser);
            patient1.setFullName("Trần Văn Nam");
            patient1.setPhone("0909999888");
            patient1.setDob(LocalDate.of(1998, 5, 15));
            patient1.setGender(Gender.MALE);
            patient1.setAddress("123 Đường Cầu Giấy, Quận Cầu Giấy, Hà Nội");
            patientRepository.save(patient1);

            // 5. Walk-in Patient
            Patient walkIn = new Patient();
            walkIn.setUser(null);
            walkIn.setFullName("Lê Thị Thu");
            walkIn.setPhone("0912345678");
            walkIn.setDob(LocalDate.of(1985, 10, 20));
            walkIn.setGender(Gender.FEMALE);
            walkIn.setAddress("456 Đường Nguyễn Trãi, Thanh Xuân, Hà Nội");
            patientRepository.save(walkIn);

            seedWeeklySchedules(dr1, dr2, dr3, dr4, dr5, dr6);
        }
    }


    private void seedWeeklySchedules(Doctor dr1, Doctor dr2, Doctor dr3, Doctor dr4, Doctor dr5, Doctor dr6) {
        LocalDate today = LocalDate.now();
        // Get Monday of current week
        LocalDate mon = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate tue = mon.plusDays(1);
        LocalDate wed = mon.plusDays(2);
        LocalDate thu = mon.plusDays(3);
        LocalDate fri = mon.plusDays(4);
        LocalDate sat = mon.plusDays(5);
        LocalDate sun = mon.plusDays(6);

        // THỨ 2
        addSchedule(dr1, mon, Shift.MORNING); addSchedule(dr1, mon, Shift.AFTERNOON);
        addSchedule(dr2, mon, Shift.MORNING);
        addSchedule(dr3, mon, Shift.MORNING); addSchedule(dr3, mon, Shift.AFTERNOON);
        addSchedule(dr4, mon, Shift.MORNING);
        addSchedule(dr5, mon, Shift.AFTERNOON);
        addSchedule(dr6, mon, Shift.AFTERNOON);

        // THỨ 3
        addSchedule(dr1, tue, Shift.MORNING); addSchedule(dr1, tue, Shift.AFTERNOON);
        addSchedule(dr2, tue, Shift.MORNING); addSchedule(dr2, tue, Shift.AFTERNOON);
        addSchedule(dr3, tue, Shift.MORNING); addSchedule(dr3, tue, Shift.AFTERNOON);
        addSchedule(dr4, tue, Shift.MORNING); addSchedule(dr4, tue, Shift.AFTERNOON);
        addSchedule(dr5, tue, Shift.MORNING);
        addSchedule(dr6, tue, Shift.AFTERNOON);

        // THỨ 4
        addSchedule(dr1, wed, Shift.MORNING); addSchedule(dr1, wed, Shift.AFTERNOON);
        addSchedule(dr3, wed, Shift.MORNING);
        addSchedule(dr4, wed, Shift.MORNING);
        addSchedule(dr5, wed, Shift.MORNING); addSchedule(dr5, wed, Shift.AFTERNOON);
        addSchedule(dr6, wed, Shift.MORNING); addSchedule(dr6, wed, Shift.AFTERNOON);

        // THỨ 5
        addSchedule(dr1, thu, Shift.MORNING);
        addSchedule(dr2, thu, Shift.MORNING); addSchedule(dr2, thu, Shift.AFTERNOON);
        addSchedule(dr3, thu, Shift.MORNING); addSchedule(dr3, thu, Shift.AFTERNOON);
        addSchedule(dr4, thu, Shift.MORNING); addSchedule(dr4, thu, Shift.AFTERNOON);
        addSchedule(dr5, thu, Shift.AFTERNOON);
        addSchedule(dr6, thu, Shift.MORNING);

        // THỨ 6
        addSchedule(dr1, fri, Shift.AFTERNOON);
        addSchedule(dr2, fri, Shift.MORNING);
        addSchedule(dr3, fri, Shift.MORNING); addSchedule(dr3, fri, Shift.AFTERNOON);
        addSchedule(dr4, fri, Shift.MORNING);
        addSchedule(dr5, fri, Shift.MORNING);
        addSchedule(dr6, fri, Shift.MORNING); addSchedule(dr6, fri, Shift.AFTERNOON);

        // THỨ 7
        addSchedule(dr1, sat, Shift.MORNING); addSchedule(dr1, sat, Shift.AFTERNOON);
        addSchedule(dr2, sat, Shift.MORNING); addSchedule(dr2, sat, Shift.AFTERNOON);
        addSchedule(dr3, sat, Shift.MORNING); addSchedule(dr3, sat, Shift.AFTERNOON);
        addSchedule(dr4, sat, Shift.MORNING); addSchedule(dr4, sat, Shift.AFTERNOON);
        addSchedule(dr5, sat, Shift.MORNING); addSchedule(dr5, sat, Shift.AFTERNOON);
        addSchedule(dr6, sat, Shift.MORNING); addSchedule(dr6, sat, Shift.AFTERNOON);

        // CHỦ NHẬT
        addSchedule(dr1, sun, Shift.MORNING);
        addSchedule(dr2, sun, Shift.MORNING);
        addSchedule(dr3, sun, Shift.MORNING);
        addSchedule(dr4, sun, Shift.MORNING);
        addSchedule(dr5, sun, Shift.MORNING); addSchedule(dr5, sun, Shift.AFTERNOON);
        addSchedule(dr6, sun, Shift.AFTERNOON);
    }

    private User createUser(Long id, Role role, String username, String password, String email, String phone,
            String fullName) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        u.setUsername(username);
        u.setPassword(password);
        u.setEmail(email);
        u.setPhone(phone);
        u.setFullName(fullName);
        u.setIsActive(true);
        return userRepository.save(u);
    }

    private Doctor createDoctor(Long id, Role role, String username, String password, String email, String phone,
            String fullName,
            String degree, int expYears, String bio) {
        User u = createUser(id, role, username, password, email, phone, fullName);
        Doctor d = new Doctor();
        d.setUser(u);
        d.setDegree(degree);
        d.setExperienceYears(expYears);
        d.setBio(bio);
        return doctorRepository.save(d);
    }

    private void addSchedule(Doctor doctor, LocalDate date, Shift shift) {
        if (scheduleRepository.findByDoctorIdAndWorkDateAndShift(doctor.getId(), date, shift).isEmpty()) {
            DoctorSchedule ds = new DoctorSchedule();
            ds.setDoctor(doctor);
            ds.setWorkDate(date);
            ds.setShift(shift);
            scheduleRepository.save(ds);
        }
    }

}
