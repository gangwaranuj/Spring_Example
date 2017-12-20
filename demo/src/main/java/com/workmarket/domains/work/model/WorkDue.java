package com.workmarket.domains.work.model;

import com.workmarket.domains.model.company.CompanyStatusType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 7/6/12
 * Time: 6:13 PM
 */
public class WorkDue implements Serializable {

	private Long workId;
	private String workNumber;
	private String workTitle;
	private Long companyId;
	private Long invoiceId;
	private Long buyerUserId;
	private Calendar dueOn;
	private boolean statementsEnabled;
	private String companyStatusTypeCode;
	private BigDecimal buyerTotalCost;

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

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public Calendar getDueOn() {
		return dueOn;
	}

	public void setDueOn(Calendar dueOn) {
		this.dueOn = dueOn;
	}

	public boolean isStatementsEnabled() {
		return statementsEnabled;
	}

	public void setStatementsEnabled(boolean statementsEnabled) {
		this.statementsEnabled = statementsEnabled;
	}

	public String getCompanyStatusTypeCode() {
		return companyStatusTypeCode;
	}

	public void setCompanyStatusTypeCode(String companyStatusTypeCode) {
		this.companyStatusTypeCode = companyStatusTypeCode;
	}

	public boolean isCompanyActive() {
		return getCompanyStatusTypeCode().equals(CompanyStatusType.ACTIVE);
	}

	public BigDecimal getBuyerTotalCost() {
		return buyerTotalCost;
	}

	public void setBuyerTotalCost(BigDecimal buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
	}

	@Override
	public String toString() {
		return "WorkDue{" +
				"workId=" + workId +
				", companyId=" + companyId +
				", invoiceId=" + invoiceId +
				", buyerUserId=" + buyerUserId +
				", dueOn=" + dueOn +
				", statementsEnabled=" + statementsEnabled +
				", companyStatusTypeCode='" + companyStatusTypeCode + '\'' +
				'}';
	}
}
