package com.levi.wholesale.exceptions;

public class ValidateInputException extends RuntimeException {
    public ValidateInputException(String errorMessage) {
        super(errorMessage);
    }
}
