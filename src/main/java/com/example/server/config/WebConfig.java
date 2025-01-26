package com.example.server.config;

import com.example.server.filter.JwtAuthenticationFilter;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public WebConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, userRepository);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .authorizeHttpRequests(auth -> auth
                        // 인증이 필요 없는 요청
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                        .requestMatchers(
                                "/api/send-verification-code", // 회원가입용 인증 코드 요청
                                "/api/register", // 회원가입 완료
                                "/api/resend-verification-code", // 인증 코드 재전송
                                "/api/register-full", // 회원가입 최종 완료 API 추가
                                "/api/check-username", // 사용자 이름 중복 확인
                                "/api/login", // 로그인
                                "/api/refresh-token", // 토큰 갱신
                                "/api/request-reset-password", // 비밀번호 재설정 요청
                                "/api/reset-password", // 비밀번호 초기화
                                "/api/verify-email",// 이메일 인증 확인
                                "/api/verify-reset-code",
                                "/api/notice"
                        ).permitAll() // 공용 접근 허용
                        // 인증이 필요한 요청
                        .requestMatchers(
                                "/api/logout", // 로그아웃
                                "/api/current-user", // 현재 사용자 정보 확인
                                "/api/token-expiration" // 토큰 만료 확인
                        ).authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 전용
                        .requestMatchers("/api/login", "/api/register", "/api/refresh-token","/api/verify-email","/api/resend-verification-code","/api/forgot-password").permitAll() // 인증 없이 허용
                        .requestMatchers("/api/current-user","/api/logout","/api/users/**").authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 접근
                        .anyRequest().authenticated() // 그 외 모든 요청 인증 필요

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 상태 Stateless
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 모든 도메인 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
