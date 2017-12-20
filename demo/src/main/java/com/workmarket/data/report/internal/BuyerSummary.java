package com.workmarket.data.report.internal;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 6/12/12
 * Time: 12:02 PM
 */
public class BuyerSummary {

	private Long companyId;
	private String companyName;
	private Integer routedAssignments;
	private Integer createdAssignments;
	private Integer activeAssignments;
	private Integer voidAssignments;
	private Integer cancelledAssignments;
	private Integer closedAssignments;
	private BigDecimal averageAssignmentPrice;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getRoutedAssignments() {
		return routedAssignments;
	}

	public void setRoutedAssignments(Integer routedAssignments) {
		this.routedAssignments = routedAssignments;
	}

	public Integer getCreatedAssignments() {
		return createdAssignments;
	}

	public void setCreatedAssignments(Integer createdAssignments) {
		this.createdAssignments = createdAssignments;
	}

	public Integer getActiveAssignments() {
		return activeAssignments;
	}

	public void setActiveAssignments(Integer activeAssignments) {
		this.activeAssignments = activeAssignments;
	}

	public BigDecimal getAverageAssignmentPrice() {
		return averageAssignmentPrice;
	}

	public void setAverageAssignmentPrice(BigDecimal averageAssignmentPrice) {
		this.averageAssignmentPrice = averageAssignmentPrice;
	}

	public Integer getVoidAssignments() {
		return voidAssignments;
	}

	public void setVoidAssignments(Integer voidAssignments) {
		this.voidAssignments = voidAssignments;
	}

	public Integer getCancelledAssignments() {
		return cancelledAssignments;
	}

	public void setCancelledAssignments(Integer cancelledAssignments) {
		this.cancelledAssignments = cancelledAssignments;
	}

	public Integer getClosedAssignments() {
		return closedAssignments;
	}

	public void setClosedAssignments(Integer closedAssignments) {
		this.closedAssignments = closedAssignments;
	}
}
