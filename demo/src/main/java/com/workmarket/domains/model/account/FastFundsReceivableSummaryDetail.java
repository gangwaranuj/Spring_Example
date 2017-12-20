package com.workmarket.domains.model.account;

import java.util.Calendar;

public class FastFundsReceivableSummaryDetail extends AccountingSummaryDetail {
	Calendar fastFundedOnDate;
	String workNumber;
	Long buyerCompanyId;
	String workerCompanyName;
	Long workerCompanyId;

	public Calendar getFastFundedOnDate() {
		return fastFundedOnDate;
	}

	public void setFastFundsOnDate(Calendar fastFundedOnDate) {
		this.fastFundedOnDate = fastFundedOnDate;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public Long getBuyerCompanyId() {
		return buyerCompanyId;
	}

	public void setBuyerCompanyId(Long buyerCompanyId) {
		this.buyerCompanyId = buyerCompanyId;
	}

	public String getWorkerCompanyName() {
		return workerCompanyName;
	}

	public void setWorkerCompanyName(String workerCompanyName) {
		this.workerCompanyName = workerCompanyName;
	}

	public Long getWorkerCompanyId() {
		return workerCompanyId;
	}

	public void setWorkerCompanyId(Long workerCompanyId) {
		this.workerCompanyId = workerCompanyId;
	}
}
