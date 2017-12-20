package com.workmarket.service.business.event.forums;

import com.workmarket.domains.forums.model.ForumPost;

public class CreateWorkFromFlaggedPostEvent {
	private ForumPost flaggedPost;

	public CreateWorkFromFlaggedPostEvent(ForumPost post) {
		this.flaggedPost = post;
	}

	public ForumPost getFlaggedPost() {
		return flaggedPost;
	}
}
