package com.hello.shopapi.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// 편의를 위해 객체 생성없이 바로 사용가능하게 처리
@Slf4j
public class JWTUtil {

    private static String key = "1234567890123456789012345678901234567890"; // 이것도 암호화 된것이 더 좋다
    //private static String key = "aGVsbG9Kd3RTZWNyZXRLZXloZWxsb0p3dFNlY3JldEtleWhlbGxvSnd0U2VjcmV0S2V5aGVsbG9Kd3RTZWNyZXRLZXkK";

    // 토큰 생성 메서드 : 토큰에 저장할 정보(payload)와 토큰 유효시간(분) 받아 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int min) {

        SecretKey secretKey = null;
        try {
            secretKey = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            log.info("secretKey : {}", secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // JWT 토큰 생성 : builder() 사용
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(secretKey)
                .compact();
        log.info("jwt : {}", jwtStr);
        return jwtStr;
    }

    // 토큰 검증 메서드
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;
        SecretKey secretKey = null;
        try {
            secretKey = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            claim = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token) // jwt 파싱 및 검증 -> 실패시 에러발생
                    .getBody(); // 토큰에 저장된 claims 꺼내기
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("Malformed"); // 잘못된 형식의 토큰
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired"); // 만료된 토큰
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid"); // 유효하지 않은 Claim
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError"); // 그 외 JWT관련 예외
        } catch (Exception e) {
            throw new CustomJWTException("Error"); //  나머지 예외
        }

        return claim;
    }


}
