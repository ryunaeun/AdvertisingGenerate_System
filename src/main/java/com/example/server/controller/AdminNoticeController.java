package com.example.server.controller;


import com.example.server.model.Board;
import com.example.server.model.Notice;
import com.example.server.service.NoticeService;
import jakarta.transaction.Transactional;
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

    @PostMapping("/write")
    public ResponseEntity<?> createNotice(@RequestBody Notice notice) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 사용자 이메일 설정 => Notice의 userId 필드에 할당
            String userEmail = authentication.getName();
            notice.setUserId(userEmail);

            // Notice 생성
            Notice createdNotice = noticeService.createNotice(notice);
            return ResponseEntity.ok(createdNotice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 생성 중 문제가 발생했습니다.");
        }
    }


    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> NoticeList(Pageable pageable,@RequestParam(defaultValue = "false") boolean isDescending) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            // 데이터베이스에서 모든 Notice 조회
            Page<Notice> notices = noticeService.getNotices(pageable,isDescending);
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 조회 중 문제가 발생했습니다.");
        }
    }

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

    /**
     * 전체 게시글 noticeOrder 재정렬
     */
    @PutMapping("/reorder")
    public ResponseEntity<String> reorderNoticeOrders() {
        noticeService.reorderNoticeOrders();
        return ResponseEntity.ok("게시판 번호가 재정렬되었습니다.");
    }

    @PostMapping("/{order}/update")
    @Transactional
    public ResponseEntity<?> updateNotice(@PathVariable("order") int noticeOrder, @RequestBody Map<String, Object> request) {
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
                if (request.containsKey("title")) {
                    notice.setTitle(request.get("title").toString());
                }

                if (request.containsKey("content")) {
                    notice.setContent(request.get("content").toString());
                }

                // 데이터 저장
                noticeRepository.save(notice);

                return ResponseEntity.ok(Map.of(
                        "content", request.containsKey("content") ? "내용 업데이트" : "내용 없음",
                        "title", request.containsKey("title") ? "제목 업데이트" : "제목 없음"
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
