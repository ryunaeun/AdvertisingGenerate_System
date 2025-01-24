package com.example.server.controller;

import com.example.server.dto.ReplyDto;
import com.example.server.model.Inquiry;
import com.example.server.model.Reply;
import com.example.server.repository.InquiryRepository;
import com.example.server.repository.NoticeRepository;
import com.example.server.repository.ReplyRepository;
import com.example.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/users/inquiry") // URL 계층을 분리
@RequiredArgsConstructor
@Validated

public class AdminInquiryController {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final ReplyRepository replyRepository;
    //
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> myInquiryList() {
        try {
            // 데이터베이스에서 모든 User 조회
            List<Inquiry> inquiries = inquiryRepository.findAll();
            return ResponseEntity.ok(inquiries); // 조회 결과 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 조회 중 문제가 발생했습니다.");
        }
    }
    // 관리자가 user 문의사항 지우기
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteInquiry(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("id"); // 요청에서 ID 추출
            if (userId == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            // 사용자 확인 후 삭제
            if (inquiryRepository.existsById(userId)) {
                inquiryRepository.deleteById(userId);
                return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 삭제 중 문제가 발생했습니다.");
        }
    }

    // 관리자가 user 문의사항 수정
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> updateInquiry(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("id").toString()); // 요청에서 ID 추출
            if (userId == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            Optional<Inquiry> inquiryOpt = inquiryRepository.findById(userId);
            if (inquiryOpt.isPresent()) {
                Inquiry inquiry = inquiryOpt.get();

                // 필요한 필드만 업데이트
                if (request.containsKey("title")) {
                    inquiry.setTitle(request.get("title").toString());
                }

                if (request.containsKey("content")) {
                    inquiry.setContent(request.get("content").toString());
                }

                // 데이터 저장
                inquiryRepository.save(inquiry);

                return ResponseEntity.ok(Map.of(
                        "content", request.containsKey("content") ? "내용 업데이트" : "내용 없음",
                        "title", request.containsKey("title") ? "제목 업데이트" : "제목 없음"
                ));
            } else {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 정보 업데이트 중 문제가 발생했습니다.");
        }
    }
    //특정 user 조회
    @GetMapping("/specific")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> specificInquiryList(@RequestBody Map<String, String> request) {
        try {
            String userId = String.valueOf(request.get("userId")); // 요청에서 ID 추출
            System.out.println(userId);
            if (userId == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }
            var inquiryList = inquiryRepository.findByUserId(userId); // 사용자의 게시글 조회
            return ResponseEntity.ok(inquiryList); // 게시글 목록 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 조회 중 문제가 발생했습니다.");
        }
    }
    @PostMapping("/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> replyInquiry(@RequestBody @Validated ReplyDto.ReplyPost replyPost) {
        try {
            // 요청에서 사용자 ID와 문의사항 ID 추출
            String userId = replyPost.getUserId();
            Long inquiryId = replyPost.getInquiryId();

            // 사용자 ID와 문의사항 ID 유효성 검사
            if (userId == null || inquiryId == null) {
                return ResponseEntity.badRequest().body("userId와 inquiryId가 필요합니다.");
            }

            // 1. inquiryId로 Inquiry 조회
            Optional<Inquiry> inquiryOptional = inquiryRepository.findById(inquiryId);
            if (inquiryOptional.isEmpty()) {
                return ResponseEntity.status(404).body("해당 inquiryId에 대한 문의사항을 찾을 수 없습니다.");
            }

            Inquiry inquiry = inquiryOptional.get();

            // 2. userId와 inquiryId 조건으로 Reply 엔티티 생성
            Reply reply = new Reply();
            reply.setUserId(userId); // 답변 작성자 ID 설정
            reply.setInquiryId(inquiryId); // 관련 문의사항 ID 설정
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
