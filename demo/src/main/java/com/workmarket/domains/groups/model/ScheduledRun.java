package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@AuditChanges
@Entity(name = "scheduledRun")
@Table(name = "scheduled_run")
public class ScheduledRun extends DeletableEntity {

	private Calendar nextRun;
	private Calendar startedOn;
	private Calendar completedOn;
	private Integer interval;

	@Column(name = "next_run")
	public Calendar getNextRun() {
		return nextRun;
	}

	public void setNextRun(final Calendar nextRun) {
		this.nextRun = nextRun;
	}

	@Column(name = "started_on")
	public Calendar getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(final Calendar startedOn) {
		this.startedOn = startedOn;
	}

	@Column(name = "completed_on")
	public Calendar getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(final Calendar completedOn) {
		this.completedOn = completedOn;
	}

	/**
	 * @return Interval between runs, in days.
	 */
	@Column(name = "time_interval")
	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}
}
