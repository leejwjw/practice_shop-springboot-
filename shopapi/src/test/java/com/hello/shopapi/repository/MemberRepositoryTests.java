package com.hello.shopapi.repository;

import com.hello.shopapi.domain.Member;
import com.hello.shopapi.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsert() {
        for(int i = 0; i < 10; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@test.com")
                    .password(passwordEncoder.encode("1111")) // 암호화
                    .nickname("User" + i)
                    .build();
            member.addRole(Role.USER);
            if(i >= 5) {
                member.addRole(Role.MANAGER);
            }
            if(i >= 8) {
                member.addRole(Role.ADMIN);
            }
            // user0 ~ 9 = USER / user5 ~ 9 +MANAGER / user8 ~ 9 +AMIN
            memberRepository.save(member);
        }
    }

    @Test
    public void testFindByEmail() {
        String email = "user9@test.com";
        Member findMember = memberRepository.getMemberWithRoles(email);
        log.info("********************* findMember : {}", findMember);
    }




}