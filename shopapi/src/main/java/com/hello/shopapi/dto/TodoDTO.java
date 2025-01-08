package com.hello.shopapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hello.shopapi.domain.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {
    private Long tno;
    private String title;
    private String writer;
    private boolean completed;
    // 날짜를 화면에서 쉽게 처리하기 위해, JsonFormat 이용하여 패턴 지정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    // TodoEntity -> TodoDTO
    public TodoDTO(Todo todo) {
        this.tno = todo.getTno();
        this.title = todo.getTitle();
        this.writer = todo.getWriter();
        this.completed = todo.isCompleted();
        this.dueDate = todo.getDueDate();
    }
    // TodoDTO -> TodoEntity
    public Todo toEntity() {
        // TodoEntity 생성
        Todo todo = Todo.builder()
                .tno(this.tno) // DTO 변수에 있는 값을 모두 엔티티에 넣기
                .title(this.title)
                .writer(this.writer)
                .completed(this.completed)
                .dueDate(this.dueDate)
                .build();
        return todo; // 엔티티 리턴
    }

}
