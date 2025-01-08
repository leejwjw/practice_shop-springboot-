package com.hello.shopapi.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String filename;
    @Setter
    private int ord;  // 이미지 마다 번호 지정, 대표이지미 = 0

    //private String orgFilename;
    //private String fileExt;
    //private String fileType;

}
