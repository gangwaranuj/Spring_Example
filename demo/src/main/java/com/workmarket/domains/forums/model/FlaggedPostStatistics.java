package com.workmarket.domains.forums.model;

import java.util.Calendar;

public class FlaggedPostStatistics {
	private String creatorName;
	private Long creatorId;
	private boolean isCreatorBanned;
	private String comment;
	private Long postId;
	private Long count;
	private Calendar dateReported;
	private Long rootId;

	public String getCreatorName() {
		return creatorName;
	}

	public FlaggedPostStatistics setCreatorName(String creatorName) {
		this.creatorName = creatorName;
		return this;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public FlaggedPostStatistics setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
		return this;
	}

	public FlaggedPostStatistics setIsCreatorBanned(boolean banned) {
		isCreatorBanned = banned;
		return this;
	}

	public boolean isCreatorBanned() { return isCreatorBanned; }

	public String getComment() {
		return comment;
	}

	public FlaggedPostStatistics setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Long getPostId() {
		return postId;
	}

	public Long getCount() {
		return count;
	}

	public Calendar getDateReported() { return dateReported; }

	public Long getRootId() { return this.rootId; }

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}

}
