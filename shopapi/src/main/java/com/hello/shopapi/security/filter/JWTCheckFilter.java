package com.hello.shopapi.security.filter;

import com.google.gson.Gson;
import com.hello.shopapi.dto.MemberDTO;
import com.hello.shopapi.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {
    // 필터 생략할 것 지정하는 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // preflight 제외
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        String requestURI = request.getRequestURI();
        log.info("******** JWTCheckFilter - shouldNotFilter - requestURI: {}", requestURI);

        // /api/member/로 시작하는 경로는 필터체크 제외
        if(requestURI.startsWith("/api/member/")) {
            return true;
        }

        // 이미지 조회 경로 체크 제외
        if(requestURI.startsWith("/api/products/view/")) {
            return true;
        }

        return false;
    }

    // 필터링 메서드 : 오버라이딩 필수
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("*********** JWTCheckFilter - doFilterInternal");

        String authHeaderValue = request.getHeader("Authorization");
        // Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IiQyYSQxMC....
        String accessToken = authHeaderValue.substring(7);

        try {
            Map<String, Object> claims = JWTUtil.validateToken(accessToken); // 예외 발생가능
            log.info("*********** JWTCheckFilter - doFilterInternal cliams : {}", claims);

            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            // AccessToken에 저장된 사용자 정보를 꺼내서 UserDetails타입인 MemberDTO에 정보 담아 생성
            MemberDTO memberDTO = new MemberDTO(email, password, nickname, social, roleNames); //
            log.info("*********** JWTCheckFilter - doFilterInternal memberDTO : {}", memberDTO);

            // 시큐리티 전용 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, password, memberDTO.getAuthorities());
            // 시큐리티 컨텍스트에 토큰 추가 (시큐리티로 로그인한 효과)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


            filterChain.doFilter(request, response); // 다음 필터로 진행~
        }catch (Exception e){
            // 검증 예외 처리 : AccessToken 검증하다 예외발생하면 JSON으로 에레메세지 전송
            log.info("*********** JWT Check Error!! ");
            log.info(e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println(msg);
            writer.close();
        }
    }
}
