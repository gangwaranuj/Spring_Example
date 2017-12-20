package com.workmarket.domains.model;

import java.util.Calendar;

public class UserLaneRelationship {

	private static final long serialVersionUID = 1L;

	private Long companyId;
	private String companyName;
	private Integer laneType;
	private Calendar lastAssignmentDate;
	private Long lastAssignmentId;
	private Integer totalAssignments;
	private Integer approvalStatus;
	private Long paymentTermsId;
	private Integer paymentTermsDays;
	private Long pendingPaymentTermsId;
	private Integer pendingPaymentTermsDays;
	

	public Long getPendingPaymentTermsId() {
		return pendingPaymentTermsId;
	}

	public void setPendingPaymentTermsId(Long pendingPaymentTermsId) {
		this.pendingPaymentTermsId = pendingPaymentTermsId;
	}

	public Integer getPendingPaymentTermsDays() {
		return pendingPaymentTermsDays;
	}

	public void setPendingPaymentTermsDays(Integer pendingPaymentTermsDays) {
		this.pendingPaymentTermsDays = pendingPaymentTermsDays;
	}

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

	public Integer getLaneType() {
		return laneType;
	}

	public void setLaneType(Integer laneType) {
		this.laneType = laneType;
	}

	public Calendar getLastAssignmentDate() {
		return lastAssignmentDate;
	}

	public void setLastAssignmentDate(Calendar lastAssignmentDate) {
		this.lastAssignmentDate = lastAssignmentDate;
	}

	public Long getLastAssignmentId() {
		return lastAssignmentId;
	}

	public void setLastAssignmentId(Long lastAssignmentId) {
		this.lastAssignmentId = lastAssignmentId;
	}

	public Integer getTotalAssignments() {
		return totalAssignments;
	}

	public void setTotalAssignments(Integer totalAssignments) {
		this.totalAssignments = totalAssignments;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

    public Integer getPaymentTermsDays()
    {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays)
    {
        this.paymentTermsDays = paymentTermsDays;
    }

	public void setPaymentTermsId(Long paymentTermsId) {
		this.paymentTermsId = paymentTermsId;
	}

	public Long getPaymentTermsId() {
		return paymentTermsId;
	}
}
