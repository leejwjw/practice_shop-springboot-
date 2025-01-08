package com.hello.shopapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "roleList")
public class Member {
    @Id
    private String email;  // PK 는 id 번호 대신 email로 처리
    private String password;
    private String nickname;
    private boolean social;  // 소셜회원 여부
    // 활동여부, 등록일, 수정일..

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @Enumerated(EnumType.STRING) // enum 타입은 반드시 enum 이름으로 저장되도록 추가!
    private List<Role> roleList = new ArrayList<>(); // 권한

    // 수정 메서드
    public void addRole(Role role) {
        roleList.add(role);
    }
    public void clearRoles() {
        roleList.clear();
    }
    public void changePassword(String password) {
        this.password = password;
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void changeSocial(boolean social) {
        this.social = social;
    }
}
