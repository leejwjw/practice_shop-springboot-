package com.hello.shopapi.dto;

import lombok.Data;
import lombok.Setter;

@Data
public class ProductFileDTO {
    private String filename;
    private int ord;  // 이미지 마다 번호 지정, 대표이지미 = 0
    private String orgFilename;
    private String fileExt;
    private String fileType;
}
