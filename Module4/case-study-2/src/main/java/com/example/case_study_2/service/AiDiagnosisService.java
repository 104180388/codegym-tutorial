package com.example.case_study_2.service;

import com.example.case_study_2.dto.AiDiagnoseRequestDto;
import com.example.case_study_2.dto.AiDiagnoseResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class AiDiagnosisService {

    @Autowired
    private MedicalExpertEngine medicalExpertEngine;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    public AiDiagnoseResponseDto generateDiagnosis(AiDiagnoseRequestDto request) {
        if (request.getSymptoms() == null || request.getSymptoms().trim().isEmpty()) {
            return AiDiagnoseResponseDto.error("Vui lòng nhập triệu chứng lâm sàng trước khi thực hiện chẩn đoán AI!");
        }

        // Try free online generative AI API (Pollinations AI) with fallback to MedicalExpertEngine
        try {
            String prompt = buildMedicalPrompt(request);
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            String url = "https://text.pollinations.ai/" + encodedPrompt + "?json=true&model=openai";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(4))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null) {
                String rawBody = response.body().trim();
                // Strip markdown code fences if any
                if (rawBody.startsWith("```json")) {
                    rawBody = rawBody.substring(7);
                } else if (rawBody.startsWith("```")) {
                    rawBody = rawBody.substring(3);
                }
                if (rawBody.endsWith("```")) {
                    rawBody = rawBody.substring(0, rawBody.length() - 3);
                }
                rawBody = rawBody.trim();

                JsonNode root = objectMapper.readTree(rawBody);
                if (root.has("diagnosis")) {
                    String diagnosis = root.path("diagnosis").asText("");
                    String treatmentPlan = root.path("treatmentPlan").asText("");
                    String prescriptionAdvice = root.path("prescriptionAdvice").asText("");
                    String doctorNotes = root.path("doctorNotes").asText("");

                    if (!diagnosis.isEmpty()) {
                        return AiDiagnoseResponseDto.success(diagnosis, treatmentPlan, prescriptionAdvice, doctorNotes, "AI-Generative-Cloud");
                    }
                }
            }
        } catch (Exception e) {
            // Log & gracefully fall back to our high-precision local clinical expert engine
            System.out.println("[AI Diagnosis Service] Online API fallback to Expert Engine: " + e.getMessage());
        }

        // Fast, deterministic, clinically sound fallback engine
        return medicalExpertEngine.diagnose(request);
    }

    private String buildMedicalPrompt(AiDiagnoseRequestDto req) {
        return "Bạn là trợ lý bác sĩ y khoa thông minh. Hãy phân tích triệu chứng của bệnh nhân sau và trả về DUY NHẤT một chuỗi JSON hợp lệ không kèm văn bản giải thích nào khác:\n" +
                "Thông tin bệnh nhân: " + (req.getPatientName() != null ? req.getPatientName() : "Bệnh nhân") +
                ", Giới tính: " + (req.getGender() != null ? req.getGender() : "Không xác định") +
                ", Tuổi: " + (req.getAge() != null ? req.getAge() : "30") +
                ", Dịch vụ khám: " + (req.getServiceName() != null ? req.getServiceName() : "Khám bệnh") + "\n" +
                "Triệu chứng lâm sàng: " + req.getSymptoms() + "\n\n" +
                "Định dạng JSON yêu cầu chính xác như sau:\n" +
                "{\n" +
                "  \"diagnosis\": \"Chẩn đoán bệnh lý chi tiết bằng tiếng Việt\",\n" +
                "  \"treatmentPlan\": \"Hướng điều trị & phác đồ, chế độ ăn uống sinh hoạt chi tiết\",\n" +
                "  \"prescriptionAdvice\": \"Toa thuốc khuyên dùng gồm: tên thuốc, hàm lượng, số lượng, liều lượng cách dùng (dặn tự mua tại nhà thuốc ngoài)\",\n" +
                "  \"doctorNotes\": \"Lời dặn dò tái khám hoặc các dấu hiệu cảnh báo khẩn cấp cần đi viện\"\n" +
                "}";
    }
}
