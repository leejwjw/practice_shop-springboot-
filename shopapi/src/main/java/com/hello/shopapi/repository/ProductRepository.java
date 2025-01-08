package com.hello.shopapi.repository;

import com.hello.shopapi.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품 조회 Join
    @EntityGraph(attributePaths = {"imageList"})
    @Query("select p from Product p where p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);

    // 상품 삭제 효과
    // @Query : INSERT, UPDATE, DELETE 쿼리 사용시, @Modifying 부착
    //  -> DirtyChecking X -> 1차 캐시 무시하고 바로 처리
    @Modifying(clearAutomatically = true) // 영속화 컨텍스트 초기화
    @Query("update Product p set p.delFlag = :delFlag where p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("delFlag") boolean delFlag);

    // 상품 목록 조회 : left 조인
    @Query("select p, pi from Product p left join p.imageList pi where pi.ord = 0 and p.delFlag = false")
    Page<Object[]> selectList(Pageable pageable);



}
