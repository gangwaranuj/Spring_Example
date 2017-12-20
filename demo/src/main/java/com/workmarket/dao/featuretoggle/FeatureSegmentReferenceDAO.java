package com.workmarket.dao.featuretoggle;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.featuretoggle.FeatureSegmentReference;

import java.util.List;

/**
 * User: micah
 * Date: 8/7/13
 * Time: 3:12 PM
 */
public interface FeatureSegmentReferenceDAO extends DAOInterface<FeatureSegmentReference> {
	public void deleteReference(FeatureSegmentReference fsr);
	public FeatureSegmentReference findByFeatureAndSegmentAndReferenceNames(String featureName, String segmentName, String referenceValue);

	List<FeatureSegmentReference> findByFeatureAndSegment(
		String featureName,
		String segmentName);
}
