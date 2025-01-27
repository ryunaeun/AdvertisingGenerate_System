package com.example.server.repository;

import com.example.server.model.Reply;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 사용자 ID를 기준으로 게시글 검색
    Optional<Reply> findByEmail(String email);
    Optional<Reply> findByBoardId(Long boardId);
    Boolean existsByBoardId(Long boardId);
}