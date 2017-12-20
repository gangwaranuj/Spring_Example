package com.workmarket.service.business.dto;

import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.utility.BeanUtilities;

import java.util.Calendar;

public class InsuranceDTO {

	private Long insuranceId;
	private String name;
	private String provider;
	private String policyNumber;
	private boolean requireMinimumCoverage;
	private String requiredCoverage;
	private String coverage;
	private Calendar issueDate;
	private boolean requireRemoveOnExpiration;
	private Calendar expirationDate;
	private boolean notApplicableOverride;

	public static InsuranceDTO newDTO(Insurance insurance, boolean isRequireMinimumCoverage, String requiredCoverage, String actualCoverage) {
		InsuranceDTO dto = new InsuranceDTO();
		BeanUtilities.copyProperties(dto, insurance);
		dto.setInsuranceId(insurance.getId());
		dto.setRequireMinimumCoverage(isRequireMinimumCoverage);
		dto.setRequiredCoverage(requiredCoverage);
		dto.setCoverage(actualCoverage);
		return dto;
	}

	public static InsuranceDTO newDTO(Insurance insurance) {
		return newDTO(insurance, false, null, null);
	}

	public Long getInsuranceId() {
		return insuranceId;
	}
	public void setInsuranceId(Long insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public boolean isRequireMinimumCoverage() {
		return requireMinimumCoverage;
	}

	public void setRequireMinimumCoverage(boolean requireMinimumCoverage) {
		this.requireMinimumCoverage = requireMinimumCoverage;
	}

	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getRequiredCoverage() {
		return requiredCoverage;
	}

	public void setRequiredCoverage(String requiredCoverage) {
		this.requiredCoverage = requiredCoverage;
	}

	public Calendar getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	public boolean isRequireRemoveOnExpiration() {
		return requireRemoveOnExpiration;
	}

	public void setRequireRemoveOnExpiration(boolean requireRemoveOnExpiration) {
		this.requireRemoveOnExpiration = requireRemoveOnExpiration;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isNotApplicableOverride() {
		return notApplicableOverride;
	}

	public void setNotApplicableOverride(boolean notApplicableOverride) {
		this.notApplicableOverride = notApplicableOverride;
	}
}
