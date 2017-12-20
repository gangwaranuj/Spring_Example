package com.workmarket.redis;

import org.springframework.util.Assert;

// Redis keys for HydratorCache
public class RedisCacheFilters {

	public static final String CERTIFICATION_HASH_KEY = "certification:%s";
	public static final String ASSESSMENT_HASH_KEY = "assessment:%s";
	public static final String INSURANCE_HASH_KEY = "insurance:%s";
	public static final String LICENSE_HASH_KEY = "license:%s";
	public static final String HYDRATE_GROUP_HASH_KEY = "hydrateGroup:%s";
	public static final String INDEED_XML_KEY = "indeed-xml-key";

	public static String getCertificationHashKey(Long certificationId) {
		Assert.notNull(certificationId);
		return String.format(CERTIFICATION_HASH_KEY, certificationId);
	}

	public static String getAssessmentHashKey(Long assessmentId) {
		Assert.notNull(assessmentId);
		return String.format(ASSESSMENT_HASH_KEY, assessmentId);
	}

	public static String getLicenseHashKey(Long licenseId) {
		Assert.notNull(licenseId);
		return String.format(LICENSE_HASH_KEY, licenseId);
	}

	public static String getHydrateGroupHashKey(Long groupId) {
		Assert.notNull(groupId);
		return String.format(HYDRATE_GROUP_HASH_KEY, groupId);
	}

	public static String getInsuranceHashKey(Long industryId) {
		return String.format(INSURANCE_HASH_KEY, industryId);
	}
}
