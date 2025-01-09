package com.hello.shopapi.controller;

import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.dto.ProductDTO;
import com.hello.shopapi.service.ProductService;
import com.hello.shopapi.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final FileUtil fileUtil;
    private final ProductService productService;

    // 저장
    @PostMapping("/")
    public Map<String, Long> regist(ProductDTO productDTO) {
        log.info("/products/POST - productDTO : {}", productDTO);
        // 파일 저장 처리
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);
        log.info("/products/POST - uploadFileNames : {}", uploadFileNames);
        productDTO.setUploadFileNames(uploadFileNames); // DTO에 저장된 파일이름들 추가

        // service 호출 -> DB 저장
        Long pno = productService.regist(productDTO);

        return Map.of("RESULT", pno);
    }

    // 이미지 조회:  http://localhost:8080/api/products/view/테스트용이미지명.확장자명
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewImage(@PathVariable("filename") String filename) {
        return fileUtil.getFile(filename);
    }

    // 게시글 목록 요청
    //@PreAuthorize("hasRole('ROLE_ADMIN')") // 권한 추가 예시
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("/products/LIST - pageRequestDTO : {}", pageRequestDTO);
        return productService.getProductList(pageRequestDTO);
    }

    // 상품 1개 조회
    @GetMapping("/{pno}")
    public ProductDTO getProduct(@PathVariable("pno") Long pno) {
        return productService.getProduct(pno);
    }

    // 상품 수정
    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable("pno") Long pno, ProductDTO productDTO) {
        log.info("/products/modify - pno : {}", pno);
        log.info("/products/modify - productDTO : {}", productDTO);

        productDTO.setPno(pno);
        ProductDTO oldDTO = productService.getProduct(pno); // DB에 저정된 이전 버전 조회
        List<String> oldFileNames = oldDTO.getUploadFileNames(); // 기존 파일명들

        List<MultipartFile> files = productDTO.getFiles(); // 새로 업로드할 파일들
        List<String> newFileNames = fileUtil.saveFiles(files); // 추가로 저장된 새파일명들

        List<String> remainFileNames = productDTO.getUploadFileNames(); // 기존에 유지될 파일명들

        // 유지되는 파일에 새로 추가된 파일명 합치기
        if(newFileNames != null && !newFileNames.isEmpty()) {
            remainFileNames.addAll(newFileNames);
        }

        // DB 수정처리
        productService.modify(productDTO);

        // 기존에 저장된 파일들 중 삭제파일 찾아 지우기
        if(oldFileNames != null && !oldFileNames.isEmpty()) {
            // 파일명 목록 찾기
            List<String> removeFileNames = oldFileNames.stream()
                    .filter(fileName -> !remainFileNames.contains(fileName))
                    .collect(Collectors.toList());
            // 파일들 삭제
            fileUtil.deleteFiles(removeFileNames);
        }
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable("pno") Long pno) {
        List<String> fileNamesToRemove = productService.getProduct(pno).getUploadFileNames();
        productService.remove(pno); // delFlag = true로 변경
        fileUtil.deleteFiles(fileNamesToRemove); // 이미지 파일 삭제
        return Map.of("RESULT", "SUCCESS");
    }


}
