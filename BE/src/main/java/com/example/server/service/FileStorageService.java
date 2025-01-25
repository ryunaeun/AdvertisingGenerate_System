package com.example.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${file.upload-dir:C:\\\\Users\\\\User\\\\Desktop\\\\input_file\\\\}") // 기본 파일 저장 경로
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        // 1. 파일 예외 처리
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 2. 디렉토리가 없으면 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // 디렉토리 생성
            if (!created) {
                throw new IOException("파일 저장 디렉토리를 생성하지 못했습니다: " + uploadDir);
            }
        }

        // 3. 파일 이름 및 저장 경로 설정
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();

        // 유효한 확장자인지 검증
        if (!List.of("jpg", "jpeg", "png", "pdf").contains(fileExtension)) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileExtension);
        }

        // 고유 파일명 생성
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        // 4. 파일 저장
        Files.copy(file.getInputStream(), filePath);

        // 5. 저장된 파일 경로 반환
        return filePath.toString();
    }
    // 파일 삭제 메서드
    public void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("파일이 성공적으로 삭제되었습니다: " + filePath);
                } else {
                    System.out.println("파일 삭제 실패: " + filePath);
                }
            }
        } catch (Exception e) {
            System.out.println("파일 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
