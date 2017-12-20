package com.workmarket.domains.model.analytics;

/**
 * Author: rocio
 */
public enum ScoreCardQualifier {

	GOOD,
	NEUTRAL,
	BAD;

	public boolean isGood() {
		return GOOD.equals(this);
	}

	public boolean isOkay() {
		return NEUTRAL.equals(this);
	}

	public boolean isBad() {
		return BAD.equals(this);
	}

}
