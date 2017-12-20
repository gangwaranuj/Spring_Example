package com.workmarket.web.helpers;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public final class ActionGroupsHelper {
	private ActionGroupsHelper() {}

	public static final Map<String, String> groupObjectiveOptions = ImmutableMap.of(
					"work", "Assignments",
					"social", "Networking / Social",
					"professional", "Professional Association"
	);

	public static final Map<String, String> sortOptions = ImmutableMap.of(
			"default", "Sort by Relevancy",
			"name", "Sort by Name",
			"created_on", "Sort by Create Date",
			"member_count", "Sort by Member Count"
	);

}
