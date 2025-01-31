package com.example.server.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    private static final String FROM_EMAIL = "wjdtjr9805@naver.com"; // 네이버 SMTP 이메일 계정
    private static final String FROM_NAME = "ADVI"; // 발신자 이름

    // 인증 이메일 전송 (HTML 템플릿 적용)
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // ✅ 발신자 이름과 이메일 설정
            helper.setFrom(FROM_NAME + " <" + FROM_EMAIL + ">");
            helper.setTo(toEmail);
            helper.setSubject("🎉 ADVI 회원가입 인증 코드 안내");

            // ✅ HTML 이메일 본문 생성
            String emailContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<h2 style='color: #2d89ef;'>ADVI 회원가입 인증</h2>"
                    + "<p>안녕하세요, ADVI에 가입해 주셔서 감사합니다!</p>"
                    + "<p>아래의 인증 코드를 입력하여 회원가입을 완료해주세요:</p>"
                    + "<div style='font-size: 24px; font-weight: bold; color: #2d89ef; border: 1px dashed #2d89ef; padding: 10px; display: inline-block;'>"
                    + code + "</div>"
                    + "<p>※ 본 인증 코드는 5분 동안 유효합니다.</p>"
                    + "<p style='margin-top: 20px;'>감사합니다.<br><strong>ADVI 팀</strong></p>"
                    + "<hr style='margin-top: 20px;'/>"
                    + "<p style='font-size: 12px; color: gray;'>이메일이 잘못 도착했다면 무시하셔도 됩니다.</p>"
                    + "</div>";

            helper.setText(emailContent, true); // ✅ HTML 형식으로 설정

            javaMailSender.send(message);
            logger.info("이메일 전송 성공: {}", toEmail);
        } catch (Exception e) {
            logger.error("이메일 전송 실패: {}", toEmail, e);
        }
    }
}