package com.example.mvc_with_jpa_1.util.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// https://cchoimin.tistory.com/entry/Valid-%EC%99%80-ControllerAdvice%EB%A1%9C-DTO-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
// https://mangkyu.tistory.com/205
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        log.warn("MethodArgumentNotValidException 발생!!! url:{}, trace:{}", request.getRequestURI(), e.getStackTrace());
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .code("400")
                .message("message")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}
