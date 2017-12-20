package com.workmarket.data.aggregate;

/**
 * User: micah
 * Date: 7/16/13
 * Time: 11:26 PM
 */
public class FeatureAggregate {
	private String featureName;
	private boolean isAllowed;
	private String segmentName;
	private String referenceValue;

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public boolean isAllowed() {
		return isAllowed;
	}

	public void setAllowed(boolean allowed) {
		isAllowed = allowed;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}
}
