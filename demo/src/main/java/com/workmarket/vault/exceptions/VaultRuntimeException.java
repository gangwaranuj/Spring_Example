package com.workmarket.vault.exceptions;

public class VaultRuntimeException extends RuntimeException {
	public VaultRuntimeException(String message) {
		super(message);
	}

	public VaultRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
