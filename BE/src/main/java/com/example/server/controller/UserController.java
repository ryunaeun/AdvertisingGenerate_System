package com.example.server.controller;

import com.example.server.dto.*;
import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import com.example.server.service.EmailService;
import com.example.server.service.FileStorageService;
import com.example.server.service.UserService;
import com.example.server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileStorageService fileService;


    // ** 아이디 중복 확인 API **
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameDuplicate(@RequestParam String username) {
        boolean isDuplicate = userService.isUsernameDuplicate(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }


    // ** 회원가입 인증 코드 요청 API **
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody @Valid UserDto.UserPostWithoutPassword userPostWithoutPassword) {
        try {
            userService.sendVerificationCode(userPostWithoutPassword);
            return ResponseEntity.ok("인증 코드가 이메일로 성공적으로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    // ** 인증 코드 확인 **
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto.VerificationRequest verificationRequest) {
        try {
            userService.saveUserAfterValidation(verificationRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 성공적으로 처리되었습니다.");
            response.put("email", verificationRequest.getEmail()); // 이후 API에서 식별 가능
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 처리 중 오류가 발생했습니다.");
        }
    }


    //비밀번호 치는 회원가입 최종버튼
    @PostMapping("/register-full")
    public ResponseEntity<?> registerFullUser(
            @ModelAttribute UserDto.FullUserPostWithFile userDto
    ) {
        try {
            // 선택적 파일 업로드 경로 처리
            String storedFilePath = userDto.getBusinessFile() != null
                    ? fileService.saveFile(userDto.getBusinessFile())
                    : null;

            // 등록 요청한 이메일 확인 후 데이터 갱신
            userService.completeUserRegistration(
                    userDto.getEmail(), // 이미 이메일로 가입된 유저여야 함
                    userDto.getPassword(),
                    userDto.getCompanyName(),
                    userDto.getBusinessNumber(),
                    storedFilePath
            );

            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    // ** 인증 코드 재전송 API **
    @PostMapping("/resend-verification-code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            userService.resendVerificationCode(email);
            return ResponseEntity.ok("인증 코드가 이메일로 다시 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    //로그인 화면에서 이메일을 입력하고 옆에 인증번호를 달라고 할때 api
    @PostMapping("/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestParam String email) {
        try {
            userService.requestResetPassword(email);
            return ResponseEntity.ok("비밀번호 재설정을 위한 인증 코드가 이메일로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    //인증번호 6자리를 입력하고, 인증하기 버튼을 눌렀을때의 api
    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String verificationCode = request.get("verificationCode");

            boolean isVerified = userService.verifyResetCode(email, verificationCode);
            if (isVerified) {
                return ResponseEntity.ok("인증 번호 확인이 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("인증 번호가 올바르지 않거나 만료되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    //새로 만들 비밀번호를 입력하고 변경하기 버튼을 누를때 api
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email"); // 서버에 저장된 이메일을 활용하거나 요청 데이터에서 가져옴
            String newPassword = request.get("newPassword");

            userService.resetPassword(email, newPassword);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto.LoginRequest loginRequest) {
        try {
            // 이메일로 사용자 조회
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 이메일 인증 확인
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이메일 인증이 완료되지 않았습니다.");
            }

            // 비밀번호 일치 여부 확인
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Access Token 및 Refresh Token 생성
                String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
                String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

                // Refresh Token 저장
                userService.saveRefreshToken(user.getEmail(), refreshToken);

                // 응답 데이터 구성
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("role", user.getRole());
                response.put("message", "로그인 성공");

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 문제가 발생했습니다.");
        }
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        System.out.println("Test");
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰이 필요합니다.");
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // Refresh Token 무효화
            user.setRefreshToken(null);
            userRepository.save(user);

            // 인증 정보 초기화
            SecurityContextHolder.clearContext();
            System.out.println("Logout");
            return ResponseEntity.ok("Logout Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Trouble Issue");
        }
    }

    // 리프레시 토큰을 통한 액세스 토큰 갱신
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            System.out.println("Received refreshToken: " + refreshToken);

            if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
                System.out.println("Refresh token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 만료되었습니다.");
            }

            String email = jwtUtil.extractEmail(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (!jwtUtil.isStoredRefreshTokenValid(refreshToken, user.getRefreshToken())) {
                System.out.println("Refresh token invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않습니다.");
            }

            String newAccessToken = jwtUtil.generateToken(email, user.getRole());
            System.out.println("Generated new accessToken: " + newAccessToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 갱신 중 문제가 발생했습니다.");
        }
    }

    // 토큰 만료 시간 확인
    @GetMapping("/token-expiration")
    public ResponseEntity<?> getTokenExpiration(@RequestParam String token) {
        try {
            Date expirationDate = jwtUtil.getExpirationDate(token);
            return ResponseEntity.ok(expirationDate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 만료 시간 조회 실패");
        }
    }

    //
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            System.out.println("Authentication 객체: " + authentication);
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String email = authentication.getName();
            System.out.println("인증된 사용자 이메일: " + email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            System.err.println("사용자 정보 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 조회 실패");
        }
    }

    private final JavaMailSender javaMailSender;

    //이메일로 받는 메세지
    public void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드는 다음과 같습니다: " + code + "\n이 코드를 인증 화면에 입력하세요.");
        javaMailSender.send(message);
    }

    //이메일 인증 처리
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        // 데이터베이스에서 사용자를 이메일로 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        System.out.println("입력된 이메일: " + email);
        System.out.println("DB 검색 결과: " + user);
        // 인증 코드가 일치하고 유효 시간 내에 있는지 확인
        if (user.getVerificationCode().equals(code) &&
                user.getVerificationCodeIssuedAt().isAfter(LocalDateTime.now().minusMinutes(3))) {
            user.setVerified(true); // 이메일 인증 완료 처리
            userRepository.save(user); // 사용자 저장
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 만료되었거나 잘못되었습니다.");
        }
    }

}


