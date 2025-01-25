package com.example.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 3600 * 1000; // 7일


    // Access Token 생성
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) // 이메일 저장
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 유효
                .signWith(SECRET_KEY)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String extractEmail(String token) {
        try {
            Claims claims = getClaims(token);
            String email = claims.getSubject(); // JWT의 subject가 이메일인지 확인
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("JWT에서 이메일을 추출할 수 없습니다.");
            }
            return email;
        } catch (Exception e) {
            System.err.println("JWT에서 이메일 추출 실패: " + e.getMessage());
            return null;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        System.out.println("Start : " + token);
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                System.err.println("JWT 토큰이 만료되었습니다.");
                return false;
            }
            System.out.println("End : " + claims);
            return true;
        } catch (Exception e) {
            System.err.println("JWT 토큰 유효성 검사 실패: " + e.getMessage());
            return false;
        }
    }

    // Refresh Token과 저장된 토큰 일치 여부 확인
    public boolean isStoredRefreshTokenValid(String token, String storedToken) {
        return token != null && token.equals(storedToken);
    }

    // 토큰 만료 시간 반환
    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    // Claims 추출 (Private 메서드)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isRefreshTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // 토큰 파싱 실패 시 만료된 것으로 처리
        }
    }


}