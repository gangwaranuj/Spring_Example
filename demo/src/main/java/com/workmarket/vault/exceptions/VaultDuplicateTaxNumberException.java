package com.workmarket.vault.exceptions;

public class VaultDuplicateTaxNumberException extends RuntimeException {
    public VaultDuplicateTaxNumberException(String message) {
        super(message);
    }

    public VaultDuplicateTaxNumberException(String message, Throwable t) {
        super(message ,t);
    }
}
