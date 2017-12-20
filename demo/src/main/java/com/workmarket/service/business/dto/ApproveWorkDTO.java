package com.workmarket.service.business.dto;

public class ApproveWorkDTO {

	private String flowUuid;
	private String decisionUuid;
	private String deciderUuid;

	public String getFlowUuid() {
		return flowUuid;
	}

	public void setFlowUuid(final String flowUuid) {
		this.flowUuid = flowUuid;
	}

	public String getDecisionUuid() {
		return decisionUuid;
	}

	public void setDecisionUuid(final String decisionUuid) {
		this.decisionUuid = decisionUuid;
	}

	public String getDeciderUuid() {
		return deciderUuid;
	}

	public void setDeciderUuid(final String deciderUuid) {
		this.deciderUuid = deciderUuid;
	}
}
