package com.workmarket.api.exceptions;

public class MessageSourceApiException extends ApiException {

    private Object[] arguments;

    public MessageSourceApiException(String message) {
        super(message);
    }

    public MessageSourceApiException(String message,
                                     Object[] arguments) {
        super(message);
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
