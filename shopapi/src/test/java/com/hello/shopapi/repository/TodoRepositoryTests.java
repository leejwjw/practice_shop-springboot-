package com.hello.shopapi.repository;

import com.hello.shopapi.domain.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
@Rollback(value = false)
class TodoRepositoryTests {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void testInsert() {
        for(int i = 1; i <= 100; i++) {
            Todo todo = Todo.builder()
                    .title("Title - " + i)
                    .dueDate(LocalDate.of(2024, 12, 31))
                    .writer("user01")
                    .build();
            todoRepository.save(todo);
        }
    }

    @Test
    public void testRead() {
        Long tno = 10L;
        Todo findTodo = todoRepository.findById(tno).orElseThrow();
        log.info("findTodo : {}", findTodo);
    }

    @Test
    public void testUpdate() {
        Long tno = 10L;
        Todo findTodo = todoRepository.findById(tno).orElseThrow();

        findTodo.changeTitle("Modified Title....");
        findTodo.changeCompleted(true);
        findTodo.changeDueDate(LocalDate.of(2024,12,23));

        em.flush();
        em.clear();

        Todo modifiedTodo = todoRepository.findById(tno).orElseThrow();
        log.info("modified Todo : {}", modifiedTodo);

    }

    @Test
    public void testDelete() {
        Long tno = 15L;
        Todo findTodo = todoRepository.findById(tno).orElse(null);
        if(findTodo != null) {
            todoRepository.delete(findTodo);
            log.info("삭제 성공!!");
        }else {
            log.info("tno {}번은 없습니다. 삭제 실패....", tno);
        }
    }

    @Test
    public void testPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());
        Page<Todo> result = todoRepository.findAll(pageable);
        log.info("result : {}", result.getTotalElements());
        result.getContent().stream().forEach(todo -> log.info("todo : {}", todo));
    }





}