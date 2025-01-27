package com.example.server.dto;


import com.example.server.model.Reply;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReplyDto {

    private static final Logger logger = LoggerFactory.getLogger(ReplyDto.class);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    // 게시글 작성 요청 데이터
    public static class ReplyPost {
        @NotNull(message = "게시글 번호는 필수입니다.")
        private Long boardId;

        @NotBlank(message = "보낼 사람은 필수입니다.")
        private String email;

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        @Schema(hidden = true)
        private LocalDateTime createdAt; // 읽기 전용 (응답 시 사용)
        }
        // DTO -> 엔티티 변환 메서드
}