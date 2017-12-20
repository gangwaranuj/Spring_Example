package com.workmarket.service.business.dto;

import java.math.BigDecimal;

import com.workmarket.dto.AbstractCustomUserEntity;
import com.workmarket.utility.StringUtilities;

public class GroupMembershipDTO extends AbstractCustomUserEntity {

	private Integer starRating;
	private Integer verificationStatus;
	private Integer approvalStatus;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String derivedStatus;

	private String email;
	private String jobTitle;
	private Double requirementsFitScore;
	private Long requestEncryptedId;
	private String postalCodeOnly;

	/**
	 * @return the starRating
	 */
	public Integer getStarRating() {
		return starRating;
	}
	/**
	 * @param starRating the starRating to set
	 */
	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

	/**
	 * @return the verificiationStatus
	 */
	public Integer getVerificationStatus() {
		return verificationStatus;
	}
	/**
	 * @param verificiationStatus the verificiationStatus to set
	 */
	public void setVerificationStatus(Integer verificationStatus) {
		this.verificationStatus = verificationStatus;
	}
	/**
	 * @return the approvalStatus
	 */
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	/**
	 * @param approvalStatus the approvalStatus to set
	 */
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the derivedStatus
	 */
	public String getDerivedStatus() {
		return derivedStatus;
	}
	/**
	 * @param derivedStatus the derivedStatus to set
	 */
	public void setDerivedStatus(String derivedStatus) {
		this.derivedStatus = derivedStatus;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}
	/**
	 * @param jobTitle the jobTitle to set
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	/**
	 * @return the requirementsFitScore
	 */
	public Double getRequirementsFitScore() {
		return requirementsFitScore;
	}
	/**
	 * @param requirementsFitScore the requirementsFitScore to set
	 */
	public void setRequirementsFitScore(Double requirementsFitScore) {
		this.requirementsFitScore = requirementsFitScore;
	}
	/**
	 * @return the requestEncryptedId
	 */
	public Long getRequestEncryptedId() {
		return requestEncryptedId;
	}
	/**
	 * @param requestEncryptedId the requestEncryptedId to set
	 */
	public void setRequestEncryptedId(Long requestEncryptedId) {
		this.requestEncryptedId = requestEncryptedId;
	}
	/**
	 * @return the postalCodeOnly
	 */
	public String getPostalCodeOnly() {
		return postalCodeOnly;
	}
	/**
	 * @param postalCodeOnly the postalCodeOnly to set
	 */
	public void setPostalCodeOnly(String postalCodeOnly) {
		this.postalCodeOnly = postalCodeOnly;
	}
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return StringUtilities.fullName(getFirstName(), getLastName());
	}
}
