package com.workmarket.domains.model;

import java.util.Calendar;

import com.workmarket.utility.StringUtilities;

public class TeamWorkflowStep {

	private static final long serialVersionUID = 1L;

	private Long workflowId;
	private String workflowName;
	private Long stepId;
	private String stepName;
	private Integer stepPosition;
	private Boolean beforeWork;
	private String timeUnit;
	private Integer timeValue;
	private Long userId;
	private String firstName;
	private String lastName;
	private Long companyId;
	private Long alertId;
	private Calendar expirationDate;
	private Boolean completed;

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Integer getStepPosition() {
		return stepPosition;
	}

	public void setStepPosition(Integer stepPosition) {
		this.stepPosition = stepPosition;
	}

	public Boolean isBeforeWork() {
		return beforeWork;
	}

	public void setBeforeWork(Boolean beforeWork) {
		this.beforeWork = beforeWork;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Integer getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Integer timeValue) {
		this.timeValue = timeValue;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getAlertId() {
		return alertId;
	}

	public void setAlertId(Long alertId) {
		this.alertId = alertId;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean isCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
	public String getFullName() {
        return StringUtilities.fullName(getFirstName(), getLastName());
    }
}
