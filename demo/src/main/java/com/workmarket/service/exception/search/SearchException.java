package com.workmarket.service.exception.search;

import com.workmarket.search.SearchError;

import java.util.Collections;
import java.util.List;

public class SearchException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<SearchError> errors = Collections.emptyList();

	public SearchException() {
	}

	public SearchException(String message) {
		super(message);
	}

	public SearchException(String why, List<SearchError> errors) {
		super(why);
		this.errors = errors;
	}

	public SearchException(String why, Throwable cause) {
		super(why, cause);
	}

	public SearchException(String why, Throwable cause, List<SearchError> errors) {
		this(why, cause);
		this.errors = errors;
	}

	public String getWhy() {
		return super.getMessage();
	}

	public List<SearchError> getErrors() {
		return errors;
	}
}