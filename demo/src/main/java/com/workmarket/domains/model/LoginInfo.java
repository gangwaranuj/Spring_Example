package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name = "loginInfo")
@Table(name = "login_info")
public class LoginInfo extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Long userId;
	private Long companyId;
	private Calendar loggedOn;
	private String roleString;
	private String inetAddress;
	private boolean successful;

	public LoginInfo() {}
	public LoginInfo(Long userId, Calendar loginDate, String inetAddress) {
		this.userId = userId;
		this.loggedOn = loginDate;
		this.inetAddress = inetAddress;
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "logged_on", nullable = false)
	public Calendar getLoggedOn() {
		return loggedOn;
	}

	public void setLoggedOn(Calendar loggedOn) {
		this.loggedOn = loggedOn;
	}

	@Column(name = "user_roles", nullable = false)
	public String getRoleString() {
		return roleString;
	}

	public void setRoleString(String roleString) {
		this.roleString = roleString;
	}

	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "ip_address", nullable = true)
	public String getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(String inetAddress) {
		this.inetAddress = inetAddress;
	}
	
	@Column(name = "successful", nullable = false)
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
