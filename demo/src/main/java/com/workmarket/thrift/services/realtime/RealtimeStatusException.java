package com.workmarket.thrift.services.realtime;

import java.util.Collections;
import java.util.List;

public class RealtimeStatusException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<RealtimeError> errors = Collections.emptyList();

	public RealtimeStatusException() {
	}

	public RealtimeStatusException(String why) {
		super(why);
	}

	public RealtimeStatusException(Throwable cause) {
		super(cause);
	}

	public String getWhy() {
		return super.getMessage();
	}

	public List<RealtimeError> getErrors() {
		return errors;
	}
}