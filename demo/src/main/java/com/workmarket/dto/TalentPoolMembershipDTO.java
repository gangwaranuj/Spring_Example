package com.workmarket.dto;

import com.google.common.collect.Maps;

import java.util.Calendar;
import java.util.Map;

public class TalentPoolMembershipDTO {

	private Map<Long, Calendar> memberships;
	private Map<Long, Calendar> invitations;
	private Map<Long, Calendar> applications;

	public TalentPoolMembershipDTO() {
		memberships = Maps.newHashMap();
		invitations = Maps.newHashMap();
		applications = Maps.newHashMap();
	}

	public Map<Long, Calendar> getMemberships() {
		return memberships;
	}

	public void setMemberships(Map<Long, Calendar> memberships) {
		this.memberships = memberships;
	}

	public Map<Long, Calendar> getInvitations() {
		return invitations;
	}

	public void setInvitations(Map<Long, Calendar> invitations) {
		this.invitations = invitations;
	}

	public Map<Long, Calendar> getApplications() {
		return applications;
	}

	public void setApplications(Map<Long, Calendar> applications) {
		this.applications = applications;
	}
}
