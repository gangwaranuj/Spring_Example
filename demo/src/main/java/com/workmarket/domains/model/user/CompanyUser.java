package com.workmarket.domains.model.user;


import com.workmarket.utility.StringUtilities;

import java.io.Serializable;
import java.util.Calendar;

public class CompanyUser implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String userNumber;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean emailConfirmed;
	private String userStatusType;
	private String rolesString;
	private String laneAccessString;

	private Calendar latestActivityOn;
	private String latestActivityInetAddress;

	private CompanyUserStats stats;
	
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

    public String getFullName() {
        return StringUtilities.fullName(getFirstName(), getLastName());
    }

	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Boolean getEmailConfirmed() {
		return this.emailConfirmed;
	}
	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}
	
	public String getUserStatusType() {
		return this.userStatusType;
	}
	public void setUserStatusType(String userStatusType) {
		this.userStatusType = userStatusType;
	}
	
	public String getRolesString() {
		return this.rolesString;
	}
	public void setRolesString(String rolesString) {
		this.rolesString = rolesString;
	}
	
	public String getLaneAccessString() {
		return this.laneAccessString;
	}
	public void setLaneAccessString(String laneAccessString) {
		this.laneAccessString = laneAccessString;
	}

	public Calendar getLatestActivityOn() {
		return latestActivityOn;
	}
	public void setLatestActivityOn(Calendar latestActivityOn) {
		this.latestActivityOn = latestActivityOn;
	}

	public String getLatestActivityInetAddress() {
		return latestActivityInetAddress;
	}
	public void setLatestActivityInetAddress(String latestActivityInetAddress) {
		this.latestActivityInetAddress = latestActivityInetAddress;
	}

	public CompanyUserStats getStats() {
		return stats;
	}
	public void setStats(CompanyUserStats stats) {
		this.stats = stats;
	}
}