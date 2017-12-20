package com.workmarket.web.forms;

import com.workmarket.domains.model.CallingCode;
import com.workmarket.web.forms.base.AddressForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class GeneralProfileForm extends AddressForm {

	@Pattern(regexp="^[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}+$", message="First Name must be alphanumeric and between 2 and 50 characters")
	String firstName;

	@Pattern(regexp="^[a-zA-Z0-9'àáâäãåèéêëìíîïòóôöõøùúûüÿýñçčšžÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕØÙÚÛÜŸÝÑßÇŒÆČŠŽ∂ð\\-\\p{Space}]{2,50}+$", message="Last Name must be alphanumeric and between 2 and 50 characters")
	String lastName;

	@Pattern(regexp="[A-Za-z0-9\\._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}", message="Invalid Email Address")
	String userEmail;

	@Pattern(regexp="([A-Za-z0-9\\._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})?", message="Invalid Secondary Email Address")
	String userEmailSecondary;

	String changedEmail;

	@Pattern(regexp="^[^<>\"\\(\\)]{0,45}+$", message="Job Title may not contain (<,>,\",(,)")
	String jobTitle;

	String workPhone;

	String workPhoneInternationalCode;

	@Pattern(regexp="^[0-9]*$", message="Work Phone Extension must be digits only")
	String workPhoneExtension;

	String mobilePhone;

	String mobilePhoneInternationalCode;

	@NotNull
	Long timezone;

	List<CallingCode> callingcodes;
	
	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(String workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public String getMobilePhoneInternationalCode() {
		return mobilePhoneInternationalCode;
	}

	public void setMobilePhoneInternationalCode(String mobilePhoneInternationalCode) {
		this.mobilePhoneInternationalCode = mobilePhoneInternationalCode;
	}

	public List<CallingCode> getCallingcodes() {
		return callingcodes;
	}

	public void setCallingcodes(List<CallingCode> callingcodes) {
		this.callingcodes = callingcodes;
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

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserEmailSecondary() {
		return userEmailSecondary;
	}
	public void setUserEmailSecondary(String userEmailSecondary) {
		this.userEmailSecondary = userEmailSecondary;
	}

	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
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

	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getTimezone() {
		return timezone;
	}
	public void setTimezone(Long timezone) {
		this.timezone = timezone;
	}

	public String getChangedEmail() {
		return changedEmail;
	}
	public void setChangedEmail(String changedEmail) {
		this.changedEmail = changedEmail;
	}
}
