package ru.backspark.task.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.backspark.task.exception.CsvProcessingException;
import ru.backspark.task.exception.SockShortageException;
import ru.backspark.task.exception.SocksNotFoundException;

@RestControllerAdvice
public class RestControllerErrorHandler {

    @ExceptionHandler(SockShortageException.class)
    public ResponseEntity<String> handleSockShortageException(SockShortageException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(SocksNotFoundException.class)
    public ResponseEntity<String> handleSocksNotFoundException(SocksNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CsvProcessingException.class)
    public ResponseEntity<String> handleCsvProcessingException(CsvProcessingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки CSV файла: " + ex.getMessage());
    }
}

