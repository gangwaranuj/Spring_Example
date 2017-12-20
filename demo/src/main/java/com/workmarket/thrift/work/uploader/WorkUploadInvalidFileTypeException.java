package com.workmarket.thrift.work.uploader;

public class WorkUploadInvalidFileTypeException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkUploadInvalidFileTypeException() {
	}

	public WorkUploadInvalidFileTypeException(String why) {
		super(why);
	}

	public String getWhy() {
		return super.getMessage();
	}
}