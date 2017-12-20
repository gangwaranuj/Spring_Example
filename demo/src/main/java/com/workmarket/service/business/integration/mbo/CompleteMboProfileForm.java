package com.workmarket.service.business.integration.mbo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CompleteMboProfileForm {

	@NotEmpty private String postalCode;
	@NotEmpty private String city;
	@NotEmpty private String country;
	@NotNull private BigDecimal longitude;
	@NotNull private BigDecimal latitude;
	@NotEmpty private String address1;

	private String addressTyper;
	private String state;

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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddressTyper() {
		return addressTyper;
	}

	public void setAddressTyper(String addressTyper) {
		this.addressTyper = addressTyper;
	}
}
