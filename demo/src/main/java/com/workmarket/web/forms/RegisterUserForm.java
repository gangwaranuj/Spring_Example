package com.workmarket.web.forms;

import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.configuration.Constants;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class RegisterUserForm {

	public static final String FIND_WORK = Constants.REGISTER_RESOURCE_FIND_WORK;
	public static final String MANAGE_LABOR = Constants.REGISTER_RESOURCE_MANAGE_LABOR;

	@NotEmpty
	@Pattern(regexp="^(" + FIND_WORK + "|" + MANAGE_LABOR + ")$")
	String registrationType = FIND_WORK;

	@NotEmpty
	@Pattern(regexp="(^[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}+$)?")
	String firstName;

	@NotEmpty
	@Pattern(regexp="(^[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}+$)?")
	String lastName;

	String password; // validated by PasswordValidator

	String userEmail; // validated by UserEmailValidator

	String postalCode;

	String city;

	String state;

	String country;

	BigDecimal longitude;

	String workPhoneInternationalCode;

	BigDecimal latitude;

	String address1;

	String addressTyper;

	List<CallingCode> callingCodesList;

	String planCode;

	public String getAddressTyper() {
		return addressTyper;
	}

	public void setAddressTyper(String addressTyper) {
		this.addressTyper = addressTyper;
	}

	String workPhone;

	@Pattern(regexp="^[0-9]*$")
	String workPhoneExtension;

	@Size(min=0, max=50)
	String companyName;

	Boolean operatingAsIndividual;

	@NotNull
	Long industryId;

	String termsAgree;

	String pictureUrl;

	String networkId;

	String campaignId;

	public List<CallingCode> getCallingCodesList() {
		return callingCodesList;
	}

	public void setCallingCodesList(List<CallingCode> callingCodesList) {
		this.callingCodesList = callingCodesList;
	}


	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(String workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getWorkPhone() {
		return workPhone;
	}
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}
	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Boolean getOperatingAsIndividual() {
		return operatingAsIndividual;
	}
	public void setOperatingAsIndividual(Boolean operatingAsIndividual) {
		this.operatingAsIndividual = operatingAsIndividual;
	}

	public Long getIndustryId() {
		return industryId;
	}
	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public String getTermsAgree() {
		return termsAgree;
	}
	public void setTermsAgree(String termsAgree) {
		this.termsAgree = termsAgree;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getFindWork() {
		return FIND_WORK;
	}

	public String getManageLabor() {
		return MANAGE_LABOR;
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

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public InvitationUserRegistrationDTO toInvitationUserRegistrationDTO() {
		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();
		dto.setState(getState());
		dto.setAddress1(getAddress1());
		dto.setPostalCode(getPostalCode());
		dto.setCountry(Country.valueOf(getCountry()).getId());
		dto.setCity(getCity());
		dto.setFirstName(getFirstName());
		dto.setLastName(getLastName());
		dto.setEmail(getUserEmail());
		dto.setWorkPhone(getWorkPhone());
		dto.setWorkPhoneExtension(getWorkPhoneExtension());
		dto.setWorkPhoneInternationalCode(getWorkPhoneInternationalCode());
		dto.setCompanyName(getCompanyName());
		dto.setIndustryId(getIndustryId());
		dto.setOperatingAsIndividualFlag(StringUtils.isBlank(getCompanyName()));
		dto.setPassword(getPassword());
		dto.setAgree(getTermsAgree());
		dto.setLatitude(getLatitude());
		dto.setLongitude(getLongitude());
		dto.setPlanCode(getPlanCode());
		dto.setNetworkId(getNetworkId());
		return dto;
	}
}
