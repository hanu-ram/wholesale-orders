package com.levi.wholesale.exceptions;

import org.springframework.util.StringUtils;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Class exceptionClass, String id, String exceptionMessage) {
        super(StringUtils.capitalize(exceptionClass.getSimpleName()) + " already exists in db for parameter " + id + " : " + exceptionMessage);
    }
}
