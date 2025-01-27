package com.example.server.repository;


import com.example.server.model.Notice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 사용자 ID를 기준으로 게시글 검색
    List<Notice> findByEmail(String email);
    Optional<Notice> findByNoticeId(Long noticeId);
    Optional<Notice> findByNoticeOrder(int noticeOrder);
    List<Notice> findAllByOrderByNoticeOrder();
    Boolean existsByEmail(String email);
    Integer deleteByNoticeOrder(int noticeOrder);
    // boardOrder 기준으로 페이지 단위 조회
    Page<Notice> findAllByOrderByNoticeOrderAsc(Pageable pageable);
    // 게시판 기록 전체 조회 (전체 재정렬용)
    List<Notice> findAllByOrderByNoticeOrderAsc();
    Page<Notice> findAll(Pageable pageable); // JpaRepository 기본 제공
}