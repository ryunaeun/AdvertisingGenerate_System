package com.example.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


/**
 * 사용자와 관련된 데이터 전송 객체
 */
@NoArgsConstructor
public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 회원가입 요청 데이터 (비밀번호 포함)
    public static class UserPost {
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 3, max = 20, message = "이름은 3자 이상 20자 이하여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "이름은 영어 알파벳만 가능합니다.")
        private String username;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
        )
        private String password;

        // 선택 입력 값
        private String companyName;  // 회사명 (선택적 입력)
        private String businessNumber; // 사업자등록번호 (선택적 입력)
        private String businessFilePath; // 파일 업로드 경로 (선택적 입력)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 회원가입 요청 데이터 (비밀번호 제외)
    public static class UserPostWithoutPassword {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 3, max = 20, message = "이름은 3자 이상 20자 이하여야 합니다.")
        private String username;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 로그인 요청 데이터
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 이메일 인증 요청 데이터
    public static class VerificationRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "인증 코드는 필수 입력 값입니다.")
        private String verificationCode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 비밀번호 재설정 요청 데이터
    public static class ResetPasswordRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "인증 코드는 필수 입력 값입니다.")
        private String verificationCode;

        @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
        )
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 사용자의 응답 데이터
    public static class UserResponse {
        private String username;
        private String email;
        private String billing;
        private LocalDateTime createdAt;
        private LocalDateTime billingDate;
        private String role;
        private boolean isVerified;
        private String companyName;
        private String businessNumber;
        private String businessFilePath;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 이메일 인증 데이터 전송
    public static class EmailVerificationStatus {
        private String email;
        private boolean isVerified;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // 사용자의 전체 업데이트 요청
    public static class FullUserUpdate {
        private String username;
        private String password;
        private String companyName;
        private String businessNumber;
        private String businessFilePath;
    }



    // 기존 UserPost DTO 확장
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullUserPost {
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 3, max = 20, message = "이름은 3자 이상 20자 이하여야 합니다.")
        private String username;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 주소를 입력하세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
        )
        private String password;

        // 선택 입력 필드
        private String companyName; // 회사 이름
        private String businessNumber; // 사업자 등록 번호
    }

    // 파일 업로드 요청을 위한 DTO (또는 MultipartFile 직접 사용 가능)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullUserPostWithFile extends FullUserPost {
        private MultipartFile businessFile; // 파일 업로드
    }
}
