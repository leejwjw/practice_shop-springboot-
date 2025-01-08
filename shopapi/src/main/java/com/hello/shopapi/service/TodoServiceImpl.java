package com.hello.shopapi.service;

import com.hello.shopapi.dto.PageRequestDTO;
import com.hello.shopapi.dto.PageResponseDTO;
import com.hello.shopapi.domain.Todo;
import com.hello.shopapi.dto.TodoDTO;
import com.hello.shopapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;

    @Override
    public Long add(TodoDTO todoDTO) {
        Todo saved = todoRepository.save(todoDTO.toEntity());
        return saved.getTno();
    }

    @Override
    public TodoDTO get(Long tno) {
        Todo todo = todoRepository.findById(tno).orElseThrow();
        return new TodoDTO(todo);
    }

    @Override
    public void modify(TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(todoDTO.getTno()).orElseThrow();
        todo.changeTitle(todoDTO.getTitle());
        todo.changeCompleted(todoDTO.isCompleted());
        todo.changeDueDate(todoDTO.getDueDate()); // Dirty Checking으로 수정 처리
    }

    @Override
    public void remove(Long tno) {
        Todo todo = todoRepository.findById(tno).orElse(null);
        if(todo != null) {
            todoRepository.delete(todo);
        }else{
            log.info("todo 삭제 실패....");
        }
    }

    @Override
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
         Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                 pageRequestDTO.getSize(), Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);
        // List<투두> -> List<TodoDTO>
        List<TodoDTO> list = result.getContent().stream()
                .map(todo -> new TodoDTO(todo))
                .collect(Collectors.toList());
        // totalCount
        long totalCount = result.getTotalElements();

        // builder() 에 all이라고 이름 붙혀서, builder()대신 all() 호출, 제네릭은 all앞에 부착
        PageResponseDTO<TodoDTO> responseDTO = PageResponseDTO.<TodoDTO>all()
                .list(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();

        return responseDTO;
    }


}
