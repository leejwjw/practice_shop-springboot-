package com.hello.shopapi.repository;

import com.hello.shopapi.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Slf4j
class ProductRepositoryTests {
    // 상품저장, 상품 pk로 1개 조회 테스트 코드 작성해서 확인해보기
    @Autowired
    ProductRepository productRepository;

    @Test
    public void testInsert() {
        for(int i = 1; i <= 10; i++) {
            Product product = Product.builder()
                    .pname("상품 " + i)
                    .price(100 * i)
                    .pdesc("상품설명 " + i)
                    .build();
            product.addImageByFilename(UUID.randomUUID().toString() + ".jpg");
            product.addImageByFilename(UUID.randomUUID().toString() + ".jpg");
            productRepository.save(product);
        }
    }

    @Transactional
    @Test
    public void testFindById() {
        Long pno = 1L;
        Optional<Product> result = productRepository.findById(pno);
        Product product = result.orElseThrow();
        log.info("product : {}" , product);
        log.info("product imageList: {}", product.getImageList());
    }

    @Test
    public void testSelectOne() {
        Long pno = 1L;
        Optional<Product> result = productRepository.selectOne(pno);
        Product product = result.orElseThrow();
        log.info("selectOne - product : {}", product);
        log.info("selectOne - product imageList: {}", product.getImageList());
    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {
        Long pno = 3L;
        productRepository.updateToDelete(pno, true);
    }

    @Test
    public void testUpdate() {
        Long pno = 5L;
        Product product = productRepository.selectOne(pno).orElseThrow();
        product.changeDesc("상품 정보 수정수정...");
        product.changePrice(123450);

        // 첨부파일 수정
        product.clearImageList();
        product.addImageByFilename(UUID.randomUUID().toString() + "new.jpg");
        product.addImageByFilename(UUID.randomUUID().toString() + "new.jpg");
        product.addImageByFilename(UUID.randomUUID().toString() + "new.jpg");

        // Dirty Checking 아닌, save -> merge 시키는 형태로 처리
        productRepository.save(product);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pno").descending());
        Page<Object[]> result = productRepository.selectList(pageable);
        result.getContent().forEach(p -> log.info("product : {}", Arrays.toString(p)));

    }





}