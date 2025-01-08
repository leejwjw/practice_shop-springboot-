package com.hello.shopapi.service;

import com.hello.shopapi.domain.Product;
import com.hello.shopapi.domain.ProductImage;
import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.dto.ProductDTO;
import com.hello.shopapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getProductList(PageRequestDTO requestDTO) {

        // 조회할 페이지,사이즈 정보 -> pageable 생성
        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1,
                requestDTO.getSize(),
                Sort.by("pno").descending());
        // 조회
        Page<Object[]> result = productRepository.selectList(pageable);

        // 내용을 꺼내서 List로 변환
        List<ProductDTO> dtoList = result.get().map(arr -> {
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            // Product -> ProductDTO로 변환
            ProductDTO productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();
            // 이미지 이름 꺼내기
            String imgFilename = productImage.getFilename();
            productDTO.setUploadFileNames(List.of(imgFilename));

            return productDTO;
        }).collect(Collectors.toList());

        // 제품 전체 수
        long totalCount = result.getTotalElements();

        // PageResponseDTO로 결과물 묶어서 리턴
        return PageResponseDTO.<ProductDTO>all()
                .list(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(requestDTO)
                .build();
    }

    @Override
    public Long regist(ProductDTO productDTO) {
        Product product = dtoToEntity(productDTO);
        Product saved = productRepository.save(product);
        return saved.getPno();
    }

    @Override
    public ProductDTO getProduct(Long pno) {
        Product product = productRepository.selectOne(pno).orElseThrow();
        ProductDTO productDTO = entityToDto(product);
        return productDTO;
    }

    @Override
    public void modify(ProductDTO productDTO) {
        Product findProduct = productRepository.findById(productDTO.getPno()).orElseThrow();

        findProduct.changePname(productDTO.getPname());
        findProduct.changeDesc(productDTO.getPdesc());
        findProduct.changePrice(productDTO.getPrice());

        findProduct.clearImageList();
        List<String> uploadFileNames = productDTO.getUploadFileNames(); // 최종 저장될 이미지 파일명들
        if(uploadFileNames != null && !uploadFileNames.isEmpty()){
            uploadFileNames.stream().forEach(name -> {
                findProduct.addImageByFilename(name);
            });
        }
        productRepository.save(findProduct);
    }

    @Override
    public void remove(Long pno) {
        // TODO 이미지 테이블 레코드도 삭제
        productRepository.updateToDelete(pno, true);
    }

    // Product Entity -> ProductDTO 변환
    private ProductDTO entityToDto(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .build();
        List<ProductImage> imageList = product.getImageList();
        if(imageList == null || imageList.isEmpty()) { // 이미지 없으면 바로 리턴
            return productDTO;
        }
        // 이미지가 있으면 체워서 리턴
        List<String> fileNameList = imageList.stream()
                .map(img -> img.getFilename()).collect(Collectors.toList());
        productDTO.setUploadFileNames(fileNameList);
        return productDTO;
    }

    // ProductDTO -> Product 변환
    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .build();

        // 업로드 끝난 저장파일들의 이름 리스트
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        if(uploadFileNames == null) {
            return product;
        }
        uploadFileNames.stream().forEach(name -> {
            product.addImageByFilename(name);
        });
        return product;
    }



}
