package com.workmarket.service.business.dto;

import javax.validation.constraints.NotNull;

public class UserCommentDTO extends CommentDTO {
	@NotNull
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}

