package com.example.server;

import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 관리자 계정이 이미 존재하는지 확인
        if (userRepository.findByEmail("admin@example.com").isEmpty()) { // 정확한 이메일 비교
            User admin = new User();
            admin.setUsername("admin"); // 관리자 ID
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin1234@")); // 비밀번호 암호화
            admin.setRole("ROLE_ADMIN"); // 관리자 역할 설정
            admin.setSecretKey(java.util.UUID.randomUUID().toString()); // Secret Key 추가
            admin.setVerified(true);//이메일 인증 없이 관리자는 바로
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
        } else {
            System.out.println("Admin user already exists!");
        }
    }
}
