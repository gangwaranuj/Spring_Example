package com.workmarket.dto;

import com.workmarket.domains.model.lane.LaneType;

public abstract class AbstractCustomUserEntity extends AddressDTO {

	private Long userId;
	private String userNumber;
	private String firstName;
	private String lastName;
	private Long companyId;
	private String companyName;
	private LaneType laneType;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
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

	public LaneType getLaneType() {
		return laneType;
	}

	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}

	public void setLaneType(Long laneType) {
		if (laneType != null)
			this.laneType = LaneType.values()[laneType.intValue()];
	}

}
