package com.workmarket.service.business.event.user;

import com.workmarket.service.business.event.Event;
import java.util.List;

public class BadActorEvent extends Event {

	private static final long serialVersionUID = 3309999726535681818L;

	private final Long blockedUserId;
	private final List<Long> blockingCompanyIds;

	public BadActorEvent(Long blockedUserId, List<Long> blockingCompanyIds) {
		this.blockedUserId = blockedUserId;
		this.blockingCompanyIds = blockingCompanyIds;
	}

	public Long getBlockedUserId() {
		return blockedUserId;
	}

	public List<Long> getBlockingCompanyIds() {
		return blockingCompanyIds;
	}
}
