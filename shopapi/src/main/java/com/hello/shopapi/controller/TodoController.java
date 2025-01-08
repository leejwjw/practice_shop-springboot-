package com.hello.shopapi.controller;

import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.dto.TodoDTO;
import com.hello.shopapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    // 1개 조회
    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable("tno") Long tno){
        log.info("/todo/get - tno : {}", tno);
        return todoService.get(tno);
    }

    // 목록 처리 : .../list?page=1&size=10
    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("/todo/list - pageRequestDTO : {}", pageRequestDTO);
        return todoService.list(pageRequestDTO);
    }

    // 등록
    @PostMapping("/")
    public Map<String, Long> add(@RequestBody TodoDTO todoDTO){
        log.info("/todo/add - todoDTO : {}", todoDTO);
        Long tno = todoService.add(todoDTO);
        return Map.of("TNO", tno);
    }

    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable("tno") Long tno, @RequestBody TodoDTO todoDTO) {
        todoDTO.setTno(tno);
        log.info("/todo/modify - todoDTO : {}", todoDTO);
        todoService.modify(todoDTO);
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{tno}")
    public Map<String, String> delete(@PathVariable("tno") Long tno) {
        log.info("/todo/delete - tno : {}" , tno);
        todoService.remove(tno);
        return Map.of("RESULT", "SUCCESS");
    }


}
