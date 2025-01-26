package com.example.server.dto;


import com.example.server.model.Reply;
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
        private String userId;

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        private LocalDateTime createdAt; // 읽기 전용 (응답 시 사용)

        // DTO -> 엔티티 변환 메서드
        public Reply toEntity(String userId) {
            logger.debug("Converting BoardPost DTO to Entity. UserId: {}", userId);
            logger.debug("Title: {}, Content: {}", title, content);

            Reply reply = new Reply();
            reply.setUserId(userId);       // 로그인한 사용자 ID 설정
            reply.setTitle(this.title);    // 제목 설정
            reply.setContent(this.content); // 내용 설정
            reply.setCreatedAt(LocalDateTime.now()); // 작성 시간 자동 설정

            // createdAt 필드는 DTO에서 읽기 전용으로 전달 (엔티티 시간 반영)
            this.createdAt = reply.getCreatedAt();

            return reply;
        }
    }
}