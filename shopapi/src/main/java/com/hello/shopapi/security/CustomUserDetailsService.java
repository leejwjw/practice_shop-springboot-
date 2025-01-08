package com.hello.shopapi.security;

import com.hello.shopapi.domain.Member;
import com.hello.shopapi.dto.MemberDTO;
import com.hello.shopapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("************* CustomUserDetailsService - loadUserByUsername");
        // DB에서 로그인아이디(Member엔티티의 email = 시큐리티에서는 username이라 칭함 = 위 매개변수 Username) 로 회원 정보 조회
        Member member = memberRepository.getMemberWithRoles(username);
        if(member == null) {
            throw new UsernameNotFoundException("Email(username) Not Found");
        }
        // Member -> MemberDTO 변환 (이때 MemberDTO는 SpringSecurity 가 요구하는 UserDetails 타입이다 )
        MemberDTO memberDTO = new MemberDTO(member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.isSocial(),
                member.getRoleList().stream()
                        .map(role -> role.name())
                        .collect(Collectors.toList()));
        log.info("************* CustomUserDetailsService - loadUserByUsername - memberDTO : {}", memberDTO);

        return memberDTO;
    }
}
