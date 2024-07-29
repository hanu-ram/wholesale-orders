package com.levi.wholesale.lambda.common.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
