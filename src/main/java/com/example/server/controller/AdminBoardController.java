package com.example.server.controller;

import com.example.server.dto.ReplyDto;
import com.example.server.dto.UserDto;
import com.example.server.model.Board;
import com.example.server.model.Notice;
import com.example.server.model.Reply;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.NoticeRepository;
import com.example.server.repository.ReplyRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.BoardService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/admin/users/board") // URL 계층을 분리
@RequiredArgsConstructor
@Validated

public class
AdminBoardController {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final BoardService boardService;
    //

    @Operation(
            summary = "모든 문의사항 조회(관리자 로그인 후 이용가능)",
            description = "모든 문의사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 문의사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> myBoardList(Pageable pageable, @RequestParam(defaultValue = "false") boolean isDescending) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            Page<Board> boards = boardService.getBoards(pageable,isDescending);
            return ResponseEntity.ok(boards); // 조회 결과 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("문의사항 조회 중 문제가 발생했습니다.");
        }
    }

    @Operation(
            summary = "문의사항 삭제(관리자 로그인 후 이용가능)",
            description = "문의사항 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항을 삭제했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    // 관리자가 email 문의사항 지우기
    @DeleteMapping("/{order}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> deleteBoard(@PathVariable("order") int boardOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        Optional<Board> boardOptional = boardRepository.findByBoardOrder(boardOrder);
        Board board = boardOptional.get();
        boardService.deleteBoardByBoardOrder(board.getBoardOrder());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @Operation(
            summary = "특정 유저 문의사항 조회(관리자 로그인 후 이용가능)",
            description = "유저의 문의사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

// 특정 email로 게시글 조회
    @GetMapping("/specific")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> specificBoardList(@RequestBody UserDto.EmailRequest request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 요청에서 email 추출
            String email = request.getEmail();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("사용자 Email이 필요합니다.");
            }

            // email을 기준으로 게시글 목록 조회
            List<Board> boardList = boardRepository.findByEmail(email);
            return ResponseEntity.ok(boardList); // 게시글 목록 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 조회 중 문제가 발생했습니다.");
        }
    }


    @Operation(
            summary = "문의사항 답변(관리자 로그인 후 이용가능)",
            description = "문의사항 답변"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항을 답변했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })


    @PostMapping("/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> replyBoard(@RequestBody @Validated ReplyDto.ReplyPost replyPost) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            // 요청에서 사용자 Email과 문의사항 ID 추출
            String email = replyPost.getEmail();
            Long boardId = replyPost.getBoardId();

            // 사용자 Email과 문의사항 ID 유효성 검사
            if (email == null || boardId == null) {
                return ResponseEntity.badRequest().body("email와 boardId가 필요합니다.");
            }
            // 1. boardId로 Board 조회
            List<Board> boardOptionalUser = boardRepository.findByEmail(email);
            if (boardOptionalUser.isEmpty()) {
                return ResponseEntity.status(404).body("해당 Email에 대한 문의사항을 찾을 수 없습니다.");
            }
            // 1. boardId로 Board 조회
            Optional<Board> boardOptional = boardRepository.findByBoardId(boardId);
            if (boardOptional.isEmpty()) {
                return ResponseEntity.status(404).body("해당 boardId에 대한 문의사항을 찾을 수 없습니다.");
            }

            Board board = boardOptional.get();

            // 2. email과 boardId 조건으로 Reply 엔티티 생성
            Reply reply = new Reply();
            reply.setEmail(email); // 답변 작성자 Email 설정
            reply.setBoardId(boardId); // 관련 문의사항 ID 설정
            reply.setTitle(replyPost.getTitle()); // 제목 설정
            reply.setContent(replyPost.getContent()); // 내용 설정

            // ReplyRepository를 통해 데이터베이스에 저장
            replyRepository.save(reply);

            // 성공 응답 반환
            return ResponseEntity.ok(Map.of(
                    "message", "답변이 성공적으로 저장되었습니다.",
                    "replyId", reply.getReplyId(),
                    "createdAt", reply.getCreatedAt()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("답변 작성 중 문제가 발생했습니다.");
        }
    }
}
