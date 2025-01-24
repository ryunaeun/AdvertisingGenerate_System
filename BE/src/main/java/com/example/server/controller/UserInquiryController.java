package com.example.server.controller;

import com.example.server.dto.InquiryDto;
import com.example.server.model.Inquiry;
import com.example.server.model.Notice;
import com.example.server.model.Reply;
import com.example.server.repository.InquiryRepository;
import com.example.server.repository.NoticeRepository;
import com.example.server.repository.ReplyRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // URL 계층을 분리
@RequiredArgsConstructor
@Validated
public class UserInquiryController {

    private final InquiryRepository inquiryRepository;
    private final NoticeRepository noticeRepository;
    private final ReplyRepository replyRepository;
    // 유저가 공지사항 보기
    @GetMapping("/notice")
    public ResponseEntity<?> myNoticeList() {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();
            List<Notice> noticeList = noticeRepository.findByUserId(userEmail); // 사용자의 게시글 조회
            return ResponseEntity.ok(noticeList); // 게시글 목록 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 조회 중 문제가 발생했습니다.");
        }
    }

    // 유저가 관리자에게 문의넣기
    @PostMapping("/writeboard")
    public ResponseEntity<?> saveBoard(@RequestBody @Validated InquiryDto.InquiryPost inquiryPost) {
        System.out.println("Before entering saveBoard");

        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 인증 객체 검증 후 데이터 추출
        String userEmail = authentication.getName(); // 이메일 가져오기
        System.out.println("User Email: " + userEmail);

        try {
            // DTO를 통해 Inquiry 엔티티 생성 및 DB에 저장
            Inquiry newInquiry = inquiryPost.toEntity(userEmail);
            inquiryRepository.save(newInquiry);

            return ResponseEntity.ok(Map.of(
                    "message", "게시글이 성공적으로 저장되었습니다.",
                    "createdAt", newInquiry.getCreatedAt()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 작성 중 문제가 발생했습니다.");
        }
    }

    // 유저가 자기가 쓴 글 보기
    @GetMapping("/myboard")
    public ResponseEntity<?> myInquiryList() {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();
            List<Inquiry> inquiryList = inquiryRepository.findByUserId(userEmail); // 사용자의 게시글 조회

            return ResponseEntity.ok(inquiryList); // 게시글 목록 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 조회 중 문제가 발생했습니다.");
        }
    }
    //관리자에게 문의한 내용 조회
    @GetMapping("/reply")
    public ResponseEntity<?> specificInquiryAndReplies(@RequestBody Map<String, Long> request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            Long inquiryId = request.get("id"); // 요청에서 게시판 ID 추출
            if (inquiryId == null) {
                return ResponseEntity.badRequest().body("게시판 번호가 필요합니다.");
            }

            String userEmail = authentication.getName();

            // 사용자와 연관된 게시글 중에서 ID가 일치하는 항목 확인
            List<Inquiry> userInquiries = inquiryRepository.findByUserId(userEmail); // 사용자 게시글 조회
            boolean isValidId = userInquiries.stream()
                    .anyMatch(inquiry -> inquiry.getInquiryId().equals(inquiryId));

            if (!isValidId) {
                return ResponseEntity.status(403).body("접근 권한이 없는 게시글 번호입니다.");
            }

            // 유효한 ID이면 해당 게시글(Inquiry) 조회
            Optional<Inquiry> inquiryOptional = inquiryRepository.findByInquiryId(inquiryId);
            if (inquiryOptional.isEmpty()) {
                return ResponseEntity.status(404).body("게시글을 찾을 수 없습니다.");
            }
            Inquiry inquiry = inquiryOptional.get();


            Optional<Reply> replies = replyRepository.findByInquiryId(inquiryId);

            // Inquiry와 Replies 응답으로 반환
            return ResponseEntity.ok(Map.of(
                    "inquiry", inquiry,
                    "replies", replies
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 및 답변 조회 중 문제가 발생했습니다.");
        }
    }

    // 자기가 쓴 글 삭제하기
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Long> request) {
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

    // 자기가 쓴 문의사항 수정하기
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> request) {
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
}