package com.hello.shopapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default   // 빌더 사용 시, 특정 필드 초기값 설정하려면 어노테이션 부착
    private int page = 1; // default 값 세팅
    @Builder.Default
    private int size = 12;
}
