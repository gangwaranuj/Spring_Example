package com.workmarket.dto;

import java.math.BigDecimal;
import java.util.Calendar;

public class CompanyResource extends AddressDTO {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String userNumber;
	private String firstName;
	private String lastName;
	private Long companyId;
	private String companyName;
	private Integer YTDWork;
	private Double YTDPayments;
	private String rolesString;
	private Calendar lastLogin;
	private Integer laneType;
	private BigDecimal latitude;
	private BigDecimal longitude;

	public Long getId() {
		return id;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Integer getYTDWork() {
		return YTDWork;
	}

	public Double getYTDPayments() {
		return YTDPayments;
	}

	public String getRolesString() {
		return rolesString;
	}

	public Calendar getLastLogin() {
		return lastLogin;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setYTDWork(Integer yTDWork) {
		YTDWork = yTDWork;
	}

	public void setYTDPayments(Double yTDPayments) {
		YTDPayments = yTDPayments;
	}

	public void setRolesString(String rolesString) {
		this.rolesString = rolesString;
	}

	public void setLastLogin(Calendar lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Integer getLaneType() {
		return laneType;
	}

	public void setLaneType(Integer laneType) {
		this.laneType = laneType;
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

}
