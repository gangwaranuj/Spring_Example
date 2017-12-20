package com.workmarket.service.business.event.work;

import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.ScheduledEvent;

public class WorkClosedEvent extends ScheduledEvent {

	private static final long serialVersionUID = 6413851436430338919L;

	private final long workId;
	private final CloseWorkDTO closeWorkDTO;

	public WorkClosedEvent(CloseWorkDTO closeWorkDTO, long workId) {
		this.closeWorkDTO = closeWorkDTO;
		this.workId = workId;
	}

	public CloseWorkDTO getCloseWorkDTO() {
		return closeWorkDTO;
	}

	public long getWorkId() {
		return workId;
	}
}
