package zerobase.weather.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.weather.error.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllException(Exception e) {
        log.error("error from GlobalExceptionHandler");
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부에 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ErrorResponse handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        return new ErrorResponse("HttpMediaTypeNotAcceptableException", "올바른 형식으로 입력해주세요.");
    }
}
