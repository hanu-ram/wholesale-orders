package com.levi.wholesale.lambda.common.exception;

public class GdoExtractorException extends RuntimeException {

    public GdoExtractorException(String message) {
        super(message);
    }

    public GdoExtractorException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
