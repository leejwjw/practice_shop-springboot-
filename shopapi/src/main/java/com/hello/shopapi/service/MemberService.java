package com.hello.shopapi.service;

import com.hello.shopapi.domain.Member;
import com.hello.shopapi.dto.MemberDTO;
import com.hello.shopapi.dto.MemberModifyDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    // Member 엔티티 -> MemberDTO 변환 default 메서드
    default MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.isSocial(),
                member.getRoleList().stream()
                        .map(role -> role.name())
                        .collect(Collectors.toList())
        );
        return memberDTO;
    }

    void modifyMember(MemberModifyDTO memberModifyDTO);
}
