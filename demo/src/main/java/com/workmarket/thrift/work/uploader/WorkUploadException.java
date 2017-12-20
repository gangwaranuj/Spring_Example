package com.workmarket.thrift.work.uploader;

public class WorkUploadException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkUploadException() {
	}

	public WorkUploadException(String why, Throwable cause) {
		super(why, cause);
	}

	public String getWhy() {
		return super.getMessage();
	}
}