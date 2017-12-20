package com.workmarket.reporting.mapping;

import org.springframework.util.Assert;

public enum FilteringType {

	TEXT("text"),
	DATE_TIME("date_time"),
	NUMERIC("numeric"),
	BIG_DECIMAL("big_decimal"),
	DATE_RANGE("date_range"),
	DATE_BEFORE("date_before"),
	DATE_AFTER("date_after"),
	NEXT_1_DAY("next_1_day"),
	NEXT_7_DAYS("next_7_days"),
	LAST_1_DAY("last_1_day"),
	LAST_7_DAYS("last_7_days"),
	LAST_30_DAYS("last_30_days"),
	LAST_60_DAYS("last_60_days"),
	LAST_90_DAYS("last_90_days"),
	LAST_365_DAYS("last_365_days"),
	THIS_YEAR_TO_DATE("this_year_to_date"),
	LAST_YEAR_ONLY("last_year_only"),
	FIELD_VALUE("field_value"),
	CONTAINS("contains"),
	DISPLAY("display"),
	PLEASE_SELECT("please_select"),
	NEXT_30_DAYS("next_30_days"),
	NEXT_60_DAYS("next_60_days"),
	NEXT_90_DAYS("next_90_days");

	/*
	 * Instance variables
	 */
	private final String type;

	private FilteringType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	/**
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static FilteringType getInput(String type)throws Exception{
		Assert.notNull(type, "type object can't be null");

		FilteringType filteringTypes[] = FilteringType.values();
		for(int i = 0; i < filteringTypes.length; i++){//relationalOperators isn't null, no need to check.
			if(filteringTypes[i].getType().equals(type))
				return filteringTypes[i];
		}

		throw new Exception("The filteringType '" + type + "' doesn't return an appropriate FilteringType enum.");
	}

}


