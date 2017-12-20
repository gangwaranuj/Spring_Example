package com.workmarket.service.exception.work;

public class WorkNotFoundException extends Exception {

	private static final long serialVersionUID = 7896862418664244627L;
	private Long workId;
	private String workNumber;

	public WorkNotFoundException(String workNumber) {
		super();
		this.workNumber = workNumber;
	}

	public WorkNotFoundException(Long workId) {
		super();
		this.workId = workId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkNotFoundException [workId=");
		builder.append(workId);
		builder.append(", workNumber=");
		builder.append(workNumber);
		builder.append("]");
		return builder.toString();
	}

}
