package com.workmarket.service.exception.project;

/**
 * Date: 6/18/13
 * Time: 12:37 PM
 */
public class InsufficientBudgetException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	public InsufficientBudgetException(){
	}

	public InsufficientBudgetException(String message) {
		super(message);
	}

}
