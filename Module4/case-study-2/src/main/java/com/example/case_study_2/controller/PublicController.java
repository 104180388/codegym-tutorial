package com.example.case_study_2.controller;

import com.example.case_study_2.dto.RegisterDto;
import com.example.case_study_2.service.AuthService;
import com.example.case_study_2.service.DoctorService;
import com.example.case_study_2.service.NewsService;
import com.example.case_study_2.service.OtpService;
import com.example.case_study_2.service.ServiceManagementService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {

    public static final String SESSION_PENDING_REGISTRATION = "PENDING_REGISTRATION_DTO";
    public static final String SESSION_REGISTRATION_OTP = "REGISTRATION_OTP_INFO";

    @Autowired
    private ServiceManagementService serviceManagementService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("latestNews", newsService.getLatestNews(6));
        model.addAttribute("featuredEvents", newsService.getFeaturedNews(6, 3));
        return "index";
    }

    @GetMapping("/news")
    public String newsList(Model model) {
        model.addAttribute("newsList", newsService.getAllNews());
        model.addAttribute("featuredNews", newsService.getNewsById(1L).orElse(null));
        return "news";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.example.case_study_2.dto.NewsDto news = newsService.getNewsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết tin tức yêu cầu"));
        model.addAttribute("news", news);
        model.addAttribute("relatedNews", newsService.getRelatedNews(id, 4));
        return "news-detail";
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
    public String registerForm(HttpSession session, Model model) {
        if (!model.containsAttribute("registerDto")) {
            RegisterDto existingDto = (RegisterDto) session.getAttribute(SESSION_PENDING_REGISTRATION);
            if (existingDto != null) {
                model.addAttribute("registerDto", existingDto);
            } else {
                model.addAttribute("registerDto", new RegisterDto());
            }
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Validate unique fields and business rules (including DNS MX real email domain checks)
            authService.validateRegisterDto(registerDto);

            // Generate 6-digit OTP and send to Gmail
            OtpService.OtpInfo otpInfo = otpService.generateOtp(registerDto.getEmail(), registerDto.getPhone());

            // Store pending registration & OTP in session
            session.setAttribute(SESSION_PENDING_REGISTRATION, registerDto);
            session.setAttribute(SESSION_REGISTRATION_OTP, otpInfo);

            redirectAttributes.addFlashAttribute("infoMessage",
                    "Mã OTP 6 chữ số đã được gửi tới Gmail " + registerDto.getEmail() +
                    ". Vui lòng kiểm tra hộp thư (bao gồm mục Thư rác/Spam) và nhập mã xác thực.");

            return "redirect:/verify-otp";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        RegisterDto pendingDto = (RegisterDto) session.getAttribute(SESSION_PENDING_REGISTRATION);
        OtpService.OtpInfo otpInfo = (OtpService.OtpInfo) session.getAttribute(SESSION_REGISTRATION_OTP);

        if (pendingDto == null || otpInfo == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đăng ký đang chờ. Vui lòng thực hiện đăng ký lại!");
            return "redirect:/register";
        }

        populateOtpViewModel(model, pendingDto, otpInfo);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam("otpCode") String inputOtp,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        RegisterDto pendingDto = (RegisterDto) session.getAttribute(SESSION_PENDING_REGISTRATION);
        OtpService.OtpInfo otpInfo = (OtpService.OtpInfo) session.getAttribute(SESSION_REGISTRATION_OTP);

        if (pendingDto == null || otpInfo == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại!");
            return "redirect:/register";
        }

        if (otpInfo.isExpired()) {
            model.addAttribute("errorMessage", "Mã OTP 6 chữ số đã hết hạn sau 5 phút. Vui lòng bấm 'Gửi lại mã OTP qua Gmail'!");
            populateOtpViewModel(model, pendingDto, otpInfo);
            return "verify-otp";
        }

        if (!otpService.validateOtp(otpInfo, inputOtp)) {
            model.addAttribute("errorMessage", "Mã xác thực OTP không chính xác. Vui lòng kiểm tra lại!");
            populateOtpViewModel(model, pendingDto, otpInfo);
            return "verify-otp";
        }

        try {
            // OTP is valid -> Save user & patient into Database
            authService.registerPatient(pendingDto);

            // Clean up session
            session.removeAttribute(SESSION_PENDING_REGISTRATION);
            session.removeAttribute(SESSION_REGISTRATION_OTP);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký và xác thực tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateOtpViewModel(model, pendingDto, otpInfo);
            return "verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterDto pendingDto = (RegisterDto) session.getAttribute(SESSION_PENDING_REGISTRATION);

        if (pendingDto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đăng ký. Vui lòng thực hiện đăng ký lại!");
            return "redirect:/register";
        }

        try {
            // Generate fresh OTP and send real email to Gmail
            OtpService.OtpInfo newOtpInfo = otpService.generateOtp(
                    pendingDto.getEmail(),
                    pendingDto.getPhone()
            );
            session.setAttribute(SESSION_REGISTRATION_OTP, newOtpInfo);

            redirectAttributes.addFlashAttribute("infoMessage",
                    "Mã OTP 6 chữ số mới đã được gửi lại tới Gmail " + pendingDto.getEmail() + "!");
            return "redirect:/verify-otp";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/verify-otp";
        }
    }

    private void populateOtpViewModel(Model model, RegisterDto pendingDto, OtpService.OtpInfo otpInfo) {
        model.addAttribute("maskedEmail", otpService.maskEmail(pendingDto.getEmail()));
        model.addAttribute("targetEmail", pendingDto.getEmail());
        model.addAttribute("expiresAt", otpInfo.getExpiresAt());
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
