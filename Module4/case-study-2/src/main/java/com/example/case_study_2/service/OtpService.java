package com.example.case_study_2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom random = new SecureRandom();

    @Autowired(required = false)
    private EmailService emailService;

    // Regex for valid 10-digit Vietnamese mobile carrier prefixes:
    // Viettel: 086, 096, 097, 098, 032-039
    // Mobifone: 089, 090, 093, 070, 079, 077, 076, 078
    // Vinaphone: 088, 091, 094, 081-085
    // Vietnamobile: 092, 056, 058
    // Gmobile / Wintel / Itelecom / FPT: 099, 059, 055, 087, 0775
    private static final Pattern VN_MOBILE_PATTERN = Pattern.compile(
            "^(03[2-9]|05[25689]|07[06-9]|08[1-9]|09[0-9])\\d{7}$"
    );

    private static final Pattern EMAIL_SYNTAX_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    private static final Set<String> DISPOSABLE_EMAIL_DOMAINS = new HashSet<>(Arrays.asList(
            "mailinator.com", "guerrillamail.com", "10minutemail.com", "tempmail.com",
            "throwawaymail.com", "yopmail.com", "sharklasers.com", "getairmail.com",
            "dispostable.com", "mohmal.com", "crazymailing.com", "fakemailgenerator.com",
            "trashmail.com", "maildrop.cc", "nada.ltd", "temp-mail.org", "mytemp.email"
    ));

    public static class OtpInfo implements Serializable {
        private String code;
        private LocalDateTime expiresAt;
        private String targetEmail;
        private String targetPhone;
        private String channel; // "EMAIL" or "PHONE"

        public OtpInfo() {
        }

        public OtpInfo(String code, LocalDateTime expiresAt, String targetEmail, String targetPhone, String channel) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.targetEmail = targetEmail;
            this.targetPhone = targetPhone;
            this.channel = (channel != null && channel.equalsIgnoreCase("PHONE")) ? "PHONE" : "EMAIL";
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public String getTargetEmail() {
            return targetEmail;
        }

        public String getTargetPhone() {
            return targetPhone;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getDestinationText() {
            return "PHONE".equalsIgnoreCase(channel) ? targetPhone : targetEmail;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    /**
     * Comprehensive real-life email validation:
     * 1. Standard syntax regex
     * 2. Disposable / temporary email domain block
     * 3. Specific Google/Gmail account rules (length 6-30, no consecutive dots, valid characters)
     * 4. DNS MX record resolution
     * 5. Direct SMTP handshake with recipient MX server (port 25)
     */
    public void validateRealEmail(String email) {
        if (email == null || !EMAIL_SYNTAX_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Địa chỉ email không đúng định dạng chuẩn.");
        }

        String trimmed = email.trim().toLowerCase();
        String[] parts = trimmed.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Địa chỉ email không hợp lệ.");
        }

        String username = parts[0];
        String domain = parts[1];

        // 1. Check for disposable temporary email domains
        if (DISPOSABLE_EMAIL_DOMAINS.contains(domain)) {
            throw new IllegalArgumentException("Hệ thống không chấp nhận địa chỉ email tạm thời / rác. Vui lòng sử dụng Gmail có thật của bạn.");
        }

        // 2. Enforce strict Gmail naming policies
        if ("gmail.com".equalsIgnoreCase(domain) || "googlemail.com".equalsIgnoreCase(domain)) {
            if (username.length() < 6 || username.length() > 30) {
                throw new IllegalArgumentException("Địa chỉ Gmail không hợp lệ: Tên người dùng Gmail phải có từ 6 đến 30 ký tự.");
            }
            if (!username.matches("^[a-z0-9.]+$")) {
                throw new IllegalArgumentException("Địa chỉ Gmail không hợp lệ: Tên tài khoản chỉ được chứa chữ cái (a-z), số (0-9) và dấu chấm (không chứa ký tự đặc biệt).");
            }
            if (username.startsWith(".") || username.endsWith(".")) {
                throw new IllegalArgumentException("Địa chỉ Gmail không hợp lệ: Dấu chấm không được nằm ở đầu hoặc cuối tên tài khoản.");
            }
            if (username.contains("..")) {
                throw new IllegalArgumentException("Địa chỉ Gmail không hợp lệ: Không được có hai dấu chấm liền nhau.");
            }
        }

        // 3. DNS MX Record Resolution
        List<String> mxHosts = getMxHosts(domain);
        if (mxHosts.isEmpty()) {
            if (!isEmailDomainValid(trimmed)) {
                throw new IllegalArgumentException("Tên miền email '" + domain + "' không tồn tại ngoài đời thật hoặc không có máy chủ nhận thư.");
            }
        }

        // 4. Direct SMTP Handshake with Recipient Mail Server
        Boolean mailboxExists = checkSmtpMailboxExists(trimmed, mxHosts);
        if (Boolean.FALSE.equals(mailboxExists)) {
            throw new IllegalArgumentException("Địa chỉ email '" + email + "' không tồn tại trên máy chủ " + domain + " (Tài khoản không có thật ngoài đời). Vui lòng kiểm tra lại chính xác.");
        }
    }

    /**
     * Resolves MX hosts for the given domain using DNS.
     */
    public List<String> getMxHosts(String domain) {
        List<String> mxList = new ArrayList<>();
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("com.sun.jndi.dns.timeout.initial", "3000");
            env.put("com.sun.jndi.dns.timeout.retries", "1");

            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");
            if (attr != null) {
                for (int i = 0; i < attr.size(); i++) {
                    String mxRecord = (String) attr.get(i);
                    String[] tokenParts = mxRecord.split("\\s+");
                    String host = tokenParts.length > 1 ? tokenParts[1] : tokenParts[0];
                    if (host.endsWith(".")) {
                        host = host.substring(0, host.length() - 1);
                    }
                    mxList.add(host);
                }
            }
        } catch (Exception e) {
            logger.debug("[OTP SERVICE] Không thể truy vấn MX cho domain {}: {}", domain, e.getMessage());
        }
        return mxList;
    }

    /**
     * Performs direct SMTP handshake with recipient MX server to verify mailbox existence.
     */
    public Boolean checkSmtpMailboxExists(String email, List<String> mxHosts) {
        if (mxHosts == null || mxHosts.isEmpty()) {
            return null;
        }

        for (String mxHost : mxHosts) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(mxHost, 25), 3000);
                socket.setSoTimeout(3000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

                String banner = readSmtpResponse(reader);
                if (!banner.startsWith("220")) {
                    continue;
                }

                writer.write("HELO medcare.vn\r\n");
                writer.flush();
                String heloResp = readSmtpResponse(reader);
                if (!heloResp.startsWith("250")) {
                    continue;
                }

                writer.write("MAIL FROM:<verify@medcare.vn>\r\n");
                writer.flush();
                String mailResp = readSmtpResponse(reader);
                if (!mailResp.startsWith("250")) {
                    continue;
                }

                writer.write("RCPT TO:<" + email + ">\r\n");
                writer.flush();
                String rcptResp = readSmtpResponse(reader);

                try {
                    writer.write("QUIT\r\n");
                    writer.flush();
                } catch (Exception ignored) {}

                if (rcptResp.startsWith("250")) {
                    logger.info("[OTP SERVICE] SMTP Handshake: Hòm thư '{}' tồn tại thực tế (250 OK)", email);
                    return true;
                } else if (rcptResp.startsWith("550") || rcptResp.startsWith("551") || rcptResp.startsWith("552")
                        || rcptResp.startsWith("553") || rcptResp.startsWith("554") || rcptResp.startsWith("501")) {
                    logger.warn("[OTP SERVICE] SMTP Handshake: Hòm thư '{}' KHÔNG TỒN TẠI (Máy chủ phản hồi: {})", email, rcptResp.trim());
                    return false;
                }
            } catch (Exception e) {
                logger.debug("[OTP SERVICE] SMTP Handshake với host '{}' gặp lỗi/timeout: {}", mxHost, e.getMessage());
            }
        }
        return null; // Indeterminate
    }

    private String readSmtpResponse(BufferedReader reader) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append(" ");
            if (line.length() >= 4 && line.charAt(3) == ' ') {
                break;
            }
        }
        return response.toString();
    }

    /**
     * Checks whether an email domain exists in DNS and has valid MX/A records.
     */
    public boolean isEmailDomainValid(String email) {
        if (email == null || !EMAIL_SYNTAX_PATTERN.matcher(email.trim()).matches()) {
            return false;
        }

        String domain = email.substring(email.indexOf('@') + 1).trim();
        if (domain.isEmpty() || !domain.contains(".")) {
            return false;
        }

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("com.sun.jndi.dns.timeout.initial", "3000"); // 3s timeout
            env.put("com.sun.jndi.dns.timeout.retries", "1");

            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            if (attr == null || attr.size() == 0) {
                // Fallback to check A record (some mail servers accept fallback to domain A record)
                attrs = ictx.getAttributes(domain, new String[]{"A"});
                attr = attrs.get("A");
                return attr != null && attr.size() > 0;
            }
            return true;
        } catch (Exception e) {
            logger.warn("[OTP SERVICE] Kiểm tra tên miền email '{}' thất bại: {}", domain, e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a phone number belongs to valid Vietnamese mobile carrier prefixes.
     */
    public boolean isVietnamesePhoneNumberValid(String phone) {
        if (phone == null) {
            return false;
        }
        return VN_MOBILE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Generates a secure 6-digit OTP code for Email delivery.
     */
    public OtpInfo generateOtp(String targetEmail, String targetPhone) {
        return generateOtp(targetEmail, targetPhone, "EMAIL");
    }

    /**
     * Generates a secure 6-digit OTP code and triggers email delivery.
     */
    public OtpInfo generateOtp(String targetEmail, String targetPhone, String channel) {
        int number = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        String code = String.format("%06d", number);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpInfo otpInfo = new OtpInfo(code, expiresAt, targetEmail, targetPhone, "EMAIL");

        // Send real email to recipient Gmail - propagates error if failed
        if (targetEmail != null && !targetEmail.trim().isEmpty()) {
            if (emailService != null) {
                emailService.sendOtpEmail(targetEmail, code);
            }
        }

        logger.info("=================================================================");
        logger.info("[OTP SERVICE] Gửi mã OTP 6 chữ số tới Gmail:");
        logger.info("  -> Đích đến:  [{}]", targetEmail);
        logger.info("  -> MÃ OTP:    [{}] (Hiệu lực 5 phút, đến {})", code, expiresAt);
        logger.info("=================================================================");

        return otpInfo;
    }

    public boolean validateOtp(OtpInfo otpInfo, String inputCode) {
        if (otpInfo == null || inputCode == null || inputCode.trim().isEmpty()) {
            return false;
        }
        if (otpInfo.isExpired()) {
            return false;
        }
        return otpInfo.getCode().equals(inputCode.trim());
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.substring(0, 2) + "***" + name.charAt(name.length() - 1) + "@" + domain;
    }

    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }
}
