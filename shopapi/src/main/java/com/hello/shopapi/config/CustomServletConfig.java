package com.hello.shopapi.config;

import com.hello.shopapi.controller.formatter.LocalDateFormatter;
import com.hello.shopapi.controller.formatter.LocalDateTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {
    // 설정파일에 우리가만든 포매팅 클래스 지정하기
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
        //registry.addFormatter(new LocalDateTimeFormatter());
    }

    /* CORS 설정 -> Spring Security 설정으로 변경
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // CORS 적용할 URL 패턴
                .allowedOrigins("*") // 자원 공유 허락할 Origin 설정 "http://localhost:5173"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .maxAge(300) // pre-flight request를 캐싱해두는 시간
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type");
    }*/
}
