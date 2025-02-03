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
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId; // PK (기본 키)

    @Column(nullable = false)
    private String email; // 작성자 이름

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일시

    @Column(nullable = false)
    private int boardOrder; // 게시판 번호 (int)

    @Column(nullable = false)
    private boolean breply = false;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 순서 조정 메서드는 서비스 계층에서 구현
}