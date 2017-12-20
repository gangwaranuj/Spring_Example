package com.workmarket.search.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.thrift.work.Work;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PeopleSearchTransientData extends AbstractSearchTransientData {

	/* Companies from user responses - key user value company */
	private Map<Long, Long> userCompanyMap = Maps.newHashMapWithExpectedSize(25);
	private Set<Long> groupIdsInResponse;
	// key - user id value - groups
	private Map<Long, Set<CompanyGroupId>> userGroupsInResponse;
	//Key userId, value assessmentId
	private Map<Long, Set<Long>> companyAssessmentIdsInResponse;
	private Map<Long, Integer> workCompletedCountsByUserForCompany;
	private String skills;
	private String description;
	private Set<LaneType> ignoredLanes;
	private Long inviteToGroupId;
	private long memberOfGroupId;
	private long inviteToAssessmentId;
	private List<Long> failedVerifications;
	private boolean enhancedRelevancy = true;
	private boolean typeAhead = false;
	private Work work;

	/**
	 * Takes in a user ID and looks over the results for another user. This will
	 * return a list of company IDs that will be "displayable" in the results.
	 * <p/>
	 * The rules go like this: user who searches has a company that owns groups.
	 * These groups have members. Only the user's company owned groups will be
	 * capable of viewing.
	 * <p/>
	 * The hydrator as the results get parsed keep track of the company/groupid
	 * for the groups that they belong to. this is stored in the
	 * userGroupsInResponseMap. The user has a company ID in the response. So we
	 * get that individual user's response and if the company matches the user,
	 * we will display that company group name.
	 *
	 * @param foundUserId
	 * @return
	 */
	public Collection<Long> findCompanyViewableByUserCompany(Long foundUserId) {
		// get the user's company/group IDs
		Collection<CompanyGroupId> companyGroupsForUser = userGroupsInResponse.get(foundUserId);
		if (companyGroupsForUser == null) {
			return null;
		}
		// iterate over the groups and if the company IDs match, it belongs in the result
		List<Long> groupsToDisplay = Lists.newLinkedList();
		for (CompanyGroupId companyGroup : companyGroupsForUser) {
			if (getCurrentUser().getCompanyId().equals(companyGroup.getCompanyId())) {
				groupsToDisplay.add(companyGroup.getGroupId());
			}
		}
		if (groupsToDisplay.size() > 0) {
			return groupsToDisplay;
		}
		return null;
	}

	public boolean isEnhancedRelevancy() {
		return enhancedRelevancy;
	}

	public void setEnhancedRelevancy(boolean enhancedRelevancy) {
		this.enhancedRelevancy = enhancedRelevancy;
	}

	public boolean isTypeAhead() {
		return typeAhead;
	}

	public void setTypeAhead(boolean typeAhead) {
		this.typeAhead = typeAhead;
	}

	public Map<Long, Long> getUserCompanyMap() {
		return userCompanyMap;
	}

	public void setUserCompanyMap(Map<Long, Long> userCompanyMap) {
		this.userCompanyMap = userCompanyMap;
	}

	public Set<Long> getGroupIdsInResponse() {
		return groupIdsInResponse;
	}

	public void setGroupIdsInResponse(Set<Long> groupIdsInResponse) {
		this.groupIdsInResponse = groupIdsInResponse;
	}

	public Map<Long, Set<CompanyGroupId>> getUserGroupsInResponse() {
		return userGroupsInResponse;
	}

	public void setUserGroupsInResponse(Map<Long, Set<CompanyGroupId>> userGroupsInResponse) {
		this.userGroupsInResponse = userGroupsInResponse;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Work getWork() { return this.work; }

	public void setWork(Work work) { this.work = work; }

	public void addToIgnoredLanes(LaneType laneToIgnore) {
		if (ignoredLanes == null) {
			ignoredLanes = Sets.newHashSetWithExpectedSize(1);
		}
		ignoredLanes.add(laneToIgnore);
	}

	public boolean isIgnoredLane(LaneType laneToCheck) {
		if (ignoredLanes == null) {
			return false;
		} else {
			return ignoredLanes.contains(laneToCheck);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PeopleSearchTransientData [searchType=");
		builder.append(getSearchType());
		builder.append(", currentUser=");
		builder.append(getCurrentUser());
		builder.append(", userCompanyMap=");
		builder.append(userCompanyMap);
		builder.append(", originalRequest=");
		builder.append(super.getOriginalRequest());
		builder.append(", groupIdsInResponse=");
		builder.append(groupIdsInResponse);
		builder.append(", userGroupsInResponse=");
		builder.append(userGroupsInResponse);
		builder.append(", skills=");
		builder.append(skills);
		builder.append(", description=");
		builder.append(description);
		builder.append(", point=");
		builder.append(getGeopoint());
		builder.append("]");
		return builder.toString();
	}

	public List<Long> getFailedVerifications() {
		return failedVerifications;
	}

	public void setFailedVerifications(List<Long> failedVerifications) {
		this.failedVerifications = failedVerifications;
	}

	public static class CompanyGroupId {
		private Long companyId;
		private Long groupId;

		public Long getCompanyId() {
			return companyId;
		}

		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}

		public Long getGroupId() {
			return groupId;
		}

		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
			CompanyGroupId other = (CompanyGroupId) obj;
			if (companyId == null) {
				if (other.companyId != null)
					return false;
			} else if (!companyId.equals(other.companyId))
				return false;
			if (groupId == null) {
				if (other.groupId != null)
					return false;
			} else if (!groupId.equals(other.groupId))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("CompanyGroupId [companyId=");
			builder.append(companyId);
			builder.append(", groupId=");
			builder.append(groupId);
			builder.append("]");
			return builder.toString();
		}

	}

	public Map<Long, Set<Long>> getCompanyAssessmentIdsInResponse() {
		return companyAssessmentIdsInResponse;
	}

	public void setCompanyAssessmentIdsInResponse(Map<Long, Set<Long>> companyAssessmentIdsInResponse) {
		this.companyAssessmentIdsInResponse = companyAssessmentIdsInResponse;
	}

	public Map<Long, Integer> getWorkCompletedCountsByUserForCompany() {
		return workCompletedCountsByUserForCompany;
	}

	public void setWorkCompletedCountsByUserForCompany(Map<Long, Integer> workCompletedCountsByUserForCompany) {
		this.workCompletedCountsByUserForCompany = workCompletedCountsByUserForCompany;
	}

	public Long getInviteToGroupId() {
		return inviteToGroupId;
	}

	public void setInviteToGroupId(Long inviteToGroupId) {
		this.inviteToGroupId = inviteToGroupId;
	}

	public boolean isSetInviteToGroupId() {
		return inviteToGroupId != null;
	}

	public long getMemberOfGroupId() {
		return memberOfGroupId;
	}

	public void setMemberOfGroupId(long memberOfGroupId) {
		this.memberOfGroupId = memberOfGroupId;
	}

	public boolean isSetMemberOfGroupId() {
		return (memberOfGroupId > 0L);
	}

	public boolean isGroupMemberSearch() {
		return SearchType.PEOPLE_SEARCH_GROUP_MEMBER.equals(getSearchType()) && isSetMemberOfGroupId();
	}

	public long getInviteToAssessmentId() {
		return inviteToAssessmentId;
	}

	public void setInviteToAssessmentId(long inviteToAssessmentId) {
		this.inviteToAssessmentId = inviteToAssessmentId;
	}

	public boolean isSetInviteToAssessmentId() {
		return (inviteToAssessmentId > 0L);
	}

	public boolean isAssessmentInviteSearch() {
		return SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE.equals(getSearchType()) && isSetInviteToAssessmentId();
	}

}