package com.hello.shopapi.security.handler;

import com.google.gson.Gson;
import com.hello.shopapi.dto.MemberDTO;
import com.hello.shopapi.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        log.info("******************************** authentication - {}", authentication);

        // 리액트에 응답해줄 응답 데이터 생성
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
        Map<String, Object> claims = memberDTO.getClaims(); // 사용자정보 Map으로 만든것 리턴

        // JWTUtil 이용해 AccessToken, RefreshToken 생성 -> claims 에 추가
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);
        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims); // 자바 Map 객체 -> JSON(JSON.stringify) 문자열로 변환

        response.setContentType("application/json; charset=utf-8"); // 응답 헤더 정보 추가
        PrintWriter writer = response.getWriter();
        writer.println(jsonStr); // 데이터 응답하기
        writer.close();
    }
}
