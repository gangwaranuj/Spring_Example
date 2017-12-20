package com.workmarket.service.business.dto;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.utility.BeanUtilities;

import java.math.BigDecimal;
import java.util.Calendar;

public class UserDTO {

	private Long id;
	private Long companyId;
	private String uuid;

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Long recruitingCampaignId;
	private BigDecimal spendLimit = Constants.DEFAULT_SPEND_LIMIT;
	private BigDecimal salary;
	private Integer stockOptions;
	private Calendar startDate;
	private Boolean operatingAsIndividualFlag = Boolean.FALSE;
	private boolean emailConfirmed;
	private String secondaryEmail;
	private String planCode;
	private String networkId;
	private String mobilePhone;
	private String resumeUrl;
	private Integer warpRequisitionId;

	public static UserDTO newDTO(User user) {
		UserDTO userDTO = new UserDTO();
		BeanUtilities.copyProperties(userDTO, user);
		return userDTO;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRecruitingCampaignId(Long recruitingCampaignId) {
		this.recruitingCampaignId = recruitingCampaignId;
	}

	public Long getRecruitingCampaignId() {
		return recruitingCampaignId;
	}

	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

	public void setSpendLimit(Double spendLimit) {
		setSpendLimit(BigDecimal.valueOf(spendLimit));
	}
	public void setSpendLimit(BigDecimal spendLimit) {
		this.spendLimit = spendLimit;
	}
	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		setSalary(BigDecimal.valueOf(salary));
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public Integer getStockOptions() {
		return stockOptions;
	}

	public void setStockOptions(Integer stockOptions) {
		this.stockOptions = stockOptions;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Boolean getOperatingAsIndividualFlag() {
		return operatingAsIndividualFlag;
	}

	public void setOperatingAsIndividualFlag(Boolean operatingAsIndividualFlag) {
		this.operatingAsIndividualFlag = operatingAsIndividualFlag;
	}

	/**
	 * @return the emailConfirmed
	 */
	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	/**
	 * @param emailConfirmed the emailConfirmed to set
	 */
	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public Integer getWarpRequisitionId(){ return warpRequisitionId; }

	public void setWarpRequisitionId(Integer warpRequisitionId) { this.warpRequisitionId = warpRequisitionId; }
}
