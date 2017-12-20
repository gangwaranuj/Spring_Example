package com.workmarket.data.report.work;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.utility.StringUtilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;

public class WorkReportRow extends StandardDecoratedWorkReportRow {

	private static final long serialVersionUID = -7757574577306993292L;

	private String endDate;
	private Boolean scheduleRangeFlag;
	private String address1;
	private String address2;
	private String status;

	private BigDecimal workMarketFee;
	private BigDecimal workPrice;
	private BigDecimal workTotalCost;
	private BigDecimal workFeePercentage;
	private BigDecimal workOverridePrice;
	private Double hoursWorked;
	private Double hoursBudgeted;

	private Calendar assignedResourceAppointmentDate;
	private Calendar completedOn;
	private Calendar closedOn;
	private Calendar sentOn;
	private boolean owner;
	private String invoiceSummaryNumber;
	private BigDecimal pendingApprovalCost;
	private boolean taxCollected;
	private BigDecimal taxRate;

	// budget options
	private Calendar expenseCreateDate;
	private String  expenseType;
	private Double expenseAmount;
	private String expenseNote;
	private String expenseApprovalStatus;
	private Calendar expenseActionDate;
	private String expenseApproverName;
	//

	private String type;

	private Long childCount;

	public Long getChildCount() {
		return childCount;
	}

	public void setChildCount(Long childCount) {
		this.childCount = childCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBundle() {
		return "B".equals(type);
	}

	public WorkReportRow() {
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getWorkMarketFee() {
		return workMarketFee;
	}

	public void setWorkMarketFee(BigDecimal workMarketFee) {
		this.workMarketFee = workMarketFee;
	}

	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	public Boolean getScheduleRangeFlag() {
		return scheduleRangeFlag;
	}

	public void setScheduleRangeFlag(Boolean scheduleRangeFlag) {
		this.scheduleRangeFlag = scheduleRangeFlag;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Calendar getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Calendar completedOn) {
		this.completedOn = completedOn;
	}

	public Calendar getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(Calendar closedOn) {
		this.closedOn = closedOn;
	}

	public void setHoursWorked(Double hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public Double getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursBudgeted(Double hoursBudgeted) {
		this.hoursBudgeted = hoursBudgeted;
	}

	public Double getHoursBudgeted() {
		return hoursBudgeted;
	}

	public Calendar getSentOn() {
		return sentOn;
	}

	public void setSentOn(Calendar sentOn) {
		this.sentOn = sentOn;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setWorkTotalCost(BigDecimal workTotalCost) {
		this.workTotalCost = workTotalCost;
	}

	public BigDecimal getWorkTotalCost() {
		return workTotalCost;
	}

	public void setInvoiceSummaryNumber(String invoiceSummaryNumber) {
		this.invoiceSummaryNumber = invoiceSummaryNumber;
	}

	public String getInvoiceSummaryNumber() {
		return invoiceSummaryNumber;
	}

	public Calendar getAssignedResourceAppointmentDate() {
		return assignedResourceAppointmentDate;
	}

	public void setAssignedResourceAppointmentDate(Calendar assignedResourceAppointmentDate) {
		this.assignedResourceAppointmentDate = assignedResourceAppointmentDate;
	}

	public BigDecimal getPendingApprovalCost() {
		return pendingApprovalCost;
	}

	public void setPendingApprovalCost(BigDecimal pendingApprovalCost) {
		this.pendingApprovalCost = pendingApprovalCost;
	}

	public boolean isTaxCollected() {
		return taxCollected;
	}

	public void setTaxCollected(boolean taxCollected) {
		this.taxCollected = taxCollected;
	}

	public BigDecimal getTaxRate() {
		if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
			if (taxRate.compareTo(BigDecimal.ONE) > 0) {
				return taxRate.divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
			}
		}
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public String getTaxCollected() {
		return StringUtilities.toYesNo(taxCollected);
	}

	public BigDecimal getTaxesDue() {
		if (taxCollected && getTaxRate() != null) {
			return workPrice.multiply(getTaxRate(), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return BigDecimal.ZERO;
	}

	public Calendar getExpenseCreateDate() {
		return expenseCreateDate;
	}

	public void setExpenseCreateDate(Calendar expenseCreateDate) {
		this.expenseCreateDate = expenseCreateDate;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	public Double getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(Double expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public String getExpenseNote() {
		return expenseNote;
	}

	public void setExpenseNote(String expenseNote) {
		this.expenseNote = expenseNote;
	}

	public Calendar getExpenseActionDate() {
		return expenseActionDate;
	}

	public void setExpenseActionDate(Calendar expenseActionDate) {
		this.expenseActionDate = expenseActionDate;
	}

	public String getExpenseApproverName() {
		return expenseApproverName;
	}

	public void setExpenseApproverName(String expenseApproverName) {
		this.expenseApproverName = expenseApproverName;
	}

	public String getExpenseApprovalStatus() {
		return expenseApprovalStatus;
	}

	public void setExpenseApprovalStatus(String expenseApprovalStatus) {
		this.expenseApprovalStatus = expenseApprovalStatus;
	}

	public BigDecimal getWorkFeePercentage() { return workFeePercentage; }

	public void setWorkFeePercentage(BigDecimal workFeePercentage) { this.workFeePercentage = workFeePercentage; }

	public BigDecimal getWorkOverridePrice() { return workOverridePrice; }

	public void setWorkOverridePrice(BigDecimal workOverridePrice) { this.workOverridePrice = workOverridePrice; }

	public double getPrice() {
		if (WorkStatusType.PAID.equals(getStatus()) ||
			WorkStatusType.PAYMENT_PENDING.equals(getStatus()) ||
			WorkStatusType.CANCELLED.equals(getStatus()) ||
			WorkStatusType.CANCELLED_WITH_PAY.equals(getStatus()) ||
			WorkStatusType.CANCELLED_PAYMENT_PENDING.equals(getStatus())) {
			return getBuyerTotalCost();
		} else {
			return getSpendLimitWithFee();
		}
	}
}
