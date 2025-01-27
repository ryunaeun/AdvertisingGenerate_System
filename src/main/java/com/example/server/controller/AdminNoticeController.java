package com.example.server.controller;


import com.example.server.dto.NoticeDto;
import com.example.server.model.Board;
import com.example.server.model.Notice;
import com.example.server.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.server.repository.NoticeRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notice")
public class AdminNoticeController {
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;


    @Operation(
            summary = "공지사항 작성(관리자 로그인 후 이용가능)",
            description = "관리자가 공지사항 작성(title과 content만 남기고 작성)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항을 작성했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @PostMapping("/write")
    public ResponseEntity<?> createNotice(@RequestBody NoticeDto.NoticePost request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 사용자 이메일 설정 => Notice의 email 필드에 할당
            String useremail = authentication.getName();
            Notice notice = request.toEntity(useremail);

            // Notice 생성
            Notice createdNotice = noticeService.createNotice(notice);
            return ResponseEntity.ok(createdNotice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 생성 중 문제가 발생했습니다.");
        }
    }


    @Operation(
            summary = "공지사항 조회(관리자 로그인 후 이용가능)",
            description = "관리자가 공지사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> NoticeList(@ParameterObject Pageable pageable, @RequestParam(defaultValue = "false") boolean isDescending) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            // 데이터베이스에서 모든 Notice 조회
            Page<Notice> notices = noticeService.getNotices(pageable,isDescending);
            List<Notice> reorderedNotices = notices.stream()
                    .map(notice -> {
                        notice.setNoticeOrder(notices.getContent().indexOf(notice) + 1);
                        return notice;
                    })
                    .toList();
            return ResponseEntity.ok(reorderedNotices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 조회 중 문제가 발생했습니다.");
        }
    }

    @Operation(
            summary = "특정 공지사항 조회(관리자 로그인 후 이용가능)",
            description = "관리자가 특정 공지사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @GetMapping("/{order}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> NoticeList(@PathVariable("order") int noticeOrder) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            Optional<Notice> noticeOptional = noticeRepository.findAllByOrderByNoticeOrder()
                    .stream()
                    .filter(notice -> notice.getNoticeOrder() == noticeOrder)
                    .findFirst();
            if (noticeOptional.isEmpty()) {
                return ResponseEntity.status(404).body("제공된 noticeOrder에 해당하는 게시글을 찾을 수 없습니다.");
            }
            Notice notice = noticeOptional.get();

            // 해당 공지사항에 대한 답변 목록 조회
            return ResponseEntity.ok(Map.of(
                    "notice", notice
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 및 답변 조회 중 문제가 발생했습니다.");
        }
    }

    @Operation(
            summary = "공지사항 삭제(관리자 로그인 후 이용가능)",
            description = "관리자가 공지사항 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항이 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @DeleteMapping("/{order}")
    public ResponseEntity<String> deleteNotice(@PathVariable("order") int noticeOrder) {
        Optional<Notice> noticeOptional = noticeRepository.findAllByOrderByNoticeOrder()
                .stream()
                .filter(notice -> notice.getNoticeOrder() == noticeOrder)
                .findFirst();
        if (noticeOptional.isEmpty()) {
            return ResponseEntity.status(404).body("제공된 noticeOrder에 해당하는 게시글을 찾을 수 없습니다.");
        }
        Notice notice = noticeOptional.get();
        noticeService.deleteNoticeByNoticeOrder(notice.getNoticeOrder());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

//    /**
//     * 전체 게시글 noticeOrder 재정렬
//     */
//    @PutMapping("/reorder")
//    public ResponseEntity<String> reorderNoticeOrders() {
//        noticeService.reorderNoticeOrders();
//        return ResponseEntity.ok("게시판 번호가 재정렬되었습니다.");
//    }

    @Operation(
            summary = "공지사항 수정(관리자 로그인 후 이용가능)",
            description = "관리자가 공지사항 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항이 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    @PostMapping("/{order}/update")
    @Transactional
    public ResponseEntity<?> updateNotice(@PathVariable("order") int noticeOrder, @RequestBody NoticeDto.NoticePost request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            Optional<Notice> noticeOpt = noticeRepository.findAllByOrderByNoticeOrder()
                    .stream()
                    .filter(notice -> notice.getNoticeOrder() == noticeOrder)
                    .findFirst();
            if (noticeOpt.isPresent()) {
                Notice notice = noticeOpt.get();

                // 필요한 필드만 업데이트
                if (request.getTitle() != null) {
                    notice.setTitle(request.getTitle().toString());
                }

                if (request.getContent() != null) {
                    notice.setContent(request.getContent().toString());
                }

                notice.setCreatedAt(LocalDateTime.now());
                // 데이터 저장
                noticeRepository.save(notice);

                return ResponseEntity.ok(Map.of(
                        "content", request.getContent() != null ? "내용 업데이트" : "내용 없음",
                        "title", request.getTitle() != null? "제목 업데이트" : "제목 없음"
                ));
            } else {
                return ResponseEntity.status(404).body("공지사항을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 업데이트 중 문제가 발생했습니다.");
        }
    }
}
