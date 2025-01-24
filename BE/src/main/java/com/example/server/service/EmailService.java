package com.example.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    private static final String FROM_EMAIL = "wjdtjr9805@naver.com"; // 발신자 이메일

    // 인증 이메일 전송
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail); // 수신자 (회원가입한 사용자의 이메일)
            message.setFrom(FROM_EMAIL); // 반드시 SMTP 발신자 이메일 (네이버 계정)
            message.setSubject("회원가입 인증 코드");
            message.setText("인증 코드는 다음과 같습니다: " + code + "\n이 코드를 인증 화면에 입력하세요.");
            javaMailSender.send(message);
            logger.info("이메일 전송 성공: {}", toEmail);
        } catch (Exception e) {
            logger.error("이메일 전송 실패: {}", toEmail, e);
        }
    }
}
