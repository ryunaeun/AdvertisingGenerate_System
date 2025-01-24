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
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId; // PK (기본 키)

    @Column(nullable = false)
    private String userId; // 답변해주는 회원 ID

    @Column(nullable = false)
    private Long inquiryId;

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일시

    @Column(nullable = false)
    private int boardOrder; // 게시판 번호 (int)

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    // 순서 조정 메서드는 서비스 계층에서 구현
}