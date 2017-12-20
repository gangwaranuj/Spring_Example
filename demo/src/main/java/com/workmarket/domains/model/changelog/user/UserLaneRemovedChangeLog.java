package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("ULR")
@AuditChanges
public class UserLaneRemovedChangeLog extends UserLaneChangeLog {
	private static final long serialVersionUID = 1L;

	public UserLaneRemovedChangeLog() {
	}

	public UserLaneRemovedChangeLog(Company company, LaneType laneType) {
		super(company, laneType);
	}

	public UserLaneRemovedChangeLog(Long user, Long actor, Long masqueradeActor, Company company, LaneType laneType) {
		super(user, actor, masqueradeActor, company, laneType);
	}

	@Transient
	@Override
	public String getDescription() {
		return "Lane removed";
	}
}
