package com.workmarket.domains.model.analytics;


public class VendorScoreCard extends ScoreCard<ResourceScoreField> {
	private static final long serialVersionUID = 5843520149544033866L;

	public VendorScoreCard() {
		super();
	}

	@Override
	void decorateScoreCardQualifier(ResourceScoreField scoreField) {
		return;
	}
}
