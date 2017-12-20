package com.workmarket.domains.forums.model;

import com.google.common.collect.Lists;

import java.util.EnumSet;
import java.util.List;

public enum ForumTag {
	ASSIGNMENTS("Assignments"),
	PROFILE("Profile"),
	NOTIFICATIONS("Notifications"),
	GROUPS("Groups"),
	DASHBOARD("Dashboard"),
	CONTACT_MANAGER("Contact Manager"),
	PROJECTS("Projects"),
	TESTS_SURVEYS("Tests/Surveys"),
	PAYMENTS("Payments"),
	REPORTS("Reports"),
	WORK_FEED("Work Feed"),
	RATINGS("Ratings"),
	MOBILE("Mobile");

	private String displayString;
	public static final String FORUM_TAG_COLUMN = "GROUP_CONCAT(tag SEPARATOR ',')";

	ForumTag(String displayString) {
		this.displayString = displayString;
	}

	@Override
	public String toString() {
		return this.displayString;
	}

	public String getDisplayString() {
		return this.displayString;
	}

	public static List<String> getForumListTags() {
		List<String> tags = Lists.newArrayList();
		for (ForumTag tag : EnumSet.allOf(ForumTag.class)) {
			tags.add(tag.getDisplayString());
		}
		return tags;
	}

	public static ForumTag getTagByName(String name) {
		ForumTag returnTag = null;
		for (ForumTag tag : ForumTag.class.getEnumConstants()) {
			if (name.equals(tag.getDisplayString())) {
				returnTag = tag;
			}
		}
		return returnTag;
	}

}
