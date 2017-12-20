package com.workmarket.data.aggregate;

import java.math.BigDecimal;
import java.util.Calendar;

public class CompanyAggregate {

	private Long companyId;
	private String companyName;
	private Integer lane0Users;
	private Integer lane1Users;
	private Integer lane2Users;
	private Integer lane3Users;
	private Integer YTDAssignments;
	private BigDecimal availableCreditLimit;
	private Calendar createdOn;
	private String companyStatus;
	private Calendar lockedOn;
	private String customerType;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getLane0Users() {
		return lane0Users;
	}

	public void setLane0Users(Integer lane0Users) {
		this.lane0Users = lane0Users;
	}

	public Integer getLane1Users() {
		return lane1Users;
	}

	public void setLane1Users(Integer lane1Users) {
		this.lane1Users = lane1Users;
	}

	public Integer getLane2Users() {
		return lane2Users;
	}

	public void setLane2Users(Integer lane2Users) {
		this.lane2Users = lane2Users;
	}

	public Integer getLane3Users() {
		return lane3Users;
	}

	public void setLane3Users(Integer lane3Users) {
		this.lane3Users = lane3Users;
	}

	public Integer getYTDAssignments() {
		return YTDAssignments;
	}

	public void setYTDAssignments(Integer YTDAssignments) {
		this.YTDAssignments = YTDAssignments;
	}

	public BigDecimal getAvailableCreditLimit() {
		return availableCreditLimit;
	}
	
	public void setAvailableCreditLimit(BigDecimal availableCreditLimit) {
		this.availableCreditLimit = availableCreditLimit;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	public void setCompanyStatus(String companyStatus) {
		this.companyStatus = companyStatus;
	}

	public String getCompanyStatus() {
		return companyStatus;
	}
	
	public Integer countAllResources() {
		return getLane0Users() + getLane1Users() + getLane2Users() + getLane3Users();
	}

	public void setLockedOn(Calendar lockedOn) {
		this.lockedOn = lockedOn;
	}

	public Calendar getLockedOn() {
		return lockedOn;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
}
