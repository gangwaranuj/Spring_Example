package com.workmarket.thrift.search.cart;

public class UserNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	private String userNumber;

	public UserNotFoundException() {
	}

	public UserNotFoundException(String message, String userNumber) {
		super(message);
		this.userNumber = userNumber;
	}

	public String getUserNumber() {
		return userNumber;
	}
}