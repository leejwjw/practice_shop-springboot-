package com.hello.shopapi.controller;

import com.hello.shopapi.util.CustomJWTException;
import com.hello.shopapi.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@Slf4j
public class APIRefreshController {

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        log.info("*********** APIRefreshController - refresh - authHeader : {}", authHeader);
        log.info("*********** APIRefreshController - refresh - refreshToken : {}", refreshToken);

        // Refresh Token이 없는 경우 -> 예외 처리
        if(refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH_TOKEN");
        }
        // 헤더값이 없거나 맞지 않을 경우 -> 예외 처리
        if(authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_TOKEN");
        }

        String accessToken = authHeader.substring(7);

        // Access Token 검증 -> 만료 X -> 기존 것 그대로 전달
        if(!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Access Token 만료 상황
        // Refresh Token 검증 -> 만료 : 로그인 필요 -> 예외발생, 만료 X : 기존 것 전달
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("*********** APIRefreshController - refresh - claims : {}", claims);
        // 새 토큰 생성해 전달
        String newAccessToken = JWTUtil.generateToken(claims, 10);
        // refreshToken 남은 시간 체크 -> 새로 만들지 결정
        String newRefreshToken = checkRemainTime((Integer) claims.get("exp")) ?
                JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    private boolean checkRemainTime(Integer exp) {
        Date expDate = new Date((long)exp * 1000);
        long diff = expDate.getTime() - System.currentTimeMillis();
        long leftMin = diff / (1000 * 60);
        return leftMin < 60; // 1시간도 안남았으면 true 리턴
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException e) {
            if(e.getMessage().equals("Expired")) {  // 만료되었으면 true 리턴
                return true;
            }
        }
        return false;
    }


}
