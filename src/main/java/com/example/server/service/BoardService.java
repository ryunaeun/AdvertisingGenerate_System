package com.example.server.service;
import com.example.server.model.Board;
import com.example.server.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    /**
     * 게시글 생성
     * 새로운 게시글에는 연속된 boardOrder를 자동으로 할당
     */
    @Transactional
    public Board createBoard(Board board) {
        // 가장 최신 boardOrder 찾기
        List<Board> boards = boardRepository.findAllByOrderByBoardOrderAsc();
        int latestOrder = boards.isEmpty() ? 0 : boards.get(boards.size() - 1).getBoardOrder();

        // 새로운 게시글의 boardOrder 설정
        board.setBoardOrder(latestOrder + 1);

        // 객체 필드 값 디버깅
        System.out.println("Board 객체 상태: " + board);

        // 저장
        return boardRepository.save(board);
    }

    /**
     * ID (Primary Key)로 게시글 조회
     */
    public Board getBoardById(Long BoardId) {
        return boardRepository.findById(BoardId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
    }

    /**
     * 특정 boardOrder로 게시글 조회
     */
    public Board getBoardByBoardOrder(int boardOrder) {
        return boardRepository.findAllByOrderByBoardOrderAsc()
                .stream()
                .filter(board -> board.getBoardOrder() == boardOrder)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
    }

    /**
     * 게시글 삭제 및 boardOrder 재정렬
     */
    @Transactional
    public void deleteBoardByBoardOrder(int boardOrder) {
        // 게시글 삭제
        boardRepository.deleteByBoardOrder(boardOrder);

        // 삭제 이후, boardOrder 재정렬
        reorderBoardOrders();
    }

    /**
     * 전체 게시판의 boardOrder 재정렬
     */
    @Transactional
    public void reorderBoardOrders() {
        List<Board> boards = boardRepository.findAllByOrderByBoardOrderAsc();

        // boardOrder를 1부터 다시 재정렬
        for (int i = 0; i < boards.size(); i++) {
            boards.get(i).setBoardOrder(i + 1);
        }

        // 변경된 데이터 저장
        boardRepository.saveAll(boards);
    }
    public Page<Board> getUserBoards(String email, Pageable pageable, boolean isDescending) {
        Sort sort = isDescending ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return boardRepository.findByEmail(email,pageable);
    }
    /**
     * 게시글 페이지네이션 조회
     */
    public Page<Board> getBoards(Pageable pageable, boolean isDescending) {
        Sort sort = isDescending ? Sort.by("boardOrder").descending() : Sort.by("boardOrder").ascending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return boardRepository.findAll(sortedPageable);
    }
}
