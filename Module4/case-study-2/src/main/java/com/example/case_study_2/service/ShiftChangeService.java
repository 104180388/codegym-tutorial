package com.example.case_study_2.service;

import com.example.case_study_2.entity.Doctor;
import com.example.case_study_2.entity.DoctorSchedule;
import com.example.case_study_2.entity.ShiftChangeRequest;
import com.example.case_study_2.entity.User;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.entity.enums.ShiftChangeType;
import com.example.case_study_2.entity.enums.ShiftRequestStatus;
import com.example.case_study_2.repository.DoctorRepository;
import com.example.case_study_2.repository.DoctorScheduleRepository;
import com.example.case_study_2.repository.ShiftChangeRequestRepository;
import com.example.case_study_2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftChangeService {

    @Autowired
    private ShiftChangeRequestRepository requestRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private com.example.case_study_2.repository.AppointmentRepository appointmentRepository;

    @Transactional
    public ShiftChangeRequest createLeaveRequest(Long doctorId, Long scheduleId, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin bác sĩ"));

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc đã chọn"));

        if (!schedule.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Ca làm việc này không thuộc về bạn");
        }

        LocalDate today = LocalDate.now();
        LocalDate endOfNextWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).plusWeeks(1);

        if (schedule.getWorkDate().isBefore(today)) {
            throw new IllegalArgumentException("Không thể xin nghỉ ca trực cho các ngày đã qua");
        }

        if (schedule.getWorkDate().isAfter(endOfNextWeek)) {
            throw new IllegalArgumentException("Chỉ được phép xin nghỉ các ca trực trong tuần này và tuần sau (đến hết Chủ nhật tuần sau)");
        }

        // Check if there are already booked patient appointments for this shift
        long bookedAppointmentsCount = appointmentRepository.countBookedAppointmentsForDoctorAndShift(
                doctorId, schedule.getWorkDate(), schedule.getShift());
        if (bookedAppointmentsCount > 0) {
            throw new IllegalArgumentException("Không thể xin nghỉ ca trực ngày " 
                    + schedule.getWorkDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                    + " (" + schedule.getShift().getDisplayName() + ") vì đã có " + bookedAppointmentsCount + " bệnh nhân đặt lịch khám.");
        }

        // Check if there is already a PENDING request for this schedule
        Optional<ShiftChangeRequest> existingPending = requestRepository.findByDoctorIdAndScheduleIdAndStatus(
                doctorId, scheduleId, ShiftRequestStatus.PENDING);
        if (existingPending.isPresent()) {
            throw new IllegalArgumentException("Bạn đã có một yêu cầu xin nghỉ đang chờ phê duyệt cho ca trực này");
        }

        ShiftChangeRequest request = new ShiftChangeRequest();
        request.setDoctor(doctor);
        request.setSchedule(schedule);
        request.setCurrentDate(schedule.getWorkDate());
        request.setCurrentShift(schedule.getShift());
        request.setRequestType(ShiftChangeType.LEAVE);
        request.setReason(reason);
        request.setStatus(ShiftRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Transactional
    public void cancelRequest(Long requestId, Long doctorId) {
        ShiftChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));

        if (!request.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy yêu cầu này");
        }

        if (request.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("Chỉ có thể hủy yêu cầu đang ở trạng thái Chờ phê duyệt");
        }

        request.setStatus(ShiftRequestStatus.CANCELLED);
        requestRepository.save(request);
    }

    @Transactional
    public void approveRequest(Long requestId, Long adminUserId, String adminNotes) {
        ShiftChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý trước đó");
        }

        User adminUser = userRepository.findById(adminUserId).orElse(null);
        Doctor doctor = request.getDoctor();

        // Find the schedule to delete
        DoctorSchedule scheduleToDelete = request.getSchedule();
        if (scheduleToDelete == null) {
            scheduleToDelete = scheduleRepository.findByDoctorIdAndWorkDateAndShift(
                    doctor.getId(), request.getCurrentDate(), request.getCurrentShift()).orElse(null);
        }

        if (scheduleToDelete != null) {
            // Unlink all shift_change_requests referencing this schedule_id to satisfy foreign key constraint
            requestRepository.unlinkScheduleByScheduleId(scheduleToDelete.getId());
            scheduleRepository.delete(scheduleToDelete);
        }

        request.setSchedule(null);
        request.setStatus(ShiftRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminUser);
        request.setAdminNotes(adminNotes);

        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long requestId, Long adminUserId, String adminNotes) {
        ShiftChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));

        if (request.getStatus() != ShiftRequestStatus.PENDING) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý trước đó");
        }

        User adminUser = userRepository.findById(adminUserId).orElse(null);

        request.setStatus(ShiftRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminUser);
        request.setAdminNotes(adminNotes);

        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<ShiftChangeRequest> getDoctorRequests(Long doctorId) {
        return requestRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    @Transactional(readOnly = true)
    public List<ShiftChangeRequest> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<ShiftChangeRequest> getRequestsByStatus(ShiftRequestStatus status) {
        return requestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public long getPendingCount() {
        return requestRepository.countByStatus(ShiftRequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public ShiftChangeRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu ID: " + id));
    }
}
