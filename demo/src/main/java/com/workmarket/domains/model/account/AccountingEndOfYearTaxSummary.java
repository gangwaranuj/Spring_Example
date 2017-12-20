package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.workmarket.utility.DateUtilities;

public class AccountingEndOfYearTaxSummary {
	private String companyName;
	private Long companyId;
	private BigDecimal paidToWorkers;
	private String serviceType;
	private String pricingType;
	private Calendar startDate;
	private Calendar endDate;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public BigDecimal getPaidToWorkers() {
		return paidToWorkers;
	}

	public void setPaidToWorkers(BigDecimal paidToWorkers) {
		this.paidToWorkers = paidToWorkers;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getPricingType() {
		return pricingType;
	}

	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
}
