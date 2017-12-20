package com.workmarket.service.exception.file;

import com.workmarket.service.exception.HostServiceException;

public class AmazonS3ServiceException extends HostServiceException {
	private static final long serialVersionUID = 1L;
	public AmazonS3ServiceException() {
		super();
	}
	public AmazonS3ServiceException(String message) {
		super(message);
	}
	public AmazonS3ServiceException(Throwable e) {
		super(e);
	}
}
