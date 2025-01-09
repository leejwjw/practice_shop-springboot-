package com.hello.shopapi.service;

import com.hello.shopapi.domain.Member;
import com.hello.shopapi.domain.Role;
import com.hello.shopapi.dto.MemberDTO;
import com.hello.shopapi.dto.MemberModifyDTO;
import com.hello.shopapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {

        String email = getEmailFromKakaoAccessToken(accessToken);
        log.info("********************** getKakaoMember - email : {}", email);

        Optional<Member> findMember = memberRepository.findById(email);
        // 기존 회원이다 -> DB에서 찾은 Member를 MemberDTO로 변환해 리턴
        if(findMember.isPresent()) {
            MemberDTO memberDTO = entityToDTO(findMember.get());
            return memberDTO;
        }
        // 기존 회원이 아니다 -> 임시비번과 임시 닉네임으로 Member 엔티티 생성해 DB에 저장 & DTO리턴
        Member socialMember = makeSocialMember(email);
        memberRepository.save(socialMember);
        MemberDTO socialMemberDTO = entityToDTO(socialMember);

        return socialMemberDTO;
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Member member = memberRepository.findById(memberModifyDTO.getEmail()).orElseThrow();
        member.changePassword(passwordEncoder.encode(memberModifyDTO.getPassword()));
        member.changeSocial(false); // 이후 로그인시 일반회원처럼 로그인 처리
        member.changeNickname(memberModifyDTO.getNickname());
        memberRepository.save(member);
    }

    // 카카오에 사용자 정보 요청
    private String getEmailFromKakaoAccessToken(String accessToken) {
        // 카카오 사용자 정보 요청 URL
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if(accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        // 카카오 서버에 HTTP 요청
        RestTemplate restTemplate = new RestTemplate();
        // 헤더정보 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 헤더 정보 포함해 HttpEntity 객체 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 경로 생성해주는 클래스 이용
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();
        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);
        log.info("********** MemberService - response : {}", response);
        LinkedHashMap<String, LinkedHashMap> responseBody = response.getBody();
        log.info("********** MemberService - responseBody : {}", responseBody);
        // 응답 내용중 카카오 계정 정보 꺼낼 수 있다.
        LinkedHashMap<String, String> kakaoAccount = responseBody.get("kakao_account");
        log.info("********** MemberService - kakaoAccount : {}", kakaoAccount);
        return kakaoAccount.get("email"); // 이메일만 꺼내서 리턴
    }

    // 임시 비밀번호 생성해주는 메서드
    private String makeTempPassword() {
        // 문자열 누적추가(수정)을 위해 String 대신 StringBuffer 사용
        StringBuffer stringBuffer = new StringBuffer();
        // ascii 이용 -> 알파벳(65) 랜덤으로 10글자 암호 생성
        for(int i = 0; i < 10; i++) {
            stringBuffer.append((char)((int)(Math.random()*55) + 65));
        }
        return stringBuffer.toString(); // 문자열로 리턴
    }

    // 이메일 존재하지 않을 경우, Member엔티티 생성해주는 메서드
    private Member makeSocialMember(String email) {
        String tempPassword = makeTempPassword(); // 임시비밀번호 생성
        log.info("tempPassword : {}", tempPassword);
        String nickname = "Social Member"; // 임의의 닉네임 (또는 카카오에서 제공하는것 사용)
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .social(true)
                .build();
        member.addRole(Role.USER);
        return member;
    }

}
