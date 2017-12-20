package com.workmarket.search.model;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class SearchUser implements Serializable {

	private static final long serialVersionUID = 5603568956435630521L;

	private Long id;
	private List<Long> industries;
	private String userNumber;
	private String companyNumber;
	private List<Long> companyUserGroupIds;
	private Long companyId;
	private Long profileId;
	private Integer maxTravelDistance = 100;
	private List<Long> blockedUserIds = Lists.newArrayListWithCapacity(0);
	private List<Long> blockedCompanyIds = Lists.newArrayListWithCapacity(0);
	private GeoPoint location;
	private String firstName;
	private String lastName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getIndustries() {
		return industries;
	}

	public void setIndustries(List<Long> industries) {
		this.industries = industries;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getCompanyNumber() { return companyNumber; }

	public void setCompanyNumber(String companyNumber) { this.companyNumber = companyNumber; }

	public List<Long> getCompanyUserGroupIds() {
		return companyUserGroupIds;
	}

	public void setCompanyUserGroupIds(List<Long> companyUserGroupIds) {
		this.companyUserGroupIds = companyUserGroupIds;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public Integer getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(Integer maxTravelDistance) {
		if (maxTravelDistance != null) {
			this.maxTravelDistance = maxTravelDistance;
		}
	}

	public List<Long> getBlockedUserIds() {
		return blockedUserIds;
	}

	public void setBlockedUserIds(List<Long> blockedUserIds) {
		if (blockedUserIds != null) {
			this.blockedUserIds = blockedUserIds;
		}
	}

	public List<Long> getBlockedCompanyIds() { return blockedCompanyIds; }

	public void setBlockedCompanyIds(List<Long> blockedCompanyIds) {
		if (blockedCompanyIds != null) {
			this.blockedCompanyIds = blockedCompanyIds;
		}
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockedUserIds == null) ? 0 : blockedUserIds.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((companyUserGroupIds == null) ? 0 : companyUserGroupIds.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((industries == null) ? 0 : industries.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((maxTravelDistance == null) ? 0 : maxTravelDistance.hashCode());
		result = prime * result + ((userNumber == null) ? 0 : userNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchUser other = (SearchUser) obj;
		if (blockedUserIds == null) {
			if (other.blockedUserIds != null)
				return false;
		} else if (!blockedUserIds.equals(other.blockedUserIds))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (companyUserGroupIds == null) {
			if (other.companyUserGroupIds != null)
				return false;
		} else if (!companyUserGroupIds.equals(other.companyUserGroupIds))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (industries == null) {
			if (other.industries != null)
				return false;
		} else if (!industries.equals(other.industries))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (maxTravelDistance == null) {
			if (other.maxTravelDistance != null)
				return false;
		} else if (!maxTravelDistance.equals(other.maxTravelDistance))
			return false;
		if (userNumber == null) {
			if (other.userNumber != null)
				return false;
		} else if (!userNumber.equals(other.userNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchUser{" +
			"id=" + id +
			", industries=" + industries +
			", userNumber='" + userNumber + '\'' +
			", companyUserGroupIds=" + companyUserGroupIds +
			", companyId=" + companyId +
			", maxTravelDistance=" + maxTravelDistance +
			", blockedUserIds=" + blockedUserIds +
			", location=" + location +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			'}';
	}
}
