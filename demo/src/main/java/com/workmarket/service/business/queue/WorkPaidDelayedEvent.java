package com.workmarket.service.business.queue;

import java.util.Calendar;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.workmarket.domains.model.User;
import com.workmarket.service.business.event.ScheduledEvent;

public class WorkPaidDelayedEvent extends ScheduledEvent implements Delayed {

	protected WorkPaidDelayedEvent(Long workId, Long workResourceId, long delayTimeMillis, Calendar date, User actor) {
		super();
		this.workId = workId;
		this.workResourceId = workResourceId;
		this.delayTime = delayTimeMillis + System.currentTimeMillis();
		this.date = date;
		this.actor = actor;
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(delayTime);
		setScheduledDate(now);
	}

	private final Long workResourceId;
	private final Long workId;
	private final long delayTime;
	private Calendar date;

	// TODO: Alex - replace reference to entity with id or code
	@Deprecated
	private User actor;

	@Deprecated
	public User getActor() {
		return actor;
	}

	@Deprecated
	public void setActor(User actor) {
		this.actor = actor;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public Long getWorkId() {
		return workId;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (delayTime ^ (delayTime >>> 32));
		result = prime * result + ((workId == null) ? 0 : workId.hashCode());
		result = prime * result + ((workResourceId == null) ? 0 : workResourceId.hashCode());
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
		WorkPaidDelayedEvent other = (WorkPaidDelayedEvent) obj;
		if (delayTime != other.delayTime)
			return false;
		if (workId == null) {
			if (other.workId != null)
				return false;
		} else if (!workId.equals(other.workId))
			return false;
		if (workResourceId == null) {
			if (other.workResourceId != null)
				return false;
		} else if (!workResourceId.equals(other.workResourceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WorkEventDelay [workResourceId=" + workResourceId + ", workId=" + workId + ", delayTime=" + delayTime + "]";
	}

	@Override
	public int compareTo(Delayed arg0) {
		WorkPaidDelayedEvent delay = (WorkPaidDelayedEvent) arg0;
		return (int) (this.workId - delay.getWorkId());
	}
}
