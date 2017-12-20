package com.workmarket.thrift.work.display;

import java.util.Collections;
import java.util.List;

public class WorkDisplayException extends Exception {
	private static final long serialVersionUID = 1L;

	private WorkDisplayErrorType workDisplayErrorType;
	private List<String> messages = Collections.emptyList();

	public WorkDisplayException() {
	}

	public WorkDisplayException(WorkDisplayErrorType workDisplayErrorType, List<String> messages) {
		this.workDisplayErrorType = workDisplayErrorType;
		this.messages = messages;
	}

	public WorkDisplayErrorType getWorkDisplayErrorType() {
		return workDisplayErrorType;
	}

	public List<String> getMessages() {
		return messages;
	}
}