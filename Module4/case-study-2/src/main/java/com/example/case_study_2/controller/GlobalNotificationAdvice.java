package com.example.case_study_2.controller;

import com.example.case_study_2.config.CustomUserDetails;
import com.example.case_study_2.dto.NotificationDto;
import com.example.case_study_2.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalNotificationAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("systemNotifications")
    public List<NotificationDto> populateNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return Collections.emptyList();
        }
        try {
            return notificationService.getNotificationsForUser(userDetails.getUser());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
