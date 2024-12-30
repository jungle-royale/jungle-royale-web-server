package com.example.jungleroyal.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class JungleFileUtils {
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";

    public String handleFileUpload(MultipartFile file, String existingFilePath) {
        if (file == null || file.isEmpty()) {
            return existingFilePath; // 파일이 없으면 기존 경로 유지
        }

        try {
            // 업로드 디렉토리 확인 및 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유 파일명 생성 및 저장
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = uploadPath.resolve(fileName).toString();
            file.transferTo(Paths.get(filePath));

            // 기존 파일 삭제 (선택)
            if (existingFilePath != null) {
                Files.deleteIfExists(Paths.get(existingFilePath));
            }

            return filePath; // 새로운 파일 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }

}
