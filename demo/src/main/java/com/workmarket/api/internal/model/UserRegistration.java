package com.workmarket.api.internal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.UserDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;


@ApiModel("UserRegistration")
public class UserRegistration {

	private Integer userId;
	private String userNumber;

	@NotEmpty
	@Email
	private String email;
	private String password;
	private String firstName;
	private String lastName;

	private String address;
	private String city;
	private String stateCode;
	private String postalCode;
	private String isoCountryCode; // ISO 2-Letter country code

	private boolean returnPassword = false;
	private boolean autoConfirmEmail = false;
	private boolean sendConfirmEmail = true;
	private boolean onboardCompleted = false;
	private String phoneNumber;
	private String countryCallingCode;

	private String resumeUrl;

	private Integer warpRequisitionId;

	@Override
	public String toString() {
		return "UserRegistration{" +
				"userId=" + userId +
				", email='" + email + '\'' +
				'}';
	}

	@ApiModelProperty
	@JsonProperty
	public String getResumeUrl() {
		return resumeUrl;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	@ApiModelProperty
	@JsonProperty
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@ApiModelProperty
	@JsonProperty
	public String getCountryCallingCode() {
		return this.countryCallingCode;
	}

	public void setCountryCallingCode(String countryCallingCode) {
		this.countryCallingCode = countryCallingCode;
	}

	@ApiModelProperty
	@JsonProperty
	public boolean isOnboardCompleted() {
		return onboardCompleted;
	}

	public void setOnboardCompleted(boolean onboardCompleted) {
		this.onboardCompleted = onboardCompleted;
	}

	@ApiModelProperty
	@JsonProperty
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ApiModelProperty
	@JsonProperty
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@ApiModelProperty
	@JsonProperty
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ApiModelProperty
	@JsonProperty
	public boolean getReturnPassword() {
		return returnPassword;
	}

	public void setReturnPassword(boolean returnPassword) {
		this.returnPassword = returnPassword;
	}

	@ApiModelProperty
	@JsonProperty
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	@ApiModelProperty
	@JsonProperty
	public boolean getAutoConfirmEmail() {
		return autoConfirmEmail;
	}

	public void setAutoConfirmEmail(boolean autoConfirmEmail) {
		this.autoConfirmEmail = autoConfirmEmail;
	}

	@ApiModelProperty
	@JsonProperty
	public boolean getSendConfirmEmail() {
		return sendConfirmEmail;
	}

	public void setSendConfirmEmail(boolean sendConfirmEmail) {
		this.sendConfirmEmail = sendConfirmEmail;
	}

	@ApiModelProperty
	@JsonProperty
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@ApiModelProperty
	@JsonProperty
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ApiModelProperty
	@JsonProperty
	public String getAddress() {
		return address;
	}

	/**
	 * Utility method supports AddressValidator reuse within UserRegistrationValidator
	 */
	public String getAddress1() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public void setAddress1(String address) {
		this.address = address;
	}

	@ApiModelProperty
	@JsonProperty
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ApiModelProperty
	@JsonProperty
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * Utility method supports AddressValidator reuse within UserRegistrationValidator
	 */
	@ApiModelProperty
	@JsonProperty
	public String getState() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public void setState(String stateCode) {
		this.stateCode = stateCode;
	}

	@ApiModelProperty
	@JsonProperty
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	/**
	 * Utility method supports AddressValidator reuse within UserRegistrationValidator
	 */
	@ApiModelProperty
	@JsonProperty
	public String getCountry() {
		return isoCountryCode;
	}

	public void setIsoCountryCode(String isoCountryCode) {
	  this.isoCountryCode = isoCountryCode;
	}
	public void setCountry(String isoCountryCode) {
	  this.isoCountryCode = isoCountryCode;
	}

	public Integer getWarpRequisitionId(){ return warpRequisitionId; }

	public void setWarpRequisitionId(Integer warpRequisitionId) { this.warpRequisitionId = warpRequisitionId; }

	public UserDTO toUserDTO() {
		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(this.email);
		userDTO.setPassword(this.password);
		userDTO.setFirstName(this.firstName);
		userDTO.setLastName(this.lastName);
		userDTO.setMobilePhone(this.phoneNumber);
		userDTO.setResumeUrl(this.resumeUrl);
		userDTO.setWarpRequisitionId(this.warpRequisitionId);
		return userDTO;
	}

	public AddressDTO toAddressDTO() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1(this.address);
		addressDTO.setCity(this.city);
		addressDTO.setState(this.stateCode);
		addressDTO.setCountry(translateIso2ToKey(this.isoCountryCode));
		addressDTO.setPostalCode(this.postalCode);
		addressDTO.setAddressTypeCode(AddressType.PROFILE);

		return addressDTO;
	}


	/**
	 *  Expected form binding is an iso2 value. We use iso3 3 letter values for keys
	 *  for countries USA and CAN
	 */
	private String translateIso2ToKey(String isoCountryCode) {
		if (Country.US.equals(isoCountryCode)) {
			return Country.USA;
		}
		if (Country.ISO2_CANADA.equals(isoCountryCode)) {
			return Country.CANADA;
		}
		return isoCountryCode;
	}
}
