package com.workmarket.api.v2.worker.ex;

import com.workmarket.api.exceptions.MessageSourceApiException;

public class WorkNotAuthorizedException extends MessageSourceApiException {

    public WorkNotAuthorizedException() {
		super("assignment.mobile.notallowed");
    }
}
