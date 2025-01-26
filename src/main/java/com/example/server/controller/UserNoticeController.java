package com.example.server.controller;


import com.example.server.model.Notice;
import com.example.server.repository.NoticeRepository;
import com.example.server.service.NoticeService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/notice")
public class UserNoticeController {
    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;

    @GetMapping("")
    public ResponseEntity<?> NoticeList(Pageable pageable, @RequestParam(defaultValue = "false") boolean isDescending) {
        try {
            Page<Notice> notices = noticeService.getNotices(pageable, isDescending);
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 조회 중 문제가 발생했습니다.");
        }
    }

    @GetMapping("/{order}")
    public ResponseEntity<?> NoticeList(@PathVariable("order") int noticeOrder) {
        try {
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
            return ResponseEntity.status(500).body("공지사항 조회 중 문제가 발생했습니다.");
        }
    }
}
