package com.workmarket.search.request.user;

public enum PeopleSearchSortByType {
	DISTANCE,
	NAME,
	RATING,
	HOURLY_RATE,
	LANE,
	RELEVANCY,
	WORK_COMPLETED,
	WORK_CANCELLED,
	CREATED_ON;

	public static PeopleSearchSortByType findByName(String name) {
		switch (name) {
			case "distance":
				return DISTANCE;
			case "name":
				return NAME;
			case "rating":
				return RATING;
			case "hourlyRate":
				return HOURLY_RATE;
			case "lane":
				return LANE;
			case "relevancy":
				return RELEVANCY;
			case "workCompleted":
				return WORK_COMPLETED;
			case "workCancelled":
				return WORK_CANCELLED;
			case "createdOn":
				return CREATED_ON;
			default:
				return null;
		}
	}
}
