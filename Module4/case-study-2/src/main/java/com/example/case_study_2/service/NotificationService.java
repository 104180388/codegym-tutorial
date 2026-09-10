package com.example.case_study_2.service;

import com.example.case_study_2.dto.NotificationDto;
import com.example.case_study_2.entity.*;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.PaymentStatus;
import com.example.case_study_2.entity.enums.Shift;
import com.example.case_study_2.entity.enums.ShiftRequestStatus;
import com.example.case_study_2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ShiftChangeRequestRepository shiftChangeRequestRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsForUser(User user) {
        if (user == null || user.getRole() == null) {
            return Collections.emptyList();
        }

        List<NotificationDto> list = new ArrayList<>();
        String roleName = user.getRole().getName();
        LocalDate today = LocalDate.now();

        if ("ROLE_ADMIN".equals(roleName)) {
            // 1. Pending Doctor Shift Leave Requests
            List<ShiftChangeRequest> pendingRequests = shiftChangeRequestRepository.findByStatusOrderByCreatedAtDesc(ShiftRequestStatus.PENDING);
            if (!pendingRequests.isEmpty()) {
                for (ShiftChangeRequest r : pendingRequests) {
                    String docName = (r.getDoctor() != null && r.getDoctor().getUser() != null) ? r.getDoctor().getUser().getFullName() : "Bác sĩ";
                    String shiftName = (r.getCurrentShift() == Shift.MORNING) ? "Ca Sáng" : "Ca Chiều";
                    list.add(new NotificationDto(
                            "admin_leave_pending_" + r.getId(),
                            "Đơn xin nghỉ ca chờ duyệt",
                            "BS. " + docName + " xin nghỉ " + shiftName + " ngày " + r.getCurrentDate().format(DATE_FMT) + ". Lý do: " + r.getReason(),
                            "/admin/shift-requests?status=PENDING",
                            "Mới",
                            "WARNING",
                            "fa-solid fa-calendar-xmark"
                    ));
                }
            }

            // 2. Pending appointments needing approval/assignment
            List<Appointment> pendingAppointments = appointmentRepository.findPendingAppointments();
            if (!pendingAppointments.isEmpty()) {
                list.add(new NotificationDto(
                        "admin_pending_apps_count_" + pendingAppointments.size(),
                        "Lịch hẹn khám mới trực tuyến",
                        "Có " + pendingAppointments.size() + " lịch hẹn khám bệnh trực tuyến đang chờ tiếp nhận và xử lý.",
                        "/admin/dashboard",
                        "Hôm nay",
                        "PRIMARY",
                        "fa-solid fa-calendar-plus"
                ));
            }

        } else if ("ROLE_DOCTOR".equals(roleName)) {
            Doctor doctor = doctorRepository.findByUserId(user.getId()).orElse(null);
            if (doctor != null) {
                // 1. Patients waiting in queue for this doctor today
                List<Appointment> waitingInQueue = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                        doctor.getId(), today, List.of(AppointmentStatus.CHECKED_IN));
                if (!waitingInQueue.isEmpty()) {
                    list.add(new NotificationDto(
                            "doc_queue_waiting_" + waitingInQueue.size(),
                            "Bệnh nhân đang chờ khám",
                            "Có " + waitingInQueue.size() + " bệnh nhân đã check-in đang đợi tại phòng khám của bạn hôm nay.",
                            "/doctor/queue",
                            "Hôm nay",
                            "PRIMARY",
                            "fa-solid fa-hospital-user"
                    ));
                }

                // 2. Doctor's shift leave requests processed by Admin (APPROVED / REJECTED)
                List<ShiftChangeRequest> processedRequests = shiftChangeRequestRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId()).stream()
                        .filter(r -> r.getStatus() == ShiftRequestStatus.APPROVED || r.getStatus() == ShiftRequestStatus.REJECTED)
                        .limit(5)
                        .toList();

                for (ShiftChangeRequest r : processedRequests) {
                    boolean approved = (r.getStatus() == ShiftRequestStatus.APPROVED);
                    String shiftName = (r.getCurrentShift() == Shift.MORNING) ? "Ca Sáng" : "Ca Chiều";
                    list.add(new NotificationDto(
                            "doc_leave_result_" + r.getId() + "_" + r.getStatus().name(),
                            "Kết quả đơn xin nghỉ ca #" + r.getId(),
                            "Đơn xin nghỉ " + shiftName + " ngày " + r.getCurrentDate().format(DATE_FMT) 
                                    + " đã được Admin " + (approved ? "PHÊ DUYỆT thành công." : "TỪ CHỐI.")
                                    + (r.getAdminNotes() != null && !r.getAdminNotes().isEmpty() ? " Phản hồi: " + r.getAdminNotes() : ""),
                            "/doctor/shift-requests",
                            "Mới",
                            approved ? "SUCCESS" : "DANGER",
                            approved ? "fa-solid fa-circle-check" : "fa-solid fa-circle-xmark"
                    ));
                }

                // 3. Upcoming appointments for today
                List<Appointment> todayApps = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                        doctor.getId(), today, List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.IN_PROGRESS));
                if (!todayApps.isEmpty()) {
                    list.add(new NotificationDto(
                            "doc_today_confirmed_" + todayApps.size(),
                            "Lịch khám bệnh hôm nay",
                            "Bạn có " + todayApps.size() + " cuộc hẹn khám bệnh đã lên lịch trong ngày hôm nay.",
                            "/doctor/appointments",
                            "Hôm nay",
                            "PRIMARY",
                            "fa-solid fa-calendar-check"
                    ));
                }
            }

        } else if ("ROLE_STAFF".equals(roleName)) {
            // 1. Pending check-in patients for today
            List<Appointment> pendingCheckins = appointmentRepository.findByDoctorIdAndAppointmentDate(null, today).stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED)
                    .toList();
            if (!pendingCheckins.isEmpty()) {
                list.add(new NotificationDto(
                        "staff_checkin_needed_" + pendingCheckins.size(),
                        "Bệnh nhân cần check-in tiếp đón",
                        "Có " + pendingCheckins.size() + " bệnh nhân cần tiếp đón và xác nhận check-in hôm nay.",
                        "/staff/checkin",
                        "Hôm nay",
                        "PRIMARY",
                        "fa-solid fa-door-open"
                ));
            }

            // 2. Unpaid invoices / prescriptions waiting for billing
            List<Invoice> unpaidInvoices = invoiceRepository.findByPaymentStatus(PaymentStatus.UNPAID);
            if (!unpaidInvoices.isEmpty()) {
                list.add(new NotificationDto(
                        "staff_billing_unpaid_" + unpaidInvoices.size(),
                        "Yêu cầu thanh toán viện phí",
                        "Có " + unpaidInvoices.size() + " hóa đơn/toa thuốc đang chờ thu ngân tiếp nhận thanh toán.",
                        "/staff/billing",
                        "Mới",
                        "SUCCESS",
                        "fa-solid fa-file-invoice-dollar"
                ));
            }

        } else if ("ROLE_PATIENT".equals(roleName)) {
            Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
            if (patient != null) {
                // 1. Active & upcoming appointments
                List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patient.getId());
                for (Appointment a : appointments) {
                    if (a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED) {
                        String docName = (a.getDoctor() != null && a.getDoctor().getUser() != null) ? a.getDoctor().getUser().getFullName() : "Bác sĩ phụ trách";
                        list.add(new NotificationDto(
                                "pat_app_status_" + a.getId() + "_" + a.getStatus().name(),
                                "Lịch hẹn khám: " + a.getAppointmentCode(),
                                "Ngày khám: " + a.getAppointmentDate().format(DATE_FMT) + " lúc " + a.getAppointmentTime() 
                                        + " - BS: " + docName + " - Trạng thái: " + a.getStatus().getDisplayName(),
                                "/patient/appointments",
                                "Đang xử lý",
                                "PRIMARY",
                                "fa-solid fa-calendar-check"
                        ));
                    } else if (a.getStatus() == AppointmentStatus.COMPLETED) {
                        // Recently completed appointment results
                        if (a.getAppointmentDate() != null && !a.getAppointmentDate().isBefore(today.minusDays(7))) {
                            list.add(new NotificationDto(
                                    "pat_med_completed_" + a.getId(),
                                    "Kết quả khám & Đơn thuốc",
                                    "Kết quả khám ngày " + a.getAppointmentDate().format(DATE_FMT) + " (" + a.getService().getName() + ") đã hoàn thành. Nhấn để xem chi tiết bệnh án.",
                                    "/patient/medical-history",
                                    "Đã hoàn thành",
                                    "SUCCESS",
                                    "fa-solid fa-file-medical"
                            ));
                        }
                    }
                }

                // 2. Unpaid patient invoices
                List<Invoice> patientInvoices = invoiceRepository.findByPatientId(patient.getId()).stream()
                        .filter(i -> i.getPaymentStatus() == PaymentStatus.UNPAID)
                        .toList();
                for (Invoice inv : patientInvoices) {
                    list.add(new NotificationDto(
                            "pat_unpaid_invoice_" + inv.getId(),
                            "Hóa đơn viện phí chưa thanh toán",
                            "Hóa đơn #" + inv.getInvoiceCode() + " với số tiền " + String.format("%,.0f VNĐ", inv.getTotalAmount()) + " đang chờ thanh toán.",
                            "/patient/invoices",
                            "Chờ thanh toán",
                            "WARNING",
                            "fa-solid fa-receipt"
                    ));
                }
            }
        }

        return list;
    }
}
