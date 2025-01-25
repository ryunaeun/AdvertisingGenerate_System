package com.example.server.repository;

import com.example.server.model.Notice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 사용자 ID를 기준으로 게시글 검색
    List<Notice> findByUserId(String userId);
    Optional<Notice> findAllByOrderByBoardOrder();
}