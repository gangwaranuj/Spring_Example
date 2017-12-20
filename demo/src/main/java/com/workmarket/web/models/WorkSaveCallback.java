package com.workmarket.web.models;

import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;

public abstract class WorkSaveCallback {
	public void before(WorkSaveRequest request) {}
	public String success(WorkSaveRequest request, WorkResponse response) { return null; }
	public String error() { return null; }
}
