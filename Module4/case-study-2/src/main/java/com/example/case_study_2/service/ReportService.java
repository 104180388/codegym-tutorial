package com.example.case_study_2.service;

import com.example.case_study_2.dto.PatientAdminDto;
import com.example.case_study_2.entity.Appointment;
import com.example.case_study_2.entity.Invoice;
import com.example.case_study_2.entity.Patient;
import com.example.case_study_2.entity.enums.AppointmentStatus;
import com.example.case_study_2.entity.enums.Gender;
import com.example.case_study_2.repository.AppointmentRepository;
import com.example.case_study_2.repository.DoctorRepository;
import com.example.case_study_2.repository.InvoiceRepository;
import com.example.case_study_2.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * Dashboard overview stats and 6-month trends.
     */
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal totalRevenue = invoiceRepository.calculateTotalPaidRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        long totalAppointments = appointmentRepository.count();
        stats.put("totalAppointments", totalAppointments);

        long totalPatients = patientRepository.count();
        stats.put("totalPatients", totalPatients);

        long totalDoctors = doctorRepository.count();
        stats.put("totalDoctors", totalDoctors);

        long pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING).size();
        stats.put("pendingAppointments", pendingAppointments);

        long inProgressAppointments = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED).size()
                + appointmentRepository.findByStatus(AppointmentStatus.CHECKED_IN).size()
                + appointmentRepository.findByStatus(AppointmentStatus.IN_PROGRESS).size()
                + appointmentRepository.findByStatus(AppointmentStatus.AWAITING_PAYMENT).size();
        stats.put("inProgressAppointments", inProgressAppointments);

        long completedAppointments = appointmentRepository.findByStatus(AppointmentStatus.COMPLETED).size();
        stats.put("completedAppointments", completedAppointments);

        long cancelledAppointments = appointmentRepository.findByStatus(AppointmentStatus.CANCELLED).size();
        stats.put("cancelledAppointments", cancelledAppointments);

        List<Object[]> serviceStats = appointmentRepository.countAppointmentsByService();
        stats.put("serviceStats", serviceStats);

        // Recent 6-month revenue trend
        LocalDate now = LocalDate.now();
        List<String> trendLabels = new ArrayList<>();
        List<BigDecimal> trendData = new ArrayList<>();
        List<Long> trendApptData = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            int m = targetDate.getMonthValue();
            int y = targetDate.getYear();
            trendLabels.add("T" + m + "/" + y);

            BigDecimal rev = invoiceRepository.getRevenueInMonth(y, m);
            trendData.add(rev != null ? rev : BigDecimal.ZERO);

            long appts = appointmentRepository.countAppointmentsInMonth(y, m);
            trendApptData.add(appts);
        }

        stats.put("trendLabels", trendLabels);
        stats.put("trendData", trendData);
        stats.put("trendApptData", trendApptData);

        return stats;
    }

    /**
     * Financial report filtered by time period.
     */
    public Map<String, Object> getFilteredRevenueReport(String filterType, LocalDate customStart, LocalDate customEnd) {
        Map<String, Object> report = new HashMap<>();
        LocalDate today = LocalDate.now();

        if (filterType == null || filterType.trim().isEmpty()) {
            filterType = "all";
        }

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        switch (filterType) {
            case "today":
                startDateTime = today.atStartOfDay();
                endDateTime = today.atTime(LocalTime.MAX);
                break;
            case "this_week":
                startDateTime = today.minusDays(6).atStartOfDay();
                endDateTime = today.atTime(LocalTime.MAX);
                break;
            case "this_month":
                startDateTime = today.withDayOfMonth(1).atStartOfDay();
                endDateTime = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
                break;
            case "last_month":
                LocalDate prevMonth = today.minusMonths(1);
                startDateTime = prevMonth.withDayOfMonth(1).atStartOfDay();
                endDateTime = prevMonth.withDayOfMonth(prevMonth.lengthOfMonth()).atTime(LocalTime.MAX);
                break;
            case "custom":
                if (customStart != null && customEnd != null) {
                    startDateTime = customStart.atStartOfDay();
                    endDateTime = customEnd.atTime(LocalTime.MAX);
                } else {
                    filterType = "this_month";
                    startDateTime = today.withDayOfMonth(1).atStartOfDay();
                    endDateTime = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
                }
                break;
            case "all":
            default:
                filterType = "all";
                startDateTime = LocalDate.of(2020, 1, 1).atStartOfDay();
                endDateTime = LocalDate.of(2030, 12, 31).atTime(LocalTime.MAX);
                break;
        }

        report.put("filterType", filterType);
        report.put("startDate", customStart != null ? customStart : startDateTime.toLocalDate());
        report.put("endDate", customEnd != null ? customEnd : endDateTime.toLocalDate());

        List<Invoice> invoices = invoiceRepository.findPaidInvoicesBetweenDates(startDateTime, endDateTime);
        report.put("invoices", invoices);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalServiceFee = BigDecimal.ZERO;
        BigDecimal totalSurcharge = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Invoice inv : invoices) {
            if (inv.getTotalAmount() != null) totalRevenue = totalRevenue.add(inv.getTotalAmount());
            if (inv.getServiceFee() != null) totalServiceFee = totalServiceFee.add(inv.getServiceFee());
            if (inv.getSurcharge() != null) totalSurcharge = totalSurcharge.add(inv.getSurcharge());
            if (inv.getDiscount() != null) totalDiscount = totalDiscount.add(inv.getDiscount());
        }

        report.put("totalRevenue", totalRevenue);
        report.put("totalServiceFee", totalServiceFee);
        report.put("totalSurcharge", totalSurcharge);
        report.put("totalDiscount", totalDiscount);
        report.put("totalInvoicesCount", invoices.size());

        BigDecimal avgInvoice = invoices.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(invoices.size()), 0, RoundingMode.HALF_UP);
        report.put("avgInvoiceValue", avgInvoice);

        // Payment method breakdown
        List<Object[]> paymentMethodRows = invoiceRepository.getRevenueByPaymentMethodBetween(startDateTime, endDateTime);
        List<String> paymentLabels = new ArrayList<>();
        List<BigDecimal> paymentAmounts = new ArrayList<>();
        List<Long> paymentCounts = new ArrayList<>();

        for (Object[] row : paymentMethodRows) {
            String method = String.valueOf(row[0]);
            String displayMethod = switch (method) {
                case "CASH" -> "Tiền mặt";
                case "BANK_TRANSFER" -> "Chuyển khoản";
                case "VNPAY" -> "VNPay QR";
                case "MOMO" -> "Ví MoMo";
                default -> method;
            };
            paymentLabels.add(displayMethod);
            paymentAmounts.add((BigDecimal) row[1]);
            paymentCounts.add((Long) row[2]);
        }

        report.put("paymentLabels", paymentLabels);
        report.put("paymentAmounts", paymentAmounts);
        report.put("paymentCounts", paymentCounts);

        // Service revenue breakdown
        List<Object[]> serviceRows = invoiceRepository.getRevenueByServiceBetween(startDateTime, endDateTime);
        List<String> serviceLabels = new ArrayList<>();
        List<BigDecimal> serviceAmounts = new ArrayList<>();
        List<Long> serviceCounts = new ArrayList<>();

        for (Object[] row : serviceRows) {
            serviceLabels.add(String.valueOf(row[0]));
            serviceAmounts.add((BigDecimal) row[1]);
            serviceCounts.add((Long) row[2]);
        }

        report.put("serviceLabels", serviceLabels);
        report.put("serviceAmounts", serviceAmounts);
        report.put("serviceCounts", serviceCounts);
        report.put("serviceRows", serviceRows);

        // Timeline trend for Chart.js & detailed breakdown (Sorted chronologically by LocalDate)
        Map<LocalDate, BigDecimal> timelineDateMap = new TreeMap<>();
        Map<LocalDate, Long> timelineCountMap = new TreeMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Invoice inv : invoices) {
            if (inv.getPaymentTime() != null) {
                LocalDate dateKey = inv.getPaymentTime().toLocalDate();
                timelineDateMap.put(dateKey, timelineDateMap.getOrDefault(dateKey, BigDecimal.ZERO).add(inv.getTotalAmount()));
                timelineCountMap.put(dateKey, timelineCountMap.getOrDefault(dateKey, 0L) + 1L);
            }
        }

        List<String> timelineLabels = new ArrayList<>();
        List<BigDecimal> timelineData = new ArrayList<>();
        List<Map<String, Object>> timelineBreakdown = new ArrayList<>();

        for (Map.Entry<LocalDate, BigDecimal> entry : timelineDateMap.entrySet()) {
            String label = entry.getKey().format(dtf);
            BigDecimal amount = entry.getValue();
            long count = timelineCountMap.getOrDefault(entry.getKey(), 0L);
            double pct = (totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0)
                    ? amount.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            timelineLabels.add(label);
            timelineData.add(amount);

            Map<String, Object> row = new HashMap<>();
            row.put("date", label);
            row.put("amount", amount);
            row.put("count", count);
            row.put("percentage", pct);
            timelineBreakdown.add(row);
        }

        report.put("timelineLabels", timelineLabels);
        report.put("timelineData", timelineData);
        report.put("timelineBreakdown", timelineBreakdown);

        return report;
    }

    /**
     * Month-over-Month Comparison Report.
     */
    public Map<String, Object> getMonthComparisonReport(int month1, int year1, int month2, int year2) {
        Map<String, Object> report = new HashMap<>();

        report.put("month1", month1);
        report.put("year1", year1);
        report.put("month2", month2);
        report.put("year2", year2);
        report.put("period1Label", "Tháng " + (month1 < 10 ? "0" + month1 : month1) + "/" + year1);
        report.put("period2Label", "Tháng " + (month2 < 10 ? "0" + month2 : month2) + "/" + year2);

        // 1. Period 1 KPIs
        BigDecimal rev1 = invoiceRepository.getRevenueInMonth(year1, month1);
        long invCount1 = invoiceRepository.countPaidInvoicesInMonth(year1, month1);
        long apptCount1 = appointmentRepository.countAppointmentsInMonth(year1, month1);
        long compCount1 = appointmentRepository.countCompletedAppointmentsInMonth(year1, month1);
        long cancelCount1 = appointmentRepository.countCancelledAppointmentsInMonth(year1, month1);
        BigDecimal avgPerAppt1 = apptCount1 > 0 ? rev1.divide(BigDecimal.valueOf(apptCount1), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // 2. Period 2 KPIs
        BigDecimal rev2 = invoiceRepository.getRevenueInMonth(year2, month2);
        long invCount2 = invoiceRepository.countPaidInvoicesInMonth(year2, month2);
        long apptCount2 = appointmentRepository.countAppointmentsInMonth(year2, month2);
        long compCount2 = appointmentRepository.countCompletedAppointmentsInMonth(year2, month2);
        long cancelCount2 = appointmentRepository.countCancelledAppointmentsInMonth(year2, month2);
        BigDecimal avgPerAppt2 = apptCount2 > 0 ? rev2.divide(BigDecimal.valueOf(apptCount2), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        report.put("rev1", rev1);
        report.put("rev2", rev2);
        report.put("invCount1", invCount1);
        report.put("invCount2", invCount2);
        report.put("apptCount1", apptCount1);
        report.put("apptCount2", apptCount2);
        report.put("compCount1", compCount1);
        report.put("compCount2", compCount2);
        report.put("cancelCount1", cancelCount1);
        report.put("cancelCount2", cancelCount2);
        report.put("avgPerAppt1", avgPerAppt1);
        report.put("avgPerAppt2", avgPerAppt2);

        // 3. Growth calculations
        BigDecimal revDiff = rev2.subtract(rev1);
        double revGrowthPct = rev1.compareTo(BigDecimal.ZERO) > 0
                ? revDiff.multiply(BigDecimal.valueOf(100)).divide(rev1, 1, RoundingMode.HALF_UP).doubleValue()
                : (rev2.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0);

        long apptDiff = apptCount2 - apptCount1;
        double apptGrowthPct = apptCount1 > 0 ? ((double) apptDiff * 100.0 / apptCount1) : (apptCount2 > 0 ? 100.0 : 0.0);

        long compDiff = compCount2 - compCount1;
        double compGrowthPct = compCount1 > 0 ? ((double) compDiff * 100.0 / compCount1) : (compCount2 > 0 ? 100.0 : 0.0);

        report.put("revDiff", revDiff);
        report.put("revGrowthPct", revGrowthPct);
        report.put("apptDiff", apptDiff);
        report.put("apptGrowthPct", apptGrowthPct);
        report.put("compDiff", compDiff);
        report.put("compGrowthPct", compGrowthPct);

        // 4. Service Breakdown Comparison
        List<Object[]> servRev1 = invoiceRepository.getRevenueByServiceInMonth(year1, month1);
        List<Object[]> servRev2 = invoiceRepository.getRevenueByServiceInMonth(year2, month2);
        List<Object[]> servAppt1 = appointmentRepository.countAppointmentsByServiceInMonth(year1, month1);
        List<Object[]> servAppt2 = appointmentRepository.countAppointmentsByServiceInMonth(year2, month2);

        Map<String, BigDecimal> servRevMap1 = new HashMap<>();
        servRev1.forEach(r -> servRevMap1.put((String) r[0], (BigDecimal) r[1]));
        Map<String, BigDecimal> servRevMap2 = new HashMap<>();
        servRev2.forEach(r -> servRevMap2.put((String) r[0], (BigDecimal) r[1]));

        Map<String, Long> servApptMap1 = new HashMap<>();
        servAppt1.forEach(r -> servApptMap1.put((String) r[0], (Long) r[1]));
        Map<String, Long> servApptMap2 = new HashMap<>();
        servAppt2.forEach(r -> servApptMap2.put((String) r[0], (Long) r[1]));

        Set<String> allServices = new TreeSet<>();
        allServices.addAll(servRevMap1.keySet());
        allServices.addAll(servRevMap2.keySet());
        allServices.addAll(servApptMap1.keySet());
        allServices.addAll(servApptMap2.keySet());

        List<Map<String, Object>> serviceComparisonList = new ArrayList<>();
        List<String> compServiceLabels = new ArrayList<>();
        List<BigDecimal> compServiceRev1 = new ArrayList<>();
        List<BigDecimal> compServiceRev2 = new ArrayList<>();

        for (String sName : allServices) {
            BigDecimal r1 = servRevMap1.getOrDefault(sName, BigDecimal.ZERO);
            BigDecimal r2 = servRevMap2.getOrDefault(sName, BigDecimal.ZERO);
            Long a1 = servApptMap1.getOrDefault(sName, 0L);
            Long a2 = servApptMap2.getOrDefault(sName, 0L);

            BigDecimal dDiff = r2.subtract(r1);
            double dPct = r1.compareTo(BigDecimal.ZERO) > 0
                    ? dDiff.multiply(BigDecimal.valueOf(100)).divide(r1, 1, RoundingMode.HALF_UP).doubleValue()
                    : (r2.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0);

            Map<String, Object> item = new HashMap<>();
            item.put("serviceName", sName);
            item.put("rev1", r1);
            item.put("rev2", r2);
            item.put("count1", a1);
            item.put("count2", a2);
            item.put("diff", dDiff);
            item.put("growthPct", dPct);
            serviceComparisonList.add(item);

            compServiceLabels.add(sName);
            compServiceRev1.add(r1);
            compServiceRev2.add(r2);
        }

        report.put("serviceComparisonList", serviceComparisonList);
        report.put("compServiceLabels", compServiceLabels);
        report.put("compServiceRev1", compServiceRev1);
        report.put("compServiceRev2", compServiceRev2);

        // 5. Doctor Breakdown Comparison
        List<Object[]> docRev1 = invoiceRepository.getRevenueByDoctorInMonth(year1, month1);
        List<Object[]> docRev2 = invoiceRepository.getRevenueByDoctorInMonth(year2, month2);
        List<Object[]> docAppt1 = appointmentRepository.countAppointmentsByDoctorInMonth(year1, month1);
        List<Object[]> docAppt2 = appointmentRepository.countAppointmentsByDoctorInMonth(year2, month2);
        List<Object[]> docComp1 = appointmentRepository.countCompletedAppointmentsByDoctorInMonth(year1, month1);
        List<Object[]> docComp2 = appointmentRepository.countCompletedAppointmentsByDoctorInMonth(year2, month2);

        Map<String, BigDecimal> docRevMap1 = new HashMap<>();
        docRev1.forEach(r -> docRevMap1.put((String) r[0], (BigDecimal) r[1]));
        Map<String, BigDecimal> docRevMap2 = new HashMap<>();
        docRev2.forEach(r -> docRevMap2.put((String) r[0], (BigDecimal) r[1]));

        Map<String, Long> docApptMap1 = new HashMap<>();
        docAppt1.forEach(r -> docApptMap1.put((String) r[0], (Long) r[1]));
        Map<String, Long> docApptMap2 = new HashMap<>();
        docAppt2.forEach(r -> docApptMap2.put((String) r[0], (Long) r[1]));

        Map<String, Long> docCompMap1 = new HashMap<>();
        docComp1.forEach(r -> docCompMap1.put((String) r[0], (Long) r[1]));
        Map<String, Long> docCompMap2 = new HashMap<>();
        docComp2.forEach(r -> docCompMap2.put((String) r[0], (Long) r[1]));

        Set<String> allDoctors = new TreeSet<>();
        allDoctors.addAll(docRevMap1.keySet());
        allDoctors.addAll(docRevMap2.keySet());
        allDoctors.addAll(docApptMap1.keySet());
        allDoctors.addAll(docApptMap2.keySet());

        List<Map<String, Object>> doctorComparisonList = new ArrayList<>();
        List<String> compDoctorLabels = new ArrayList<>();
        List<Long> compDoctorAppt1 = new ArrayList<>();
        List<Long> compDoctorAppt2 = new ArrayList<>();

        for (String dName : allDoctors) {
            BigDecimal r1 = docRevMap1.getOrDefault(dName, BigDecimal.ZERO);
            BigDecimal r2 = docRevMap2.getOrDefault(dName, BigDecimal.ZERO);
            Long a1 = docApptMap1.getOrDefault(dName, 0L);
            Long a2 = docApptMap2.getOrDefault(dName, 0L);
            Long c1 = docCompMap1.getOrDefault(dName, 0L);
            Long c2 = docCompMap2.getOrDefault(dName, 0L);

            BigDecimal dDiff = r2.subtract(r1);

            Map<String, Object> item = new HashMap<>();
            item.put("doctorName", dName);
            item.put("rev1", r1);
            item.put("rev2", r2);
            item.put("count1", a1);
            item.put("count2", a2);
            item.put("completed1", c1);
            item.put("completed2", c2);
            item.put("diff", dDiff);
            doctorComparisonList.add(item);

            compDoctorLabels.add(dName);
            compDoctorAppt1.add(a1);
            compDoctorAppt2.add(a2);
        }

        report.put("doctorComparisonList", doctorComparisonList);
        report.put("compDoctorLabels", compDoctorLabels);
        report.put("compDoctorAppt1", compDoctorAppt1);
        report.put("compDoctorAppt2", compDoctorAppt2);

        return report;
    }

    /**
     * Administrative patient registry with statistics.
     */
    public Map<String, Object> getPatientsAdminReport(String keyword) {
        Map<String, Object> result = new HashMap<>();

        List<Patient> patients = (keyword != null && !keyword.trim().isEmpty())
                ? patientRepository.searchByNameOrPhone(keyword.trim())
                : patientRepository.findAllWithUser();

        long totalCount = patientRepository.count();
        long hasAccountCount = 0;
        long maleCount = 0;
        long femaleCount = 0;

        List<PatientAdminDto> dtos = new ArrayList<>();

        for (Patient p : patients) {
            PatientAdminDto dto = new PatientAdminDto();
            dto.setId(p.getId());
            dto.setFullName(p.getFullName());
            dto.setPhone(p.getPhone());
            dto.setDob(p.getDob());
            dto.setGender(p.getGender());
            dto.setAddress(p.getAddress());
            dto.setAllergies(p.getAllergies());
            dto.setMedicalHistory(p.getMedicalHistory());
            dto.setCreatedAt(p.getCreatedAt());

            if (p.getUser() != null) {
                dto.setUserId(p.getUser().getId());
                dto.setUsername(p.getUser().getUsername());
                hasAccountCount++;
            }

            if (p.getGender() == Gender.MALE) maleCount++;
            else if (p.getGender() == Gender.FEMALE) femaleCount++;

            List<Invoice> pInvoices = invoiceRepository.findByPatientId(p.getId());
            BigDecimal totalSpent = BigDecimal.ZERO;

            for (Invoice inv : pInvoices) {
                if (inv.getPaymentStatus() == com.example.case_study_2.entity.enums.PaymentStatus.PAID && inv.getTotalAmount() != null) {
                    totalSpent = totalSpent.add(inv.getTotalAmount());
                }
            }

            List<Appointment> pAppts = appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(p.getId());
            long completed = 0;
            if (pAppts != null && !pAppts.isEmpty()) {
                dto.setTotalAppointments(pAppts.size());
                for (Appointment a : pAppts) {
                    if (a.getStatus() == AppointmentStatus.COMPLETED) {
                        completed++;
                    }
                }
                Appointment latest = pAppts.get(0);
                if (latest.getAppointmentDate() != null && latest.getAppointmentTime() != null) {
                    dto.setLatestAppointmentDate(LocalDateTime.of(latest.getAppointmentDate(), latest.getAppointmentTime()));
                }
            } else {
                dto.setTotalAppointments(0);
            }

            dto.setCompletedAppointments(completed);
            dto.setTotalSpent(totalSpent);

            dtos.add(dto);
        }

        long totalAppointmentsCount = 0;
        BigDecimal totalSpentAll = BigDecimal.ZERO;

        for (PatientAdminDto dto : dtos) {
            totalAppointmentsCount += dto.getTotalAppointments();
            if (dto.getTotalSpent() != null) {
                totalSpentAll = totalSpentAll.add(dto.getTotalSpent());
            }
        }

        result.put("patients", dtos);
        result.put("keyword", keyword);
        result.put("totalCount", totalCount);
        result.put("hasAccountCount", hasAccountCount);
        result.put("totalAppointmentsCount", totalAppointmentsCount);
        result.put("totalSpentAll", totalSpentAll);

        return result;
    }
}
