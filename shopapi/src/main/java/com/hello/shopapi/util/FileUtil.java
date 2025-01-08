package com.hello.shopapi.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component  // 스프링빈으로 등록
@Slf4j
public class FileUtil {

    @Value("${shopapi.upload.path}")
    private String uploadPath;

    @PostConstruct // 의존성 주입 이후, 초기화 수행메서드 위에 부착 -> 한번만 수행되는걸 보장
    public void init() {
        // 저장할 폴더 File 객체 생성
        File tempDir = new File(uploadPath);
        // 실제 폴더가 존재하지 않으면
        if(!tempDir.exists()) {
            tempDir.mkdir(); // 폴더 생성
        }
        uploadPath = tempDir.getAbsolutePath();
        log.info("Upload path: {}", uploadPath);
    }

    // 파일 저장 처리
    public List<String> saveFiles(List<MultipartFile> files) {
        // 매개변수로 받은 files 리스트 null체크
        if(files == null || files.isEmpty()) {
            return List.of();  // 빈 List 리턴
        }

        List<String> fileNames = new ArrayList<>();
        for(MultipartFile file : files) {
            String orgName = file.getOriginalFilename();
            String ext = orgName.substring(orgName.lastIndexOf("."));
            String saveName = UUID.randomUUID().toString() + ext; // 저장 파일명 UUID로
            Path savePath = Paths.get(uploadPath, saveName); // 경로 생성
            log.info("Save path: {}", savePath);

            try {
                Files.copy(file.getInputStream(), savePath); // 파일 저장

                // 썸네일 생성 추가
                String contentType = file.getContentType();
                log.info("Content type: {}", contentType);
                // 이미지 파일 체크 -> 맞다면 썸네일 추가
                if(contentType != null && contentType.startsWith("image")) {
                    Path thumbPath = Paths.get(uploadPath, "th_" + saveName); // 썸네일저장경로+파일명
                    Thumbnails.of(savePath.toFile())  // 원본 파일 객체
                            .size(200, 200) // 사이즈
                            .toFile(thumbPath.toFile());  // 썸네일 저장
                }
                fileNames.add(saveName); // 이름 배열에 저장된이름 추가
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return fileNames;
    }


    // 파일 정보 리턴 -> 이미지 브라우저에 보여주기 위한 파일리소스 get메서드
    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        if(!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }


    // 파일 삭제 처리
    public void deleteFiles(List<String> fileNames) {
        if(fileNames == null || fileNames.isEmpty()) {
            return;
        }
        fileNames.forEach(fileName -> {
            Path filePath = Paths.get(uploadPath, fileName);
            Path thumbPath = Paths.get(uploadPath, "th_" + fileName);
            try {
                // 파일이 존재하면 지워라!
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbPath);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
