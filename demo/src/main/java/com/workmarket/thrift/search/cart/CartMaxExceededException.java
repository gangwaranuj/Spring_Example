package com.workmarket.thrift.search.cart;

public class CartMaxExceededException extends Exception {
	private static final long serialVersionUID = 1L;

	private int numExceeded;

	public CartMaxExceededException() {
	}

	public CartMaxExceededException(String message, Throwable cause, int numExceeded) {
		super(message, cause);
		this.numExceeded = numExceeded;
	}

	public int getNumExceeded() {
		return numExceeded;
	}
}