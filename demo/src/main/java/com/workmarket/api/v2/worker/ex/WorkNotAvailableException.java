package com.workmarket.api.v2.worker.ex;

import com.workmarket.api.exceptions.MessageSourceApiException;

public class WorkNotAvailableException extends MessageSourceApiException {

    public WorkNotAvailableException() {
        super("assignment.mobile.notfound");
    }
}
