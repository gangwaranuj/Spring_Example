package com.workmarket.domains.forums.model;

import java.util.List;

public class ForumPostStatistics {
	private String title;
	private Long commentCount;
	private String lastComment;
	private String lastCommentDate;
	private Long lastCommentId;
	private List<String> tags;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public String getLastComment() {
		return lastComment;
	}

	public void setLastComment(String lastComment) {
		this.lastComment = lastComment;
	}

	public String getLastCommentDate() {
		return lastCommentDate;
	}

	public void setLastCommentDate(String lastCommentDate) {
		this.lastCommentDate = lastCommentDate;
	}

	public Long getLastCommentId() {
		return lastCommentId;
	}

	public void setLastCommentId(Long lastCommentId) {
		this.lastCommentId = lastCommentId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
