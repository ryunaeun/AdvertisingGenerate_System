package com.example.server.controller;

import com.example.server.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.server.model.User;

import com.example.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/admin/users") // URL 계층을 분리
@RequiredArgsConstructor
@Validated

public class AdminUserController {
    private final UserRepository userRepository;
    //관리자가 user 조회

    @Operation(
            summary = "회원 조회(관리자 로그인 후 이용가능)",
            description = "관리자가 사이트 내 가입 회원 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정이 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(Pageable pageable) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            // 데이터베이스에서 모든 User 조회
            // 데이터베이스에서 pageable 형태로 User 조회
            Page<User> userPage = userRepository.findAll(pageable);
            return ResponseEntity.ok(userPage); // 조회 결과 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 조회 중 문제가 발생했습니다.");
        }
    }

    @Operation(
            summary = "회원 삭제(관리자 로그인 후 이용가능)",
            description = "관리자가 사이트 내 가입 회원 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정이 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestBody UserDto.DeleteRequest request) {
        try {
            // 인증된 사용자 확인
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 요청에서 이메일 추출
            String email = request.getEmail();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수 입력값입니다.");
            }

            // 이메일을 기반으로 사용자 삭제
            if (userRepository.existsByEmail(email)) {
                userRepository.deleteByEmail(email);
                return ResponseEntity.ok(Map.of(
                        "message", "사용자가 성공적으로 삭제되었습니다.",
                        "email", email
                ));
            } else {
                return ResponseEntity.status(404).body("해당 이메일의 사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 삭제 중 문제가 발생했습니다.");
        }
    }


    @Operation(
            summary = "회원 수정(관리자 로그인 후 이용가능)",
            description = "관리자가 사이트 내 가입 회원 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정이 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UserDto.UserChange request) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 기존 사용자 이름 추출
            String username = request.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            // 사용자 조회
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            User user = userOpt.get();

            // 값이 있는 경우에만 업데이트
            if (request.getNewUsername() != null && !request.getNewUsername().isEmpty()) {
                user.setUsername(request.getNewUsername());
            }
            if (request.getRole() != null && !request.getRole().isEmpty()) {
                user.setRole(request.getRole());
            }
            if (request.getBilling() != null && !request.getBilling().isEmpty()) {
                user.setBilling(request.getBilling());
            }

            // 데이터 저장
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "사용자 정보가 성공적으로 업데이트되었습니다.",
                    "user", user.getUsername()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("사용자 정보 업데이트 중 문제가 발생했습니다.");
        }
    }

}
