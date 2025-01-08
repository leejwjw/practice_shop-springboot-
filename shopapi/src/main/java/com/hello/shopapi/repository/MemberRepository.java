package com.hello.shopapi.repository;

import com.hello.shopapi.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    @EntityGraph(attributePaths = {"roleList"})
    @Query("select m from Member m where m.email = :email")
    Member getMemberWithRoles(@Param("email") String email);
}
