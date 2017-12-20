package com.workmarket.service.business.dto;

import java.math.BigDecimal;
 

public class ProjectDTO {

	private Long projectId;
	private Long ownerId;
	private String name;
	private String description;
	private String startDate;
	private String dueDate;
	private Long clientCompanyId;
	private BigDecimal expectedRevenue;
	private BigDecimal targetMargin; 
	private BigDecimal anticipatedCost;
	private String code;
	private BigDecimal reservedFunds;
	private boolean reservedFundsEnabled;
	private boolean budgetEnabledFlag;
	private BigDecimal budget;
	private BigDecimal remainingBudget;


	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDueDateString() {
		return dueDate;
	}
	
	public void setDueDateString(String dueDate) {
		this.dueDate = dueDate;
	}

    public Long getClientCompanyId()
    {
        return clientCompanyId;
    }

    public void setClientCompanyId(Long clientCompanyId)
    {
        this.clientCompanyId = clientCompanyId;
    }

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getExpectedRevenue() {
		return expectedRevenue;
	}

	public void setExpectedRevenue(BigDecimal expectedRevenue) {
		this.expectedRevenue = expectedRevenue;
	}

	public BigDecimal getTargetMargin() {
		return targetMargin;
	}

	public void setTargetMargin(BigDecimal targetMargin) {
		this.targetMargin = targetMargin;
	}

	public BigDecimal getAnticipatedCost() {
		return anticipatedCost;
	}

	public void setAnticipatedCost(BigDecimal anticipatedCost) {
		this.anticipatedCost = anticipatedCost;
	}

	public String getStartDateString() {
		return startDate;
	}
	
	public void setStartDateString(String startDate) {
		this.startDate = startDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getReservedFunds() {
		return reservedFunds;
	}

	public void setReservedFunds(BigDecimal reservedFunds) {
		this.reservedFunds = reservedFunds;
	}

	public boolean isReservedFundsEnabled() {
		return reservedFundsEnabled;
	}

	public void setReservedFundsEnabled(boolean reservedFundsEnabled) {
		this.reservedFundsEnabled = reservedFundsEnabled;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	public BigDecimal getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(BigDecimal remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	public boolean getBudgetEnabledFlag() {
		return budgetEnabledFlag;
	}

	public void setBudgetEnabledFlag(boolean budgetEnabledFlag) {
		this.budgetEnabledFlag = budgetEnabledFlag;
	}


}
