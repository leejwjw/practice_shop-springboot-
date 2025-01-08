package com.hello.shopapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList") // 연관관계 매핑 제외한 toString (로그 보기 편하게 잠시 추가)
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private boolean delFlag;

    @ElementCollection // 테이블 별로도 생성, lazy loading
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    // 값 수정 메서드 (setter X)
    public void changePrice(int price) {
        this.price = price;
    }
    public void changeDesc(String desc) {
        this.pdesc = desc;
    }
    public void changePname(String pname) {
        this.pname = pname;
    }
    // ProductImage 타입으로 이미지 추가
    public void addImage(ProductImage image) {
        image.setOrd(this.imageList.size()); // ord 값 마지막 번호
        this.imageList.add(image);
    }
    // 파일명 문자열로 이미지 파일 추가
    public void addImageByFilename(String filename) {
        ProductImage productImage = ProductImage.builder()
                .filename(filename)
                .build();
        addImage(productImage);
    }
    // 이미지 삭제
    public void clearImageList() {
        this.imageList.clear();
    }

    // 상품 삭제 효과 -> delFlag
    public void changeDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }


}
