package com.workmarket.dao.featuretoggle;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.featuretoggle.FeatureSegment;

/**
 * User: micah
 * Date: 8/7/13
 * Time: 1:37 PM
 */
public interface FeatureSegmentDAO extends DAOInterface<FeatureSegment> {
	public void deleteSegment(FeatureSegment fs);
	public FeatureSegment findByFeatureAndSegmentNames(String featureName, String segmentName);
}
