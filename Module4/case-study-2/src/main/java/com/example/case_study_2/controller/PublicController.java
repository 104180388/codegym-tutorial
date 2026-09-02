package com.example.case_study_2.controller;

import com.example.case_study_2.dto.RegisterDto;
import com.example.case_study_2.service.AuthService;
import com.example.case_study_2.service.DoctorService;
import com.example.case_study_2.service.ServiceManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "index";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        return "services";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.registerPatient(registerDto);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
