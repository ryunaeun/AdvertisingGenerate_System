package com.example.server.repository;

import com.example.server.model.Inquiry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 사용자 ID를 기준으로 게시글 검색
    List<Inquiry> findByUserId(String userId);
    Optional<Inquiry> findByInquiryId(Long inquiryId);
    List<Inquiry> findAllByOrderByBoardOrder();
    Boolean existsByUserId(String userId);
    Integer deleteByUserId(String userId);
}