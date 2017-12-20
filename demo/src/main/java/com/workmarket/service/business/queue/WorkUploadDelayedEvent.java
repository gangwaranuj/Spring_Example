package com.workmarket.service.business.queue;

import com.workmarket.domains.work.service.audit.WorkActionRequest;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WorkUploadDelayedEvent implements Delayed {

	protected WorkUploadDelayedEvent(List<Long> workIds, WorkActionRequest workActionRequest, long delayTimeMillis) {
		super();
		this.workActionRequest = workActionRequest;
		this.delayTime = delayTimeMillis + System.currentTimeMillis();
		this.workIds = workIds;
	}

	private final WorkActionRequest workActionRequest;
	private final long delayTime;
	private final List<Long> workIds;
	
	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public List<Long> getWorkIds() {
		return workIds;
	}
	
	public WorkActionRequest getWorkActionRequest() {
		return workActionRequest;
	}

	public long getDelayTime() {
		return delayTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (delayTime ^ (delayTime >>> 32));
		result = prime * result + ((workActionRequest == null) ? 0 : workActionRequest.hashCode());
		result = prime * result + ((workIds == null) ? 0 : workIds.hashCode());
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
		WorkUploadDelayedEvent other = (WorkUploadDelayedEvent) obj;
		if (delayTime != other.delayTime)
			return false;
		if (workActionRequest == null) {
			if (other.workActionRequest != null)
				return false;
		} else if (!workActionRequest.equals(other.workActionRequest))
			return false;
		if (workIds == null) {
			if (other.workIds != null)
				return false;
		} else if (!workIds.equals(other.workIds))
			return false;
		return true;
	}

	@Override
	public int compareTo(Delayed arg0) {
		WorkUploadDelayedEvent delay = (WorkUploadDelayedEvent) arg0;
		return this.workIds.hashCode() - delay.getWorkIds().hashCode();
	}
}