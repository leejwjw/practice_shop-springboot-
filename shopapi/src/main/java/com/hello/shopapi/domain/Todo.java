package com.hello.shopapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@ToString // 개발시 편의를 위해 추가
@Builder
@AllArgsConstructor  // Builder 패턴 이용시 필요
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 엔티티는 기본생성자 필수
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql auto_increment
    private Long tno;           // todolist 고유번호 : 값 DB에서 자동부여
    @Column(nullable = false)
    private String title;       // 할일 : N/N
    private String writer;      // 작성자
    private boolean completed;  // 할일 완료 여부
    private LocalDate dueDate;  // 언제까지

    // 수정 가능한 필드 수정 메서드 추가 (클래스에 Setter 오픈하지 말자)
    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeCompleted(boolean completed){
        this.completed = completed;
    }
    public void changeDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
