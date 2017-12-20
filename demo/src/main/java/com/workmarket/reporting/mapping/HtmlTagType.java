package com.workmarket.reporting.mapping;

import org.springframework.util.Assert;

public enum HtmlTagType {

	INPUT_TEXT("input_text"),
	SELECT_OPTION("select_option"),
    MULTI_SELECT_OPTION("multi_select_option"),
	DATE("date"),
	DATE_TIME("date_time"),
	TO_FROM_DATES("to_from_dates"),
	NUMERIC("numberic"),
	NUMERIC_RANGE("numberic_range"),
	DISPLAY("display");

	/*
	 * Instance variables
	 */
	private final String type;

	private HtmlTagType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	/**
	 * @param tagType
	 * @return
	 * @throws Exception
	 */
	public static HtmlTagType getTagType(String tagType)throws Exception{
		Assert.notNull(tagType, "tagType object can't be null");

		HtmlTagType htmlTagTypes[] = HtmlTagType.values();
		for(int i = 0; i < htmlTagTypes.length; i++){//relationalOperators isn't null, no need to check.
			if(htmlTagTypes[i].getType().equals(tagType))
				return htmlTagTypes[i];
		}

		throw new Exception("The htmlTagType '" + tagType + "' doesn't return an appropriate HtmlTagType enum.");
	}

}