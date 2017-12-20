package com.workmarket.domains.model.cache;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This is purely a value object, but I've put it in the cache package
 * for easy refactoring later. 
 * @author chris
 *
 */
@Embeddable
@Access(AccessType.PROPERTY)
public class PeopleAggregateSummary {
	
	private Integer invitations;
	private Integer campaigns;
	private Integer groups;
	private Integer lane0;
	private Integer lane1;
	private Integer lane2;
	private Integer lane3;	
	private Integer lane3WithEINTaxEntity;
	private Integer groupMembers;
	
	@Column(name = "invitation_count", nullable = true)
	public Integer getInvitations() {
		return invitations;
	}
	
	@Column(name = "group_count", nullable = true)
	public Integer getGroups() {
		return groups;
	}
	
	@Column(name = "lane0_count", nullable = true)
	public Integer getLane0() {
		return lane0;
	}
	
	@Column(name = "lane1_count", nullable = true)
	public Integer getLane1() {
		return lane1;
	}
	
	@Column(name = "lane2_count", nullable = true)
	public Integer getLane2() {
		return lane2;
	}
	
	@Column(name = "lane3_count", nullable = true)
	public Integer getLane3() {
		return lane3;
	}
	
	public void setInvitations(Integer invitations) {
		this.invitations = invitations;
	}
	public void setGroups(Integer groups) {
		this.groups = groups;
	}
	public void setLane0(Integer lane0) {
		this.lane0 = lane0;
	}
	public void setLane1(Integer lane1) {
		this.lane1 = lane1;
	}
	public void setLane2(Integer lane2) {
		this.lane2 = lane2;
	}
	public void setLane3(Integer lane3) {
		this.lane3 = lane3;
	}
	
	@Column(name = "campaign_count", nullable = true)
	public Integer getCampaigns() {
		return campaigns;
	}
	public void setCampaigns(Integer campaigns) {
		this.campaigns = campaigns;
	}
		
	@Column(name = "group_member_count", nullable = true)
	public Integer getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(Integer groupMembers) {
		this.groupMembers = groupMembers;
	}
	
	@Column(name = "lane3_with_ein_count", nullable = true)
	public Integer getLane3WithEINTaxEntity() {
		return lane3WithEINTaxEntity;
	}
	public void setLane3WithEINTaxEntity(Integer lane3WithEINTaxEntity) {
		this.lane3WithEINTaxEntity = lane3WithEINTaxEntity;
	}
	
	
	
}
