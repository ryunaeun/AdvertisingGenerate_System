package com.example.server.controller;

import com.example.server.dto.NoticeDto;
import com.example.server.model.Notice;
import com.example.server.model.User;
import com.example.server.repository.InquiryRepository;
import com.example.server.repository.NoticeRepository;
import com.example.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // URL 계층을 분리
@RequiredArgsConstructor
@Validated

public class AdminController {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    //관리자가 공지사항 쓰기
    @PostMapping("/wnotice")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> mkNotice(@RequestBody @Validated NoticeDto.NoticePost noticePost) {
        System.out.println("Before making notice");

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
            Notice newNotice = noticePost.toEntity(userEmail);
            noticeRepository.save(newNotice);

            return ResponseEntity.ok(Map.of(
                    "message", "공지사항이 성공적으로 저장되었습니다.",
                    "createdAt", newNotice.getCreatedAt()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지 작성 중 문제가 발생했습니다.");
        }
    }

    // 관리자가 쓴 공지사항 보기
    @GetMapping("/rnotice")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> myNoticeList() {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String userEmail = authentication.getName();
            var noticeList = noticeRepository.findByUserId(userEmail); // 사용자의 게시글 조회
            return ResponseEntity.ok(noticeList); // 게시글 목록 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("게시글 조회 중 문제가 발생했습니다.");
        }
    }

    //관리자가 user 조회
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers() {
        try {
            // 데이터베이스에서 모든 User 조회
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users); // 조회 결과 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 조회 중 문제가 발생했습니다.");
        }
    }

    // 관리자가 회원가입된 user 삭제
    @PostMapping("/users/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> request) {
        System.out.println("Delete Hello");
        try {
            String username = String.valueOf(request.get("username")); // 요청에서 ID 추출
            System.out.println(username);
            if (username == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            // 사용자 확인 후 삭제
            if (userRepository.existsByUsername(username)) {
                System.out.println("Delete Hello2");
                userRepository.deleteByUsername(username);
                return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 삭제 중 문제가 발생했습니다.");
        }
    }

    //관리자가 회원가입된 user 데이터 업데이트
    @PostMapping("/users/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> request) {
        try {
            String username = String.valueOf(request.get("username")); // 요청에서 기존 username 추출
            if (username == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // 새로운 username 처리 (추가)
                if (request.containsKey("newUsername")) {
                    user.setUsername(request.get("newUsername").toString());
                }

                if (request.containsKey("role")) {
                    user.setRole(request.get("role").toString());
                }
                if (request.containsKey("billing")) {
                    user.setBilling(request.get("billing").toString());
                }

                // 데이터 저장
                userRepository.save(user);
                return ResponseEntity.ok(Map.of(
                        "message", "사용자 이름이 성공적으로 업데이트되었습니다.",
                        "user", user.getUsername()
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
