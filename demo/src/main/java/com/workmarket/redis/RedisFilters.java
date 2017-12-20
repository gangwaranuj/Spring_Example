package com.workmarket.redis;

import com.workmarket.service.business.dto.WorkResourceDetailPagination;

public class RedisFilters {

	private static final String DASHBOARD_FILTER_KEYS = "user:%s:dashboard_filters"; // THIS NEEDS TO BE PERSISTED IN THE DB
	private static final String SEARCH_FILTERS_KEYS = "user:%s:search_filters"; // THIS NEEDS TO BE PERSISTED IN THE DB
	private static final String NAV_PREFERENCES_KEYS = "user:%s:nav_preferences"; // NOT PERSISTED BUT OK IF LOST
	private static final String SEARCH_PREFERENCES_KEYS = "user:%s:search_preferences"; // NOT PERSISTED BUT OK IF LOST
	private static final String COMPANY_HASH_KEY = "company:%s:searchCache";
	private static final String LABEL_CONFIG_KEYS = "label:%s:company:%s:label_config";
	private static final String RECOMMENDED_ASSESSMENT_KEY = "user:%s:recommended_assessment_ids";
	private static final String WEB_HOOK_SALESFORCE_KEYS = "company:%s:web_hook:salesforce";
	private static final String WEB_HOOK_ERRORS = "web_hook:%s:errors";

	// Session refresh instructions may be lost if these keys are flushed
	private static final String REFRESH_SECURITY_CONTEXT_ALL_KEYS = "refresh_security_context:all";
	private static final String REFRESH_SECURITY_CONTEXT_COMPANY_KEYS = "refresh_security_context:company:%s";
	private static final String REFRESH_SECURITY_CONTEXT_USER_KEYS = "refresh_security_context:user:%s";
	//

	private static final String WORK_SEARCH_REQUEST_KEYS = "work:%s:search_request";
	private static final String USER_NOTIFICATION_DATA_KEY = "user:%s:user_notification:data";
	private static final String USER_NOTIFICATION_UNREAD_INFO = "user:%s:user_notification_uuid:unread";
	private static final String PUBLIC_PAGE_PROFILE_KEYS = "public_page:industry:%s:profile_keys";
	private static final String PUBLIC_PAGE_USER_PROFILE_DATA_KEY = "public_page:user_number:%s:data";
	private static final String USER_BULK_UPLOAD_KEY = "bulk_upload:user:%s:timestamp:%s";
	private static final String USER_BULK_UPLOAD_SIZE_KEY = "bulk_upload_size:user:%s:timestamp:%s";
	private static final String USER_BULK_UPLOAD_PROGRESS_KEY = "bulk_upload_progress:user:%s";
	private static final String USER_BULK_USER_UPLOAD_KEY = "bulk_user_upload:user:%s:uuid:%s";
	private static final String USER_BULK_USER_UPLOAD_SIZE_KEY = "bulk_user_upload_size:user:%s:uuid:%s";
	private static final String USER_BULK_USER_UPLOAD_SUCCESS_COUNTER_KEY = "bulk_user_upload_success:user:%s:uuid:%s";
	private static final String USER_BULK_USER_FAILED_UPLOAD_KEY = "bulk_user_failed_upload:user:%s:uuid:%s";
	private static final String USER_BULK_USER_UPLOAD_IN_PROGRESS_KEYS = "bulk_user_upload_in_progress_key:all";
	private static final String WORK_RESOURCES_DETAIL_DATA_KEY = "work:work_resource_detail:%s:%s:%s:%s:%s:%s:%s:%s:%s";
	private static final String WORK_RESOURCES_DETAIL_KEY_PATTERN = "work:work_resource_detail:%s*";
	private static final String PART_WITH_TRACKING_KEY = "part_with_tracking:id:%s:data";
	private static final String WORK_FOLLOWERS_KEY = "work:id:%s:followers";
	private static final String TWILIO_SOURCE_NUMBERS_KEY = "twilio:source_numbers";
	private static final String EXPERIMENTS_PERCENTAGE_KEY = "experiments:percentage:%s";
	private static final String WEBPACK_MANIFEST_KEY = "webpack:manifest";
	private static final String WEBPACK_MANIFEST_HASH_KEY = "webpack:manifest_hash";

	public static String dashboardFilterKeyFor(Long userId) {
		return String.format(DASHBOARD_FILTER_KEYS, userId);
	}

	public static String navPreferencesKeyFor(Long userId) {
		return String.format(NAV_PREFERENCES_KEYS, userId);
	}

	public static String searchFilterKeysFor(Long userId) { return String.format(SEARCH_FILTERS_KEYS, userId); }

	public static String searchPreferencesKeysFor(Long userId) { return String.format(SEARCH_PREFERENCES_KEYS, userId); }

	public static String labelConfigKeyFor(long labelId, long companyId) {
		return String.format(LABEL_CONFIG_KEYS, labelId, companyId);
	}

	public static String webHookSalesforceKeyFor(long companyId) {
		return String.format(WEB_HOOK_SALESFORCE_KEYS, companyId);
	}

	public static String webHookErrorsFor(long webHookId) {
		return String.format(WEB_HOOK_ERRORS, webHookId);
	}

	public static String refreshSecurityContextKeyForAll() {
		return REFRESH_SECURITY_CONTEXT_ALL_KEYS;
	}

	public static String refreshSecurityContextKeyForCompany(long companyId) {
		return String.format(REFRESH_SECURITY_CONTEXT_COMPANY_KEYS, companyId);
	}

	public static String refreshSecurityContextKeyForUser(long userId) {
		return String.format(REFRESH_SECURITY_CONTEXT_USER_KEYS, userId);
	}

	public static String workSearchKeyFor(long userId) {
		return String.format(WORK_SEARCH_REQUEST_KEYS, userId);
	}

	public static String companyHashKeyFor(long companyId) {
		return String.format(COMPANY_HASH_KEY, companyId);
	}

	public static String userNotificationDataKey(long userId) {
		return String.format(USER_NOTIFICATION_DATA_KEY, userId);
	}

	public static String getUnreadNotificationsInfo(long userId) {
		return String.format(USER_NOTIFICATION_UNREAD_INFO, userId);
	}

	public static String publicPageProfileIndustryKey(String industry) {
		return String.format(PUBLIC_PAGE_PROFILE_KEYS, industry);
	}

	public static String publicPageProfileData(String userNumber) {
		return String.format(PUBLIC_PAGE_USER_PROFILE_DATA_KEY, userNumber);
	}

	public static String getWorkResourcesDetailDataKey(long workId, WorkResourceDetailPagination pagination) {
		return String.format(
			WORK_RESOURCES_DETAIL_DATA_KEY,
			workId,
			pagination.getSortColumn(),
			pagination.getSortDirection().toString(),
			pagination.getStartRow(),
			pagination.getResultsLimit(),
			pagination.isIncludeApplyNegotiation(),
			pagination.isIncludeLabels(),
			pagination.isIncludeNotes(),
			pagination.getFilter(WorkResourceDetailPagination.FILTER_KEYS.WORK_RESOURCE_COMPANY_ID)
		);
	}

	public static String getWorkResourcesDetailKeyPattern(long workId) {
		return String.format(WORK_RESOURCES_DETAIL_KEY_PATTERN, workId);
	}

	public static String userBulkUploadKey(long userId, long time) {
		return String.format(USER_BULK_UPLOAD_KEY, userId, time);
	}

	public static String userBulkUploadSizeKey(long userId, long time) {
		return String.format(USER_BULK_UPLOAD_SIZE_KEY, userId, time);
	}

	public static String userBulkUploadProgressKey(long userId) {
		return String.format(USER_BULK_UPLOAD_PROGRESS_KEY, userId);
	}

	public static String userBulkUserUploadKey(long userId, String uuid) {
		return String.format(USER_BULK_USER_UPLOAD_KEY, userId, uuid);
	}

	public static String userBulkUserUploadSizeKey(long userId, String uuid) {
		return String.format(USER_BULK_USER_UPLOAD_SIZE_KEY, userId, uuid);
	}

	public static String userBulkUserUploadSuccessCounterKey(long userId, String uuid) {
		return String.format(USER_BULK_USER_UPLOAD_SUCCESS_COUNTER_KEY, userId, uuid);
	}

	public static String userBulkUserUploadAllInProgressKey() {
		return USER_BULK_USER_UPLOAD_IN_PROGRESS_KEYS;
	}

	public static String userBulkUserFailedUploadKey(long userId, String uuid) {
		return String.format(USER_BULK_USER_FAILED_UPLOAD_KEY, userId, uuid);
	}

	public static String recommendedAssessmentKeyFor(long userId) {
		return String.format(RECOMMENDED_ASSESSMENT_KEY, userId);
	}

	public static String partKey(String partId) {
		return String.format(PART_WITH_TRACKING_KEY, partId);
	}

	public static String followersKey(long workId) {
		return String.format(WORK_FOLLOWERS_KEY, workId);
	}

	public static String twilioSourceNumbersKey() {
		return TWILIO_SOURCE_NUMBERS_KEY;
	}

	public static String experimentPercentageKey(final String subKey) {
		return String.format(EXPERIMENTS_PERCENTAGE_KEY, subKey);
	}

	public static String webpackManifestKey() {
		return WEBPACK_MANIFEST_KEY;
	}

	public static String webpackManifestHashKey() {
		return WEBPACK_MANIFEST_HASH_KEY;
	}
}
