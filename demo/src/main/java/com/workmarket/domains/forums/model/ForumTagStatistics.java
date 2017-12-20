package com.workmarket.domains.forums.model;

import com.google.common.collect.Ordering;

public class ForumTagStatistics {
	private Long postCount;
	private String tag;

	public Long getPostCount() {
		return postCount;
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	public String getTag() { return tag; }

	public void setTag(String tag) { this.tag = tag; }

	public static final Ordering<ForumTagStatistics> POPULARITY_ORDER = new Ordering<ForumTagStatistics>() {
		@Override
		public int compare(ForumTagStatistics tag1, ForumTagStatistics tag2) {
			return tag2.getPostCount().compareTo(tag1.getPostCount());
		}
	};
}
