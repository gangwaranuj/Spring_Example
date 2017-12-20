package com.workmarket.service.business.event.user;

import com.workmarket.service.business.event.Event;

public class UserAverageRatingEvent extends Event {

	private static final long serialVersionUID = 706611535767651910L;

	private final long ratedUserId;
	private final long raterCompanyId;

	public UserAverageRatingEvent(final long ratedUserId, final long raterCompanyId) {
		this.ratedUserId = ratedUserId;
		this.raterCompanyId = raterCompanyId;
	}

	public long getRaterCompanyId() {
		return raterCompanyId;
	}

	public long getRatedUserId() {
		return ratedUserId;
	}
}
