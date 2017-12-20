package com.workmarket.redis;

/**
  * Author: rocio
  */
 public class AnalyticsRedisFilter {

	private static final String BUYER_SCORE_CARD_COMPANY_KEY = "buyer_score_card:company:%s";
	private static final String RESOURCE_SCORE_CARD_USER_KEY = "resource_score_card:user:%s";
	private static final String RESOURCE_SCORE_CARD_COMPANY_USER_KEY = "resource_score_card:company:%s:user:%s";
	private static final String RESOURCE_SCORE_CARD_ALL_COMPANY_USER_PATTERN = "resource_score_card:company:*:user:%s";
	private static final String VENDOR_SCORE_CARD_VENDOR_KEY = "vendor_score_card:vendor:%s";
	private static final String VENDOR_SCORE_CARD_COMPANY_VENDOR_KEY = "vendor_score_card:company:%s:vendor:%s";
	private static final String VENDOR_SCORE_CARD_ALL_COMPANY_VENDOR_KEY = "vendor_score_card:company:*:vendor:%s";

	private static final String PERCENTAGE_HIGH_RATING_ALL_COMPANIES = "percentage_high_rating_all_companies";

	public static String getResourceScoreCardUserKey(long userId) {
		return String.format(RESOURCE_SCORE_CARD_USER_KEY, userId);
	}

	public static String getResourceScoreCardCompanyUserKey(long companyId, long userId) {
		return String.format(RESOURCE_SCORE_CARD_COMPANY_USER_KEY, companyId, userId);
	}

	public static String getResourceScoreCardAllCompanyUserPatternUserKey(long userId) {
		return String.format(RESOURCE_SCORE_CARD_ALL_COMPANY_USER_PATTERN, userId);
	}

	public static String getVendorScoreCardVendorKey(long vendorId) {
		return String.format(VENDOR_SCORE_CARD_VENDOR_KEY, vendorId);
	}

	public static String getVendorScoreCardCompanyVendorKey(long companyId, long vendorId) {
		return String.format(VENDOR_SCORE_CARD_COMPANY_VENDOR_KEY, companyId, vendorId);
	}

	public static String getVendorScoreCardAllCompanyVendorKey(long vendorId) {
		return String.format(VENDOR_SCORE_CARD_ALL_COMPANY_VENDOR_KEY, vendorId);
	}

	public static String getBuyerScoreCardCompanyKey(long companyId) {
		return String.format(BUYER_SCORE_CARD_COMPANY_KEY, companyId);
	}

	public static String getPercentageHighRatingAllCompanies() {
		return PERCENTAGE_HIGH_RATING_ALL_COMPANIES;
	}
 }
