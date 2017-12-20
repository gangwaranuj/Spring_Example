package com.workmarket.thrift.work.uploader;

public class WorkUploadDuplicateMappingGroupNameException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkUploadDuplicateMappingGroupNameException() {
	}

	public WorkUploadDuplicateMappingGroupNameException(String why) {
		super(why);
	}

	public String getWhy() {
		return super.getMessage();
	}
}