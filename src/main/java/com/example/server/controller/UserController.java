package com.example.server.controller;

import com.example.server.dto.*;
import com.example.server.model.Board;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    // 공란 부분 작성해야함
    
    @Operation(
            summary = "회원가입 과정 1번째",
            description = "사용자 이름 중복 여부 체크"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용할 수 있는 이름입니다"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    // ** 아이디 중복 확인 API **
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameDuplicate(@RequestParam String username) {
        boolean isDuplicate = userService.isUsernameDuplicate(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "회원가입 과정 2번째",
            description = "인증번호 요청"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호가 요청되었습니다"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    // ** 회원가입 인증 코드 요청 API **
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody @Valid UserDto.UserPostWithoutPassword userPost) {
        try {
            userService.sendVerificationCode(userPost);
            return ResponseEntity.ok("인증 코드가 이메일로 성공적으로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }


    @Operation(
            summary = "회원가입 과정 3번째",
            description = "인증코드 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증코드를 확인했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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


    @Operation(
            summary = "회원가입 과정 4번째",
            description = "회원가입 양식 작성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 완료되었습니다"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    
    //비밀번호 치는 회원가입 최종버튼
    @PostMapping("/register-full")
    public ResponseEntity<?> registerFullUser(@ModelAttribute UserDto.FullUserPostWithFile userDto) {
        try {
            // 선택적 파일 업로드 경로 처리
            String storedFilePath = userDto.getBusinessFile() != null
                    ? fileService.saveFile(userDto.getBusinessFile())
                    : null;

            // 등록 요청한 이메일 확인 후 데이터 갱신
            userService.completeUserRegistration(
                    userDto.getEmail(),
                    userDto.getPassword(),
                    userDto.getCompanyName(),
                    userDto.getBusinessNumber(),
                    storedFilePath
            );

            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            // 실패 시 해당 이메일로 등록된 사용자 삭제
            userService.deleteUserByEmail(userDto.getEmail());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 실패 시 해당 이메일로 등록된 사용자 삭제
            userService.deleteUserByEmail(userDto.getEmail());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 처리 중 오류가 발생했습니다.");
        }
    }
    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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

    @Operation(
            summary = "마이페이지에서 계정 삭제 요청",
            description = "이메일 입력 후 계정 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정이 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @DeleteMapping("/user-info/delete")
    @jakarta.transaction.Transactional
    public ResponseEntity<?> deleteMyself(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username"); // 요청에서 ID 추출
            if (username == null) {
                return ResponseEntity.badRequest().body("사용자 ID가 필요합니다.");
            }

            // 사용자 확인 후 삭제
            if (userRepository.existsByUsername(username)) {
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

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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


    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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


    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
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



    @Operation(
            summary = "로그인",
            description = "로그인(관리자 계정 ID : admin@example.com, 관리자계정 PW : Admin1234@)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용할 수 있는 이름입니다"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

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

                //로그인 성공 시, nowAt 필드에 현재 시간 갱신
                userService.updateLoginTime(user.getEmail());


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

    @Operation(
            summary = "로그아웃",
            description = "로그아웃"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 되었습니다"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    
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

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })
    
    // 리프레시 토큰을 통한 액세스 토큰 갱신
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null) {
                return ResponseEntity.badRequest().body("Refresh Token이 제공되지 않았습니다.");
            }

            if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 만료되었습니다.");
            }

            String email = jwtUtil.extractEmail(refreshToken);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (!refreshToken.equals(user.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("저장된 Refresh Token과 일치하지 않습니다.");
            }

            String newAccessToken = jwtUtil.generateToken(email, user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 갱신 중 문제가 발생했습니다.");
        }
    }

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

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

    @Scheduled(fixedRate = 3600000) // 1분마다 실행
    @Transactional
    public void removeExpiredRefreshTokens() {
        Date now = new Date();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getRefreshToken() != null && jwtUtil.isRefreshTokenExpired(user.getRefreshToken())) {
                user.setRefreshToken(null);
                userRepository.save(user);
            }
        }
    }

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    //내 정보 - 기본정보 조회만
    @GetMapping("/user-info/personal")
    public ResponseEntity<?> getPersonalUserInfo(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 개인정보 데이터 반환
            Map<String, Object> personalInfo = new HashMap<>();
            personalInfo.put("username", user.getUsername());
            personalInfo.put("email", user.getEmail());
            personalInfo.put("createdAt", user.getCreatedAt());
            personalInfo.put("nowAt", user.getNowAt());
            personalInfo.put("companyName", user.getCompanyName());
            personalInfo.put("businessNumber", user.getBusinessNumber());
            personalInfo.put("businessFilePath", user.getBusinessFilePath());

            return ResponseEntity.ok(personalInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 개인정보 조회 실패");
        }
    }

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    //내 정보 - 기본 정보 - 아이디, 회사명, 사업자 번호 수정
    @PutMapping("/user-info/update/text")
    @Transactional
    public ResponseEntity<?> updateUserTextInfo(
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = authentication.getName();
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 업데이트 가능한 필드 정의
        Set<String> validFields = Set.of("username", "companyName", "businessNumber");

        // 요청 확인 및 유효성 검증
        if (request != null && !request.isEmpty()) {
            for (String key : request.keySet()) {
                if (!validFields.contains(key)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 필드: " + key);
                }
            }

            // username 필드 유효성 검사 및 업데이트
            if (request.containsKey("username")) {
                String username = request.get("username");
                if (username == null || username.isBlank() || username.length() < 3 || username.length() > 20) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username은 3자 이상 20자 이하로 입력하세요.");
                }
                user.setUsername(username);
            }

            // companyName 필드 업데이트
            if (request.containsKey("companyName")) {
                String companyName = request.get("companyName");
                if (companyName != null) {
                    user.setCompanyName(companyName);
                }
            }

            // businessNumber 필드 업데이트
            if (request.containsKey("businessNumber")) {
                String businessNumber = request.get("businessNumber");
                if (businessNumber != null) {
                    user.setBusinessNumber(businessNumber);
                }
            }


            // userRepository.save는 @Transactional로 인해 생략 가능 (변경 감지가 활성화됨)
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업데이트할 필드가 존재하지 않습니다.");
        }

        // 처리 완료 후 성공 메시지
        return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
    }


    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    //내 정보 - 기본 정보 - 사업자 폴더 수정
    @PutMapping("/user-info/update/file")
    public ResponseEntity<?> updateUserFileInfo(
            @RequestParam(required = false) MultipartFile businessFile,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 파일 업로드 처리
        if (businessFile != null && !businessFile.isEmpty()) {
            try {
                // 새 파일 저장
                String savedFilePath = fileService.saveFile(businessFile);

                // 기존 파일 삭제
                String currentFilePath = user.getBusinessFilePath();
                if (currentFilePath != null) {
                    fileService.deleteFile(currentFilePath);
                }

                // 새 파일 경로 업데이트
                user.setBusinessFilePath(savedFilePath);

            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 처리 중 문제가 발생했습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업로드된 파일이 없습니다.");
        }

        // 저장
        userRepository.save(user);

        return ResponseEntity.ok("파일이 성공적으로 업데이트되었습니다.");
    }

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    //내 정보 - 결제 정보 조회만
    @GetMapping("/user-info/payment")
    public ResponseEntity<?> getPaymentUserInfo(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 결제 정보 데이터 반환
            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("billing", user.getBilling());
            paymentInfo.put("billingDate", user.getBillingDate());

            return ResponseEntity.ok(paymentInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 결제 정보 조회 실패");
        }
    }

    @Operation(
            summary = "공란",
            description = "공란"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공란"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    })

    @PutMapping("/user-info/update-password")
    public ResponseEntity<?> updatePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {
        try {
            // 현재 로그인된 사용자 이메일 가져오기
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String email = authentication.getName();

            // 요청 데이터에서 새 비밀번호와 확인 비밀번호 가져오기
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            // 입력값 검증
            if (newPassword == null || confirmPassword == null ||
                    newPassword.isBlank() || confirmPassword.isBlank()) {
                return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인을 모두 입력해야 합니다.");
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

            // 비밀번호 복잡성 검증 (기존 정규식을 사용할 수 있음)
            if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                return ResponseEntity.badRequest().body("비밀번호는 8자 이상, 대문자, 소문자, 숫자, 특수문자를 반드시 포함해야 합니다.");
            }

            // 비밀번호 변경 서비스 호출
            userService.resetPassword(email, newPassword);

            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
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


    // ** 이메일 인증 처리 API **
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        try {
            userService.verifyEmail(email, code);
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

}


