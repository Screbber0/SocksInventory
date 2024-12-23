package ru.backspark.task.exception;

public class SockShortageException extends RuntimeException {
    public SockShortageException(String message) {
        super(message);
    }
}
