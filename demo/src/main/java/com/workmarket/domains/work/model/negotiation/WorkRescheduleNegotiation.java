package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "workRescheduleNegotiation")
@DiscriminatorValue(AbstractWorkNegotiation.RESCHEDULE)
@AuditChanges
public class WorkRescheduleNegotiation extends AbstractWorkNegotiation implements ScheduleNegotiation {
	private static final long serialVersionUID = -925496509879593525L;

	private Boolean scheduleRangeFlag;
	private Calendar scheduleFrom;
	private Calendar scheduleThrough;
	private WorkSubStatusTypeAssociation workSubStatusTypeAssociation;

	@Column(name = "schedule_is_range_flag", nullable = false, length = 1)
	public Boolean getScheduleRangeFlag() {
		return this.scheduleRangeFlag;
	}

	public void setScheduleRangeFlag(Boolean scheduleRangeFlag) {
		this.scheduleRangeFlag = scheduleRangeFlag;
	}

	@Column(name = "schedule_from", nullable = true)
	public Calendar getScheduleFrom() {
		return this.scheduleFrom;
	}

	public void setScheduleFrom(Calendar scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
	}

	@Column(name = "schedule_through", nullable = true)
	public Calendar getScheduleThrough() {
		return this.scheduleThrough;
	}

	public void setScheduleThrough(Calendar scheduleThrough) {
		this.scheduleThrough = scheduleThrough;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne(optional = true)
	@JoinColumn(name = "work_substatus_association_id", referencedColumnName = "id")
	public WorkSubStatusTypeAssociation getWorkSubStatusTypeAssociation() {
		return workSubStatusTypeAssociation;
	}

	public void setWorkSubStatusTypeAssociation(WorkSubStatusTypeAssociation workSubStatusTypeAssociation) {
		this.workSubStatusTypeAssociation = workSubStatusTypeAssociation;
	}

	@Transient
	public String getNegotiationType() {
		return RESCHEDULE;
	}
}
