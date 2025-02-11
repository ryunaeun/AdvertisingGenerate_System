package com.example.server.dto;

import com.example.server.model.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.media.Schema;

public class BoardDto {

    private static final Logger logger = LoggerFactory.getLogger(BoardDto.class);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    // 게시글 작성 요청 데이터
    public static class BoardPost {
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        @Schema(hidden = true)
        private LocalDateTime createdAt; // 읽기 전용 (응답 시 사용)

        // DTO -> 엔티티 변환 메서드
        public Board toEntity(String username) {
            logger.debug("Converting BoardPost DTO to Entity. Username: {}", username);
            logger.debug("Title: {}, Content: {}", title, content);

            Board board = new Board();
            board.setEmail(username);       // 로그인한 사용자 ID 설정
            board.setTitle(this.title);    // 제목 설정
            board.setContent(this.content); // 내용 설정
            board.setBoardOrder(0);        // 기본 게시판 순서 설정
            board.setCreatedAt(LocalDateTime.now()); // 작성 시간 자동 설정

            return board;
        }

        public static class FindUser{
            @NotBlank(message = "이름은 필수 입력 값입니다.")
            @Size(min = 3, max = 20, message = "이름은 3자 이상 20자 이하여야 합니다.")
            @Pattern(regexp = "^[a-zA-Z]+$", message = "이름은 영어 알파벳만 가능합니다.")
            private String email;
            @NotBlank(message = "제목은 필수입니다.")
            private String title;

            @NotBlank(message = "내용은 필수입니다.")
            private String content;
        }
        @Getter
        @Setter
        public static class DeleteRequest {
            private int boardOrder; // 삭제할 사용자의 이메일
        }
    }
}