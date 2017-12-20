package com.workmarket.thrift.work.display;

import java.util.Collections;
import java.util.List;

public class SqlTooComplexException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<String> messages = Collections.emptyList();

	public SqlTooComplexException() {
	}

	public List<String> getMessages() {
		return messages;
	}
}