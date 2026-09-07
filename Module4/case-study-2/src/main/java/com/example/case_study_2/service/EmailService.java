package com.example.case_study_2.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * Sends a real HTML OTP email to the recipient via Gmail SMTP.
     * Throws an exception if sending fails so the controller can inform the user immediately.
     *
     * @param toEmail recipient email address
     * @param otpCode 6-digit OTP code
     */
    public void sendOtpEmail(String toEmail, String otpCode) {
        if (toEmail == null || toEmail.trim().isEmpty() || otpCode == null) {
            throw new IllegalArgumentException("Địa chỉ email nhận mã không hợp lệ.");
        }

        if (mailSender == null || fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new IllegalStateException("Hệ thống chưa được cấu hình tài khoản Gmail gửi thư (spring.mail.username và spring.mail.password trong application.properties).");
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            try {
                helper.setFrom(fromEmail, "Phòng Khám Đa Khoa MedCare");
            } catch (UnsupportedEncodingException e) {
                helper.setFrom(fromEmail);
            }

            helper.setTo(toEmail);
            helper.setSubject("[MedCare] Mã xác thực OTP đăng ký tài khoản: " + otpCode);

            String htmlBody = buildOtpHtmlTemplate(otpCode);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            logger.info("[EMAIL SERVICE] ĐÃ GỬI EMAIL THẬT THÀNH CÔNG TỚI: {}", toEmail);
        } catch (org.springframework.mail.MailAuthenticationException e) {
            logger.error("[EMAIL SERVICE] Lỗi xác thực Gmail SMTP với tài khoản '{}': {}", fromEmail, e.getMessage());
            throw new IllegalStateException("Không thể gửi email: Google từ chối mật khẩu của tài khoản '" + fromEmail + "'. "
                    + "Google bắt buộc phải dùng 'Mật khẩu ứng dụng (App Password)' 16 chữ cái (tạo tại https://myaccount.google.com/apppasswords) thay vì mật khẩu Gmail thông thường.");
        } catch (org.springframework.mail.MailSendException e) {
            logger.error("[EMAIL SERVICE] Lỗi gửi thư tới {}: {}", toEmail, e.getMessage());
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("535") || msg.contains("5.7.8") || msg.contains("Authentication") || msg.contains("Username and Password not accepted")) {
                throw new IllegalStateException("Không thể gửi email: Google từ chối mật khẩu của tài khoản '" + fromEmail + "'. "
                        + "Vui lòng tạo 'Mật khẩu ứng dụng (App Password)' 16 chữ cái tại https://myaccount.google.com/apppasswords và điền vào spring.mail.password.");
            }
            if (msg.contains("550") || msg.contains("5.1.1") || msg.contains("User unknown") || msg.contains("Invalid Addresses") || msg.contains("recipient")) {
                throw new IllegalArgumentException("Địa chỉ Gmail '" + toEmail + "' không tồn tại hoặc không thể nhận thư. Vui lòng kiểm tra lại chính xác địa chỉ Gmail của bạn.");
            }
            throw new IllegalStateException("Không thể gửi mã OTP tới " + toEmail + ": " + e.getMessage());
        } catch (Exception e) {
            logger.error("[EMAIL SERVICE] Lỗi khi gửi email: {}", e.getMessage(), e);
            throw new IllegalStateException("Không thể gửi email xác thực tới " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildOtpHtmlTemplate(String otpCode) {
        return "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
                + "<body style='margin:0; padding:0; background-color:#f4f6f9; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>"
                + "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='background-color:#f4f6f9; padding: 30px 10px;'>"
                + "  <tr>"
                + "    <td align='center'>"
                + "      <table width='100%' max-width='580' border='0' cellspacing='0' cellpadding='0' style='max-width:580px; background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 15px rgba(0,0,0,0.06); border:1px solid #e2e8f0;'>"
                + "        <tr>"
                + "          <td style='background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%); padding: 25px 30px; text-align: center; color: #ffffff;'>"
                + "            <h1 style='margin:0; font-size: 22px; font-weight: 700; letter-spacing: 0.5px;'>HỆ THỐNG Y TẾ MEDCARE</h1>"
                + "            <p style='margin: 6px 0 0; font-size: 13px; opacity: 0.9;'>Xác thực đăng ký tài khoản bệnh nhân</p>"
                + "          </td>"
                + "        </tr>"
                + "        <tr>"
                + "          <td style='padding: 35px 30px; color: #334155; font-size: 15px; line-height: 1.6;'>"
                + "            <p style='margin-top:0;'>Xin chào quý khách,</p>"
                + "            <p>Quý khách vừa yêu cầu tạo tài khoản tại <strong>Hệ thống Đặt lịch Khám MedCare</strong>. Dưới đây là mã xác thực OTP dùng để kích hoạt tài khoản của quý khách:</p>"
                + "            <div style='background: #f0fdfa; border: 2px dashed #0d9488; border-radius: 8px; text-align: center; padding: 20px; margin: 25px 0;'>"
                + "              <div style='font-size: 13px; color: #0f766e; text-transform: uppercase; font-weight: 600; letter-spacing: 1px;'>Mã xác thực OTP (6 chữ số)</div>"
                + "              <div style='font-size: 36px; font-weight: 800; color: #0f766e; letter-spacing: 8px; margin: 8px 0; font-family: monospace;'>" + otpCode + "</div>"
                + "              <div style='font-size: 12px; color: #64748b;'>Mã có hiệu lực trong vòng <strong>5 phút</strong>.</div>"
                + "            </div>"
                + "            <p style='font-size: 13.5px; color: #64748b; background: #fffbeb; border-left: 4px solid #f59e0b; padding: 10px 14px; border-radius: 4px; margin: 20px 0;'>"
                + "              <strong>Lưu ý an toàn:</strong> Không chia sẻ mã này với bất kỳ ai để đảm bảo an toàn thông tin sức khỏe cá nhân của quý khách."
                + "            </p>"
                + "            <p style='margin-bottom: 0;'>Trân trọng,<br><strong>Đội ngũ hỗ trợ Hệ thống Y tế MedCare</strong></p>"
                + "          </td>"
                + "        </tr>"
                + "        <tr>"
                + "          <td style='background: #f8fafc; padding: 18px 30px; text-align: center; color: #94a3b8; font-size: 12px; border-top: 1px solid #f1f5f9;'>"
                + "            Đây là email tự động từ hệ thống. Quý khách vui lòng không trả lời thư này."
                + "          </td>"
                + "        </tr>"
                + "      </table>"
                + "    </td>"
                + "  </tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}
