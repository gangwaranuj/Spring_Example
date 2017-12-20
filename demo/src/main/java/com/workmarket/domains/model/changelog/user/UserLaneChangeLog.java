package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("UL")
@AuditChanges
public abstract class UserLaneChangeLog extends UserCompanyChangeLog {
	private static final long serialVersionUID = 1L;

	@NotNull
	private LaneType laneType;

	public UserLaneChangeLog() {}

	public UserLaneChangeLog(Company company, LaneType laneType) {
		super(company);
		this.laneType = laneType;
	}

	public UserLaneChangeLog(Long user, Long actor, Long masqueradeActor, Company company, LaneType laneType) {
		super(user, actor, masqueradeActor, company);
		this.laneType = laneType;
	}

	@Column(name = "lane_type_id", nullable = false, unique = false)
	public LaneType getLaneType() {
		return laneType;
	}

	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}
}
