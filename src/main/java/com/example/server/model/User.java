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

    @Column(nullable = true)
    private LocalDateTime nowAt; //최근 접속 시간,로그인시 변경됨

    @Column(nullable = false)
    private String role = "USER"; // 기본 역할은 일반 사용자(USER)

    @Column(nullable = false, unique = true)
    private String secretKey;

    @Column(nullable = true)
    private String refreshToken;

    @Column(nullable = true)
    private String companyName;

    @Column(nullable = true)
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

    // 엔티티가 처음 저장될 때 기본값 설정
    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.billingDate = createdAt; // billingDate는 기본적으로 createdAt과 동일
        this.secretKey = java.util.UUID.randomUUID().toString();
    }

    // 엔티티가 업데이트 될 때 호출 (billing이 변경되는 경우 처리)
    @PreUpdate
    public void onPreUpdate() {
        if ("CREDIT".equals(this.billing)) {
            this.billingDate = LocalDateTime.now(); // billing이 CREDIT으로 변경되는 시간을 기록
        }
    }

}

