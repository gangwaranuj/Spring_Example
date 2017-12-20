package com.workmarket.domains.model.analytics;

/**
 * Author: rocio
 */
public class ResourceScoreCard extends ScoreCard<ResourceScoreField> {

	private static final long serialVersionUID = -1056932147871914992L;

	public ResourceScoreCard() {
		super();
	}

	@Override
	void decorateScoreCardQualifier(ResourceScoreField scoreField) {
		return;
	}
}
