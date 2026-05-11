package com.himanshu.kumar.LaughApi.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice     // -> to apply logic to multiple controller
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    private static final String NOT_READABLE_REQUEST_ERROR_MESSAGE = "The request is malformed. Ensure the JSON structure is correct.";


    @ResponseBody
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponseDto<String>> responseStatusExceptionHandler(final ResponseStatusException exception) {
        logException(exception);
        final var exceptionResponse = new ExceptionResponseDto<>();
        exceptionResponse.setStatus(exception.getStatusCode().toString);
        exceptionResponse.setDescription(exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(exceptionResponse);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> serverExceptionHandler(final Exception exception) {
        logException(exception);
        final var exceptionResponse = new ExceptionResponseDto<>();
        exceptionResponse.setStatus(HttpStatus.NOT_IMPLEMENTED.toString());
        exceptionResponse.setDescription("Something went wrong");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(exceptionResponse);
    }


    private void logException(final @NonNull Exception exception) {
        log.error("Exception encountered: {}", exception.getMessage(), exception);
    }
}
