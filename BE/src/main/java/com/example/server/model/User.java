package com.example.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String username; // 사용자 이름

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String billing = "FREE"; // 기본 요금제는 무료(FREE)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 회원가입 시간

    @Column(nullable = false)
    private LocalDateTime billingDate = LocalDateTime.now(); // 기본값으로 가입일과 동일

    @Column(nullable = false)
    private String role = "USER"; // 기본 역할은 일반 사용자(USER)

    @Column(nullable = false, unique = true)
    private String secretKey;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = true)
    private String companyName;

    @Column(nullable = true, unique = true)
    private String businessNumber;

    @Column(nullable = true)
    private String businessFilePath;

    //추가 부분//
    @Column(nullable = true)
    private String verificationCode; // 이메일 인증 코드

    @Column(nullable = false)
    private boolean isVerified = false; // 이메일 인증 여부 (기본값: 미인증)

    @Column(nullable = true)
    private LocalDateTime verificationCodeIssuedAt; // 인증 코드 발행 시간

    // secretKey 생성 메서드
    @PrePersist
    public void generateSecretKey() {
        this.secretKey = java.util.UUID.randomUUID().toString();
    }
}

