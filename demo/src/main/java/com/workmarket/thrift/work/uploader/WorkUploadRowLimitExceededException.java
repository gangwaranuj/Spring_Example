package com.workmarket.thrift.work.uploader;

public class WorkUploadRowLimitExceededException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkUploadRowLimitExceededException() {}

	public WorkUploadRowLimitExceededException(String why) {
		super(why);
	}

	public String getWhy() {
		return super.getMessage();
	}
}