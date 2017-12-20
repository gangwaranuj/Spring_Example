package com.workmarket.service.business.dto;

import javax.validation.constraints.NotNull;


public class CommentDTO {

	private Long commentId;
	@NotNull
	private String comment;

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

