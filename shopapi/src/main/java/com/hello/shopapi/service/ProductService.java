package com.hello.shopapi.service;

import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.dto.ProductDTO;

public interface ProductService {

    // 목록 조회
    PageResponseDTO<ProductDTO> getProductList(PageRequestDTO requestDTO);
    // 상품 등록
    Long regist(ProductDTO productDTO);
    // 상품 1개 조회
    ProductDTO getProduct(Long pno);
    // 상품 수정
    void modify(ProductDTO productDTO);
    // 상품 삭제
    void remove(Long pno);


}
