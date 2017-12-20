package com.workmarket.domains.forums.model;

public class ForumCategoryStatistics {
	private Long postCount;
	private Long commentCount;
	private String lastPostTitle;
	private String lastPostDate;
	private Long lastPostId;

	public Long getPostCount() {
		return postCount;
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public String getLastPostTitle() {
		return lastPostTitle;
	}

	public void setLastPostTitle(String lastPostTitle) {
		this.lastPostTitle = lastPostTitle;
	}

	public String getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(String lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public Long getLastPostId() {
		return lastPostId;
	}

	public void setLastPostId(Long lastPostId) {
		this.lastPostId = lastPostId;
	}
}
