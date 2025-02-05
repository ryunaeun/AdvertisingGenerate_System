package com.example.server.controller;


import com.example.server.model.Notice;
import com.example.server.repository.NoticeRepository;
import com.example.server.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Operation(
            summary = "공지사항 조회(로그인 없이 이용가능)",
            description = "공지사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @GetMapping("")
    public ResponseEntity<?> NoticeList(@ParameterObject Pageable pageable, @RequestParam(defaultValue = "false") boolean isDescending) {
        try {
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
            summary = "특정 공지사항 조회(로그인 없이 이용가능)",
            description = "특정 공지사항 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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
