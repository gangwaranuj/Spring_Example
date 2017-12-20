package com.workmarket.web.helpers;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class RatingStarsHelper {

	public static final SortedMap<Integer, String> STAR_VALUES;
	static {
		STAR_VALUES = new TreeMap<Integer, String>() {{
			put(20, "Very Dissatisfied");
			put(40, "Dissatisfied");
			put(60, "Satisfied");
			put(80, "Very Satisfied");
			put(100, "Completely Satisfied");
		}};
	}
	
	public static String convertScaledRatingValueToText(int value) {
		switch (value) {
			case 5: return "five";
			case 4: return "four";
			case 3: return "three";
			case 2: return "two";
			case 1: return "one";
			default: return "zero";
		}
	}

	public static String convertRatingValueToText(int value) {
		return convertScaledRatingValueToText(Math.round(value / 20));
	}

	/**
	 * Convert integer to star images.
	 * @param rating is star rating
	 * @return HTML stars
	 */
	public static String getStars(Integer rating) {
		if (rating == null) return null;
		return getStars(rating.doubleValue());
	}

	public static String getStars(Double rating) {
		if (rating == null) return null;
		return getStars(rating.doubleValue());
	}

	public static String getStars(double rating) {
		return String.format("<div class='star-rating static' title='%2.1f stars'><div class='star-rating-value' style='width:%d%%'></div></div>", rating / 20D, (int)rating);
	}

	public static String getLevels(int value) {
		switch (value) {
			case 3: return "Excellent";
			case 2: return "Satisfied";
			case 1: return "Not Satisfied";
			default: return "Not applicable";
		}
	}

	public static String getRatingCode(int value) {
		switch (value) {
			case 3: return "excellent";
			case 2: return "satisfied";
			case 1: return "not-satisfied";
			default: return "not-applicable";
		}
	}

	public static int convertRatingNumberToNumeric(double rating) {
		return (int)(rating / 20D);
	}
	
	public static java.lang.String getRatingValueFromShort(java.lang.Short num) {
		Integer key = new Integer(num);
		
		if (RatingStarsHelper.STAR_VALUES.get(key) != null){
			return STAR_VALUES.get(key);
		}
		
		return StringUtils.EMPTY;
	}
}
