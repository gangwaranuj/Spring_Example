package com.workmarket.domains.model.analytics;

import org.springframework.util.Assert;

import java.util.Map;

/**
 * Author: rocio
 */
public class BuyerScoreCard extends ScoreCard<BuyerScoreField> {

	private static final long serialVersionUID = -1652508741472989662L;

	public BuyerScoreCard() {
		super();
	}

	private CompanyStatsCard companyStatsCard;

	public CompanyStatsCard getCompanyStatsCard() {
		return companyStatsCard;
	}

	public void setCompanyStatsCard(CompanyStatsCard companyStatsCard) {
		this.companyStatsCard = companyStatsCard;
	}

	@Override
	void decorateScoreCardQualifier(BuyerScoreField scoreField) {
		Assert.notNull(scoreField);
		DateIntervalData data = this.getValueForField(scoreField);
		if (data != null) {
			data.setNet30Score(getScoreCardQualifier(scoreField, data.getNet30()));
			data.setNet90Score(getScoreCardQualifier(scoreField, data.getNet90()));
			data.setAllScore(getScoreCardQualifier(scoreField, data.getAll()));
		}
	}

	private ScoreCardQualifier getScoreCardQualifier(BuyerScoreField scoreField, Double value) {
		if (value == null || scoreField == null) {
			return null;
		}
		switch (scoreField) {
			case AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS:
				if (value <= 5) {
					return ScoreCardQualifier.GOOD;
				} else if (value <= 10) {
					return ScoreCardQualifier.NEUTRAL;
				}
				return ScoreCardQualifier.BAD;

			case AVERAGE_TIME_TO_PAY_WORK_IN_DAYS:
				if (value < 0) {
					return ScoreCardQualifier.GOOD;
				} else if (value < 1) {
					return ScoreCardQualifier.NEUTRAL;
				}
				return ScoreCardQualifier.BAD;

			case PERCENTAGE_RATINGS_OVER_4_STARS:
				if (value > 90) {
					return ScoreCardQualifier.GOOD;
				} else if (value >= 75 || value == 0) {
					return ScoreCardQualifier.NEUTRAL;
				}
				return ScoreCardQualifier.BAD;

			default:
				return ScoreCardQualifier.NEUTRAL;
		}
	}

	public boolean hasBadScore() {
		for (Map.Entry<BuyerScoreField, DateIntervalData> entry : getValues().entrySet()) {
			// Do not penalize on approval time per discussion
			if (!BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS.equals(entry.getKey()) && entry.getValue().getNet90Score().isBad()) {
				return true;
			}
		}
		return companyStatsCard != null && companyStatsCard.isBad();
	}
}