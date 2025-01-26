package com.example.server.filter;

import com.example.server.model.User;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        //System.out.println("Start1");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //System.out.println("Start2");
            String token = authHeader.substring(7);
            System.out.println(token);
            try {
                String email = jwtUtil.extractEmail(token);
                System.out.println(email);
                if (jwtUtil.validateToken(token)) {
                    System.out.println("Hello " + email);
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new IllegalArgumentException("NotFound"));
                    System.out.println("안녕하세요");
                    System.out.println(user);
                    System.out.println(user.getEmail());
                    System.out.println(user.isVerified());
                    // 이메일 인증 검사
                    if (!user.isVerified()) {
                        System.out.println("S1");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        System.out.println("S2");
                        response.getWriter().write("Not Verified");
                        System.out.println("S3");
                        return;
                    }

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("S4");
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    System.out.println("S5");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("S6");
                } else {
                    System.out.println("InValid");
                    throw new IllegalArgumentException("JWT가 유효하지 않습니다.");
                }
            } catch (Exception e) {
                System.out.println("InValid2");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT: " + e.getMessage());
                System.out.println("InValid3");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}