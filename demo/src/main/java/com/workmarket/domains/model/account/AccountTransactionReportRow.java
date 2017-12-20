package com.workmarket.domains.model.account;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: rocio
 * Date: 3/1/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountTransactionReportRow {

	private Long companyId;
	private String companyName;
	private String companyNumber;
	private BigDecimal amount = BigDecimal.ZERO;

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

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
