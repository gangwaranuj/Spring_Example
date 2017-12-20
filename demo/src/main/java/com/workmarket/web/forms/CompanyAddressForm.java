package com.workmarket.web.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class CompanyAddressForm {

	Boolean useCompanyAddress;

	String address1;

	String address2;

	@NotNull(message="City is a required field")
	@Size(min=1, message="City is a required field")
	String city;


	String state;

	BigDecimal longitude;

	BigDecimal latitude;

	String postalCode;

	@NotNull(message="Country is a required field")
	@Size(min=1, message="Country is a required field")
	String country;

	Long timezone;

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

	public Boolean getUseCompanyAddress() {
		return useCompanyAddress;
	}
	public void setUseCompanyAddress(Boolean useCompanyAddress) {
		this.useCompanyAddress = useCompanyAddress;
	}

	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public Long getTimezone() {
		return timezone;
	}
	public void setTimezone(Long timezone) {
		this.timezone = timezone;
	}
}
