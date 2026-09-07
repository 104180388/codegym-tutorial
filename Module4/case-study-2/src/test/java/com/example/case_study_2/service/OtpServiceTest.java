package com.example.case_study_2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    void testValidVietnamesePhoneNumbers() {
        assertTrue(otpService.isVietnamesePhoneNumberValid("0901234567")); // Mobifone
        assertTrue(otpService.isVietnamesePhoneNumberValid("0987654321")); // Viettel
        assertTrue(otpService.isVietnamesePhoneNumberValid("0888999888")); // Vinaphone
        assertTrue(otpService.isVietnamesePhoneNumberValid("0388123456")); // Viettel 038
        assertTrue(otpService.isVietnamesePhoneNumberValid("0567123456")); // Vietnamobile 056
        assertTrue(otpService.isVietnamesePhoneNumberValid("0772123456")); // Mobifone 077
    }

    @Test
    void testInvalidPhoneNumbers() {
        assertFalse(otpService.isVietnamesePhoneNumberValid("0123456789")); // Invalid prefix 012
        assertFalse(otpService.isVietnamesePhoneNumberValid("0012345678")); // Invalid prefix 001
        assertFalse(otpService.isVietnamesePhoneNumberValid("090123456"));  // 9 digits
        assertFalse(otpService.isVietnamesePhoneNumberValid("09012345678")); // 11 digits
        assertFalse(otpService.isVietnamesePhoneNumberValid("abcdefghij"));
        assertFalse(otpService.isVietnamesePhoneNumberValid(null));
    }

    @Test
    void testValidEmailDomain() {
        assertTrue(otpService.isEmailDomainValid("test@gmail.com"));
        assertTrue(otpService.isEmailDomainValid("test@yahoo.com"));
        assertTrue(otpService.isEmailDomainValid("test@outlook.com"));
    }

    @Test
    void testInvalidEmailDomain() {
        assertFalse(otpService.isEmailDomainValid("test@nonexistentdomain123456789abcxyz.com"));
        assertFalse(otpService.isEmailDomainValid("invalid-email-format"));
        assertFalse(otpService.isEmailDomainValid(null));
    }

    @Test
    void testValidateRealEmailSuccess() {
        assertDoesNotThrow(() -> otpService.validateRealEmail("sangvuxuan9856@gmail.com"));
    }

    @Test
    void testValidateRealEmailRejectsInvalidGmailPatterns() {
        // Less than 6 chars
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("abc@gmail.com"));
        // Greater than 30 chars
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("thisiswaytoolongusernameforgmail123456789@gmail.com"));
        // Invalid special character
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("invalid_user@gmail.com"));
        // Starts with dot
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail(".invaliduser@gmail.com"));
        // Consecutive dots
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("invalid..user@gmail.com"));
        // Disposable email domain
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("testuser123@mailinator.com"));
        // Non-existent domain
        assertThrows(IllegalArgumentException.class, () -> otpService.validateRealEmail("testuser123@notexistingdomain123456789abcxyz.com"));
    }

    @Test
    void testOtpGenerationAndValidation() {
        OtpService.OtpInfo otpInfo = otpService.generateOtp("user@gmail.com", "0901234567");
        assertNotNull(otpInfo.getCode());
        assertEquals(6, otpInfo.getCode().length());
        assertEquals("EMAIL", otpInfo.getChannel());
        assertEquals("user@gmail.com", otpInfo.getTargetEmail());

        assertTrue(otpService.validateOtp(otpInfo, otpInfo.getCode()));
        assertFalse(otpService.validateOtp(otpInfo, "000000"));
    }
}
