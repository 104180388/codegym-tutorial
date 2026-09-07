package com.example.case_study_2.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    @Test
    void testSendOtpEmailWithMissingSenderThrowsIllegalStateException() {
        EmailService emailService = new EmailService();
        assertThrows(IllegalStateException.class, () -> {
            emailService.sendOtpEmail("patient@gmail.com", "123456");
        });
    }

    @Test
    void testSendOtpEmailWithInvalidParameters() {
        EmailService emailService = new EmailService();
        assertThrows(IllegalArgumentException.class, () -> emailService.sendOtpEmail(null, "123456"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendOtpEmail("", "123456"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendOtpEmail("patient@gmail.com", null));
    }
}
