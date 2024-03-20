package com.root.controller.advice;

import lombok.extern.slf4j.Slf4j;
import com.root.controller.CollectController;
import com.root.domain.io.CommonVo;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonVo<List<String>> validCheck(MethodArgumentNotValidException e) {

        log.error("ValidException " , e);

        List<String> list = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return CommonVo.<List<String>>builder()
                .payload(list)
                .message("")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonVo<Map<String, String>> validCheck(Exception e) {

        log.error("Exception " , e);

        return CommonVo.<Map<String, String>>builder()
                .payload(new HashMap<>())
                .message(e.getMessage())
                .build();
    }

}
