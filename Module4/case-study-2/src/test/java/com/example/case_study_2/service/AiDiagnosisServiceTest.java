package com.example.case_study_2.service;

import com.example.case_study_2.dto.AiDiagnoseRequestDto;
import com.example.case_study_2.dto.AiDiagnoseResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AiDiagnosisServiceTest {

    @Autowired
    private AiDiagnosisService aiDiagnosisService;

    @Autowired
    private MedicalExpertEngine medicalExpertEngine;

    @Test
    void testPharyngitisDiagnosis() {
        AiDiagnoseRequestDto request = new AiDiagnoseRequestDto(
                1L,
                "Sốt 38.5 độ C, đau rát họng khi nuốt, ho có đờm",
                "Nguyễn Văn A",
                "MALE",
                28,
                "Khám Nội Tổng Quát"
        );

        AiDiagnoseResponseDto response = aiDiagnosisService.generateDiagnosis(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getDiagnosis());
        assertFalse(response.getDiagnosis().isEmpty());
        assertNotNull(response.getTreatmentPlan());
        assertFalse(response.getTreatmentPlan().isEmpty());
        assertNotNull(response.getPrescriptionAdvice());
        assertFalse(response.getPrescriptionAdvice().isEmpty());
        assertNotNull(response.getDoctorNotes());
        assertFalse(response.getDoctorNotes().isEmpty());
    }

    @Test
    void testGastritisDiagnosis() {
        AiDiagnoseRequestDto request = new AiDiagnoseRequestDto(
                2L,
                "Đau tức vùng thượng vị, ợ chua, nóng rát dạ dày sau khi ăn",
                "Trần Thị B",
                "FEMALE",
                35,
                "Khám Tiêu Hóa"
        );

        AiDiagnoseResponseDto response = medicalExpertEngine.diagnose(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.getDiagnosis().contains("dạ dày"));
        assertTrue(response.getPrescriptionAdvice().contains("Esomeprazole") || response.getPrescriptionAdvice().contains("Phosphalugel"));
    }

    @Test
    void testEmptySymptomsValidation() {
        AiDiagnoseRequestDto request = new AiDiagnoseRequestDto();
        request.setSymptoms("");

        AiDiagnoseResponseDto response = aiDiagnosisService.generateDiagnosis(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("triệu chứng"));
    }
}
