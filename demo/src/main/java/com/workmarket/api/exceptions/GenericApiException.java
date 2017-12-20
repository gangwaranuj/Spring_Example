package com.workmarket.api.exceptions;

import java.util.ArrayList;
import java.util.List;

public class GenericApiException extends ApiException {
    private final List<String> errors;

    public GenericApiException(final String message) {
        this(message, new ArrayList<String>());
    }

    public GenericApiException(final String message, final List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
