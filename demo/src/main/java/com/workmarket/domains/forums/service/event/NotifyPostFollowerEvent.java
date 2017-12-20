package com.workmarket.domains.forums.service.event;

import com.workmarket.domains.forums.model.ForumPost;

public class NotifyPostFollowerEvent {
	private ForumPost post;

	public NotifyPostFollowerEvent(ForumPost post) {
		this.post = post;
	}

	public ForumPost getPost() {
		return post;
	}
}
