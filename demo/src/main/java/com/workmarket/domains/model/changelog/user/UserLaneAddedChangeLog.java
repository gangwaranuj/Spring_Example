package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("ULA")
@AuditChanges
public class UserLaneAddedChangeLog extends UserLaneChangeLog {
	private static final long serialVersionUID = 1L;

	public UserLaneAddedChangeLog() {

	}

	public UserLaneAddedChangeLog(Company company, LaneType laneType) {
		super(company, laneType);
	}

	public UserLaneAddedChangeLog(Long user, Long actor, Long masqueradeActor, Company company, LaneType laneType) {
		super(user, actor, masqueradeActor, company, laneType);
	}

	@Transient
	@Override
	public String getDescription() {
		return "Lane added";
	}
}
