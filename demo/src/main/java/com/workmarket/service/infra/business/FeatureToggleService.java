package com.workmarket.service.infra.business;

import com.workmarket.service.helpers.ServiceResponseBuilder;

import java.util.List;

/**
 * User: micah
 * Date: 8/5/13
 * Time: 1:49 AM
 */
public interface FeatureToggleService {
	ServiceResponseBuilder addFeature(String featureName, Boolean isAllowed);
	ServiceResponseBuilder addFeature(String featureName, Boolean isAllowed, String segmentName, String referenceValue);

	ServiceResponseBuilder addSegment(String featureName, String segmentName, String referenceValue);

	ServiceResponseBuilder removeFeature(String featureName);
	ServiceResponseBuilder removeSegment(String featureName, String segmentName);
	ServiceResponseBuilder removeReferenceValue(String featureName, String segmentName, String referenceValue);

	ServiceResponseBuilder updateFeature(String featureName, Boolean isAllowed);

	ServiceResponseBuilder getFeature(String featureName);
	ServiceResponseBuilder getAllFeatures();

	List<Long> getCompaniesWithFeature(String featureName);
}
