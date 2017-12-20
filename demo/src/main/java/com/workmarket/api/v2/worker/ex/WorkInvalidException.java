package com.workmarket.api.v2.worker.ex;

import com.workmarket.api.exceptions.MessageSourceApiException;

public class WorkInvalidException extends MessageSourceApiException {

    public WorkInvalidException() {
        super("api.v1.assignments.invalid.workNumber");
    }
}
