package com.levi.wholesale.exceptions;

public class InputValidationException extends RuntimeException {
    public InputValidationException(String errorMessage) {
        super(errorMessage);
    }
}
