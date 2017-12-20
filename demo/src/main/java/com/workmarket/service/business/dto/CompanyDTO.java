package com.workmarket.service.business.dto;

import java.math.BigDecimal;

import com.workmarket.configuration.Constants;
import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.ArrayUtilities;

public class CompanyDTO extends AddressDTO {

	private Long companyId;
	private String name;
	private String website;
	private String overview;
	private Integer employees;
	private Integer yearFounded;
	private Integer employedProfessionals;
	private Long industryId;
	private Long[] industryIds;
	private Integer lowBalancePercentage = Constants.LOW_BALANCE_PERCENTAGE;
	private BigDecimal lowBalanceAmount;
	private Boolean customLowBalanceFlag = Boolean.FALSE;
	private Boolean operatingAsIndividualFlag = Boolean.FALSE;
	private String effectiveName;
	public String getName() {
		return name;
	}

	public String getWebsite() {
		return website;
	}

	public String getOverview() {
		return overview;
	}

	public Integer getEmployees() {
		return employees;
	}

	public Integer getYearFounded() {
		return yearFounded;
	}

	public Integer getEmployedProfessionals() {
		return employedProfessionals;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public void setEmployees(Integer employees) {
		this.employees = employees;
	}

	public void setYearFounded(Integer yearFounded) {
		this.yearFounded = yearFounded;
	}

	public void setEmployedProfessionals(Integer employedProfessionals) {
		this.employedProfessionals = employedProfessionals;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Long[] getIndustryIds() {
		return industryIds;
	}

	public void setIndustryIds(Long[] industryIds) {
		this.industryIds = industryIds;
	}

	public void setIndustryIds(Integer[] industryIds) {
		setIndustryIds(ArrayUtilities.convertToLongArrays(industryIds));
	}

	public Integer getLowBalancePercentage() {
		return lowBalancePercentage;
	}

	public void setLowBalancePercentage(Integer lowBalancePercentage) {
		this.lowBalancePercentage = lowBalancePercentage;
		this.customLowBalanceFlag = true;
	}

	public BigDecimal getLowBalanceAmount() {
		return lowBalanceAmount;
	}

	public void setLowBalanceAmount(BigDecimal lowBalanceAmount) {
		this.lowBalanceAmount = lowBalanceAmount;
	}

	public void setLowBalanceAmount(Double lowBalanceAmount) {
		this.lowBalanceAmount = new BigDecimal(lowBalanceAmount);
	}

	public Boolean getCustomLowBalanceFlag() {
		return customLowBalanceFlag;
	}

	public void setCustomLowBalanceFlag(Boolean customLowBalanceFlag) {
		this.customLowBalanceFlag = customLowBalanceFlag;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public Boolean getOperatingAsIndividualFlag() {
		return operatingAsIndividualFlag;
	}

	public void setOperatingAsIndividualFlag(Boolean operatingAsIndividualFlag) {
		this.operatingAsIndividualFlag = operatingAsIndividualFlag;
	}

	/**
	 * @return the effectiveName
	 */
	public String getEffectiveName() {
		return effectiveName;
	}

	/**
	 * @param effectiveName the effectiveName to set
	 */
	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}



}
