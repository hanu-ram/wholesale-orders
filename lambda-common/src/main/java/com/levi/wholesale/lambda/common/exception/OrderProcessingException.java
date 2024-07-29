package com.levi.wholesale.lambda.common.exception;

public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
