package com.example.server.controller;
import com.example.server.model.Board;
import com.example.server.model.Notice;
import com.example.server.model.Reply;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.ReplyRepository;
import com.example.server.service.BoardService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/myboard")
public class UserBoardController {

    private final BoardService boardService;
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    /**
     * 게시글 생성
     */
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody Board board) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 사용자 이메일 설정 => Board의 userId 필드에 할당
            String userEmail = authentication.getName();
            board.setUserId(userEmail);

            // Board 생성
            Board createdBoard = boardService.createBoard(board);
            return ResponseEntity.ok(createdBoard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 생성 중 문제가 발생했습니다.");
        }
    }

    /**
     * 게시글 페이지 단위 조회
     */
    @GetMapping("")
    public ResponseEntity<?> BoardList(Pageable pageable, @RequestParam(defaultValue = "false") boolean isDescending) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 사용자 이메일(userId) 가져오기
            String userEmail = authentication.getName();

            // 사용자별 게시판 데이터 조회
            Page<Board> userBoards = boardService.getUserBoards(userEmail, pageable, isDescending);

            // `boardOrder`를 동적으로 1부터 재정렬
            List<Board> reorderedBoards = userBoards.stream()
                    .map(board -> {
                        board.setBoardOrder(userBoards.getContent().indexOf(board) + 1);
                        return board;
                    })
                    .toList();

            // 페이지 형태로 다시 감싸서 반환
            return ResponseEntity.ok(reorderedBoards);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시판 조회 중 문제가 발생했습니다.");
        }
    }

    @GetMapping("/{order}")
    public ResponseEntity<?> specificBoardAndReplies(@PathVariable("order") int boardOrder) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();

            // 사용자와 연관된 게시판 목록 가져오기 (정렬 포함)
            List<Board> userBoards = boardRepository.findByUserId(userEmail)
                    .stream()
                    .sorted((b1, b2) -> Integer.compare(b1.getBoardOrder(), b2.getBoardOrder()))
                    .toList();

            // 요청된 동적 boardOrder에 해당하는 실제 DB의 boardOrder 확인
            if (boardOrder < 1 || boardOrder > userBoards.size()) {
                return ResponseEntity.status(404).body("제공된 boardOrder에 해당하는 게시글을 찾을 수 없습니다.");
            }
            Board board = userBoards.get(boardOrder - 1); // 인덱스는 0부터 시작하므로 -1 처리

            // 해당 게시글에 대한 답변 목록 조회
            Optional<Reply> replies = replyRepository.findByBoardId(board.getBoardId());

            // 동적 boardOrder와 함께 Board와 Replies 반환
            board.setBoardOrder(boardOrder); // 사용자에게 표시할 동적 boardOrder 설정
            return ResponseEntity.ok(Map.of(
                    "board", board,
                    "replies", replies
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 및 답변 조회 중 문제가 발생했습니다.");
        }
    }
    /**
     * 특정 게시글 삭제
     */
    @DeleteMapping("/{order}")
    public ResponseEntity<String> deleteBoard(@PathVariable("order") int boardOrder) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();

            // 사용자와 연관된 게시판 목록 가져오기 (정렬 포함)
            List<Board> userBoards = boardRepository.findByUserId(userEmail)
                    .stream()
                    .sorted((b1, b2) -> Integer.compare(b1.getBoardOrder(), b2.getBoardOrder()))
                    .toList();

            // 요청된 동적 boardOrder를 실제 DB의 boardOrder로 매핑
            if (boardOrder < 1 || boardOrder > userBoards.size()) {
                return ResponseEntity.status(404).body("제공된 boardOrder에 해당하는 게시글을 찾을 수 없습니다.");
            }

            Board boardToDelete = userBoards.get(boardOrder - 1); // 인덱스는 0부터 시작하므로 -1 처리

            // 삭제 수행
            boardService.deleteBoardByBoardOrder(boardToDelete.getBoardOrder());
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 삭제 중 문제가 발생했습니다.");
        }
    }

    /**
     * 전체 게시글 boardOrder 재정렬
     */
    @PutMapping("/reorder")
    public ResponseEntity<String> reorderBoardOrders() {
        boardService.reorderBoardOrders();
        return ResponseEntity.ok("게시판 번호가 재정렬되었습니다.");
    }

    @PostMapping("/{order}/update")
    @Transactional
    public ResponseEntity<?> updateNotice(@PathVariable("order") int boardOrder, @RequestBody Map<String, Object> request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();

            // 사용자와 연관된 게시판 목록 가져오기 (정렬 포함)
            List<Board> userBoards = boardRepository.findByUserId(userEmail)
                    .stream()
                    .sorted((b1, b2) -> Integer.compare(b1.getBoardOrder(), b2.getBoardOrder()))
                    .toList();

            // 요청된 동적 boardOrder를 실제 DB의 boardOrder로 매핑
            if (boardOrder < 1 || boardOrder > userBoards.size()) {
                return ResponseEntity.status(404).body("제공된 boardOrder에 해당하는 공지사항을 찾을 수 없습니다.");
            }

            Board boardToUpdate = userBoards.get(boardOrder - 1); // 인덱스는 0부터 시작하므로 -1 처리

            // 필요한 필드만 업데이트
            if (request.containsKey("title")) {
                boardToUpdate.setTitle(request.get("title").toString());
            }

            if (request.containsKey("content")) {
                boardToUpdate.setContent(request.get("content").toString());
            }

            // 데이터 저장
            boardRepository.save(boardToUpdate);

            return ResponseEntity.ok(Map.of(
                    "content", request.containsKey("content") ? "내용 업데이트" : "내용 없음",
                    "title", request.containsKey("title") ? "제목 업데이트" : "제목 없음"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 업데이트 중 문제가 발생했습니다.");
        }
    }
}