package com.codegym.casestudy.controller.api;

import com.codegym.casestudy.model.entity.Doctor;
import com.codegym.casestudy.model.entity.Review;
import com.codegym.casestudy.repository.DoctorRepository;
import com.codegym.casestudy.repository.ReviewRepository;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;

    public ReviewApiController(ReviewRepository reviewRepository, DoctorRepository doctorRepository) {
        this.reviewRepository = reviewRepository;
        this.doctorRepository = doctorRepository;
    }

    @Data
    public static class ReviewDTO {
        private Long doctorId;
        private String patientName;
        private Integer rating;
        private String comment;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam(value = "doctorId", required = false) Long doctorId) {
        if (doctorId != null) {
            return ResponseEntity.ok(reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId));
        }
        return ResponseEntity.ok(reviewRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO dto, Principal principal) {
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            return ResponseEntity.badRequest().body("Đánh giá sao phải từ 1 đến 5!");
        }

        String name = dto.getPatientName();
        if ((name == null || name.isBlank()) && principal != null) {
            name = principal.getName();
        }
        if (name == null || name.isBlank()) {
            name = "Bệnh nhân ẩn danh";
        }

        Doctor doctor = null;
        if (dto.getDoctorId() != null) {
            doctor = doctorRepository.findById(dto.getDoctorId()).orElse(null);
        }

        Review review = Review.builder()
                .patientName(name)
                .doctor(doctor)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.ok(savedReview);
    }
}
