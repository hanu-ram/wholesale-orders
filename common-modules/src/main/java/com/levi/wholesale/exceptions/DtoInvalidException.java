package com.levi.wholesale.exceptions;

import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class DtoInvalidException extends RuntimeException {

    private List<ObjectError> errorList = Collections.emptyList();

    public DtoInvalidException(Class exceptionClass) {
        super(StringUtils.capitalize(exceptionClass.getSimpleName()) + " validation error found while persisting ");
    }

    public void addValidationError(String name, String message) {
        if (isEmpty(errorList)) {
            errorList = new ArrayList<>();
        }
        errorList.add(new ObjectError(name, message));
    }

    public List<ObjectError> getErrorList() {
        return new ArrayList<>(errorList);
    }
}
