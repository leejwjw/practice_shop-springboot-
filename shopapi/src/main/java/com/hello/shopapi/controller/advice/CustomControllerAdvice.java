package com.hello.shopapi.controller.advice;

import com.hello.shopapi.util.CustomJWTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

// 예외 발생시 -> Map 타입으로 에러메세지 데이터를 전송
@RestControllerAdvice
public class CustomControllerAdvice {

    // 없는 tno 번호로 조회
    @ExceptionHandler(NoSuchElementException.class) // 예외 처리 메서드 위에 부착
    protected ResponseEntity<?> notExist(NoSuchElementException e){
        String msg = e.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", msg));
    }

    // 매개값 타입 이상
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handlerIllegalArgumentException(MethodArgumentNotValidException e) {
        String msg = "argument not valid error"; // e.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg", msg));
    }

    // JWT 예외 발생 처리
    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {
        String msg = e.getMessage();
        return ResponseEntity.ok().body(Map.of("error", msg));
    }


}
