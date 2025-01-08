package com.hello.shopapi.service;

import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.dto.TodoDTO;

public interface TodoService {
    // 등록 (TodoDTO로 데이터받아 저장, 등록한 tno 번호 리턴)
    Long add(TodoDTO todoDTO);
    // 조회
    TodoDTO get(Long tno);
    // 수정
    void modify(TodoDTO todoDTO);
    // 삭제
    void remove(Long tno);
    // 목록 조회
    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
