package com.example.server.service;

import com.example.server.dto.UserDto;
import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    // 인증 코드 저장소 (이메일 → 인증 코드)
    private final ConcurrentHashMap<String, TempUserData> verificationCodeStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ResetPasswordData> resetPasswordStorage = new ConcurrentHashMap<>();

    // ** 회원가입 인증 코드 요청 **
    public void sendVerificationCode(UserDto.UserPostWithoutPassword userPost) {
        String email = userPost.getEmail();

        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 인증 코드 생성 및 저장
        String verificationCode = generateVerificationCode();
        // TempUserData로 임시 저장소에 저장
        TempUserData tempUserData = new TempUserData(
                userPost.getUsername(),
                verificationCode,
                LocalDateTime.now()
        );
        verificationCodeStorage.put(email, tempUserData);

        // 이메일 전송
        emailService.sendVerificationEmail(email, verificationCode);
    }

    // ** 인증 코드 재전송 **
    public void resendVerificationCode(String email) {
        // Temporary Storage에서 인증 코드 확인
        TempUserData tempUserData = verificationCodeStorage.get(email);

        if (tempUserData == null) {
            throw new IllegalArgumentException("인증 코드 요청 이력이 없습니다. 등록되지 않은 이메일입니다.");
        }

        // 인증 코드 재생성
        String newVerificationCode = generateVerificationCode();
        tempUserData.setVerificationCode(newVerificationCode); // 새 인증 코드 업데이트
        tempUserData.setIssuedAt(LocalDateTime.now()); // 발행 시간 갱신

        // 다시 저장
        verificationCodeStorage.put(email, tempUserData);

        // 이메일 전송
        emailService.sendVerificationEmail(email, newVerificationCode);
    }

    // ** 인증 코드 검증 및 사용자 저장 **
    public void saveUserAfterValidation(UserDto.VerificationRequest request) {
        String email = request.getEmail();
        // TempUserData에서 인증 데이터 가져오기
        TempUserData tempUserData = verificationCodeStorage.get(email);
        if (tempUserData == null) {
            throw new IllegalArgumentException("인증 코드 요청 이력이 없습니다.");
        }

        // 인증 코드 검증
        if (!tempUserData.getVerificationCode().equals(request.getVerificationCode())) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        if (tempUserData.getIssuedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        // 인증 성공 후 User 엔티티 생성 및 저장
        User user = new User();
        user.setUsername(tempUserData.getUsername());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("TEMPORARY")); // 임시 비밀번호 설정
        user.setVerified(true); // ** 이메일 인증 완료 처리 **
        userRepository.save(user);

        // 인증 데이터 제거
        verificationCodeStorage.remove(email);
    }

    public void completeUserRegistration(
            String email,
            String password,
            String companyName,
            String businessNumber,
            String businessFilePath
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원가입 정보가 올바르지 않습니다."));

        // 비밀번호 암호화 설정
        user.setPassword(passwordEncoder.encode(password));

        // 선택 데이터 설정
        user.setCompanyName(companyName);
        user.setBusinessNumber(businessNumber);
        user.setBusinessFilePath(businessFilePath);

        // 업데이트된 사용자 데이터 저장
        userRepository.save(user);
    }

    // ** 비밀번호 재설정 요청 **
    public void requestResetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 인증 코드 생성 및 저장
        String resetCode = generateVerificationCode();
        resetPasswordStorage.put(email, new ResetPasswordData(resetCode, LocalDateTime.now()));

        // 이메일 전송
        emailService.sendVerificationEmail(email, resetCode);
    }

    public boolean verifyResetCode(String email, String verificationCode) {
        ResetPasswordData resetData = resetPasswordStorage.get(email);

        if (resetData == null) {
            throw new IllegalArgumentException("비밀번호 재설정 인증 코드 요청 기록이 없습니다.");
        }

        if (!resetData.resetCode.equals(verificationCode)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        if (resetData.issuedAt.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        return true; // 인증 성공
    }

    // ** 비밀번호 재설정 **
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));

        // 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 인증 데이터 제거
        resetPasswordStorage.remove(email);
    }
    // ** 사용자명 중복 확인 **
    public boolean isUsernameDuplicate(String username) {
        return userRepository.existsByUsername(username); // 단순 존재 확인
    }

    // ** Refresh Token 저장 **
    public void saveRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public void registerFullUser(
            String username,
            String email,
            String password,
            String companyName,
            String businessNumber,
            String businessFilePath
    ) {
        // 중복 데이터 확인
        if (userRepository.existsByUsername(username) || userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디 또는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 데이터 생성
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setCompanyName(companyName); // 선택적 정보
        user.setBusinessNumber(businessNumber); // 선택적 정보
        user.setBusinessFilePath(businessFilePath); // 선택적 업로드 파일 경로

        user.setCreatedAt(LocalDateTime.now()); // 생성일 설정
        user.setBillingDate(LocalDateTime.now()); // 기본 빌링 일 설정
        user.setRole("USER"); // 기본 사용자 역할

        // 사용자 저장
        userRepository.save(user);
    }

    public void updateLoginTime(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 시간을 nowAt에 갱신
        user.setNowAt(LocalDateTime.now());
        userRepository.save(user);
    }



    // ** 이메일 인증 처리 **
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 인증 코드 및 만료 시간 확인
        if (!user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        if (user.getVerificationCodeIssuedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        // 이메일 인증 완료 처리
        user.setVerified(true);
        user.setVerificationCode(null); // 인증 코드 제거
        user.setVerificationCodeIssuedAt(null); // 인증 코드 발행 시간 제거

        userRepository.save(user);
    }

    public void deleteUserByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }

    // 인증 코드 생성 (6자리 난수)
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // 고유 SecretKey 생성
    private String generateSecretKey() {
        return UUID.randomUUID().toString();
    }

    // ** 내부 클래스: 인증 데이터 구조 **
    @Getter
    @Setter
    private static class TempUserData {
        private final String username;
        private String verificationCode;
        private LocalDateTime issuedAt;

        public TempUserData(String username, String verificationCode, LocalDateTime issuedAt) {
            this.username = username;
            this.verificationCode = verificationCode;
            this.issuedAt = issuedAt;
        }
    }

    private record ResetPasswordData(String resetCode, LocalDateTime issuedAt) {
    }
}
