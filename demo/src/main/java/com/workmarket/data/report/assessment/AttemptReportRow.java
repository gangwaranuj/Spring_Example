package com.workmarket.data.report.assessment;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.google.common.collect.Lists;
import com.workmarket.dto.AbstractCustomUserEntity;

public class AttemptReportRow extends AbstractCustomUserEntity {
	private Long attemptId;
	private Calendar completedOn;
	private Boolean passedFlag;
	private BigDecimal score;
	private List<AttemptResponseReportRow> responses = Lists.newArrayList();
	private Long workId;
	private String workNumber;
	private String workTitle;
	private String status;

	public Long getAttemptId() {
		return attemptId;
	}
	public void setAttemptId(Long attemptId) {
		this.attemptId = attemptId;
	}
	public Calendar getCompletedOn() {
		return completedOn;
	}
	public void setCompletedOn(Calendar completedOn) {
		this.completedOn = completedOn;
	}
	public Boolean getPassedFlag() {
		return passedFlag;
	}
	public void setPassedFlag(Boolean passedFlag) {
		this.passedFlag = passedFlag;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	public List<AttemptResponseReportRow> getResponses() {
		return responses;
	}
	public void setResponses(List<AttemptResponseReportRow> responses) {
		this.responses = responses;
	}
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	public String getWorkNumber() {
		return workNumber;
	}
	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}
	public String getWorkTitle() {
		return workTitle;
	}
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
