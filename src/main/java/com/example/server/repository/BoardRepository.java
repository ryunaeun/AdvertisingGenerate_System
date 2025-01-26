package com.example.server.repository;

import com.example.server.model.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 사용자 ID를 기준으로 게시글 검색
    List<Board> findByUserId(String userId);
    Optional<Board> findByBoardId(Long boardId);
    Optional<Board> findByBoardOrder(int boardOrder);
    List<Board> findAllByOrderByBoardOrder();
    Boolean existsByBoardOrder(int boardOrder);
    Integer deleteByBoardOrder(int boardOrder);
    // boardOrder 기준으로 페이지 단위 조회
    Page<Board> findAllByOrderByBoardOrderAsc(Pageable pageable);
    // 게시판 기록 전체 조회 (전체 재정렬용)
    List<Board> findAllByOrderByBoardOrderAsc();
}