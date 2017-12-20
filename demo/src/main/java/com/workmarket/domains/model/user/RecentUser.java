package com.workmarket.domains.model.user;

import java.util.Calendar;

public class RecentUser {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String userNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String workPhone;
	private String mobilePhone;
	private Long companyId;
	private String companyName;
	private Calendar registeredOn;
	private Boolean lane1Flag;
	private Boolean lane2Flag;
	private Boolean lane3Flag;
	private Boolean lane4Flag;

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserNumber() {
		return this.userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getWorkPhone() {
		return this.workPhone;
	}
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}
	
	public String getMobilePhone() {
		return this.mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	public Long getCompanyId() {
		return this.companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	public String getCompanyName() {
		return this.companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public Calendar getRegisteredOn() {
		return this.registeredOn;
	}
	public void setRegisteredOn(Calendar registeredOn) {
		this.registeredOn = registeredOn;
	}
	
	public Boolean getLane1Flag() {
		return this.lane1Flag;
	}
	public void setLane1Flag(Boolean lane1Flag) {
		this.lane1Flag = lane1Flag;
	}
	
	public Boolean getLane2Flag() {
		return this.lane2Flag;
	}
	public void setLane2Flag(Boolean lane2Flag) {
		this.lane2Flag = lane2Flag;
	}
	
	public Boolean getLane3Flag() {
		return this.lane3Flag;
	}
	public void setLane3Flag(Boolean lane3Flag) {
		this.lane3Flag = lane3Flag;
	}
	
	public Boolean getLane4Flag() {
		return this.lane4Flag;
	}
	public void setLane4Flag(Boolean lane4Flag) {
		this.lane4Flag = lane4Flag;
	}
}