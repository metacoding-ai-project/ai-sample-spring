package com.example.demo._core.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo._core.handler.ex.Exception400;
import com.example.demo._core.handler.ex.Exception404;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 에러 처리: 경고창(alert) 띄우고 이전 페이지로 이동
    @ResponseBody
    @ExceptionHandler(Exception404.class)
    public String handleException404(Exception404 e) {
        var body = """
                <script>
                    alert("%s");
                    history.back();
                </script>
                """.formatted(e.getMessage());
        return body;
    }

    // 400 에러 처리: 경고창(alert) 띄우고 이전 페이지로 이동
    @ResponseBody
    @ExceptionHandler(Exception400.class)
    public String handleException400(Exception400 e) {
        var body = """
                <script>
                    alert("%s");
                    history.back();
                </script>
                """.formatted(e.getMessage());
        return body;
    }

    // 일반 예외 처리: 경고창 띄우고 이전 페이지로 이동
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        var body = """
                <script>
                    alert("%s");
                    history.back();
                </script>
                """.formatted(e.getMessage());
        return body;
    }
}
