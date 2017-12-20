package com.workmarket.domains.model.analytics;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.rating.RatingSummary;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.Map;

public abstract class ScoreCard<Enum> implements Serializable {

	protected ScoreCard() {
	}

	private static final long serialVersionUID = 7509018888205236911L;
	private RatingSummary rating;
	private Map<Enum, DateIntervalData> values = Maps.newLinkedHashMap();

	public static class DateIntervalData {
		private Double net30;
		private Double net90;
		private Double all;

		private ScoreCardQualifier net30Score;
		private ScoreCardQualifier net90Score;
		private ScoreCardQualifier allScore;

		public Double getNet30() {
			return net30;
		}

		public DateIntervalData setNet30(Double net30) {
			this.net30 = net30;
			return this;
		}

		public DateIntervalData setNet30(Integer net30) {
			return setNet30(net30.doubleValue());
		}

		public Double getNet90() {
			return net90;
		}

		public DateIntervalData setNet90(Double net90) {
			this.net90 = net90;
			return this;
		}

		public DateIntervalData setNet90(Integer net90) {
			return setNet90(net90.doubleValue());
		}

		public Double getAll() {
			return all;
		}

		public DateIntervalData setAll(Double all) {
			this.all = all;
			return this;
		}

		public DateIntervalData setAll(Integer all) {
			return setAll(all.doubleValue());
		}

		public ScoreCardQualifier getNet30Score() {
			return net30Score;
		}

		public void setNet30Score(ScoreCardQualifier net30Score) {
			this.net30Score = net30Score;
		}

		public ScoreCardQualifier getNet90Score() {
			return net90Score;
		}

		public void setNet90Score(ScoreCardQualifier net90Score) {
			this.net90Score = net90Score;
		}

		public ScoreCardQualifier getAllScore() {
			return allScore;
		}

		public void setAllScore(ScoreCardQualifier allScore) {
			this.allScore = allScore;
		}

		public boolean isBad() {
			// use 90 day score right now
			return net90Score!= null && net90Score.isBad();
		}
	}

	public RatingSummary getRating() {
		return rating;
	}

	public ScoreCard setRating(RatingSummary rating) {
		this.rating = rating;
		return this;
	}

	public Map<Enum, DateIntervalData> getValues() {
		return values;
	}

	public DateIntervalData getValueForField(Enum field) {
		return (DateIntervalData)MapUtils.getObject(values, field, new DateIntervalData());
	}

	public Map<String, DateIntervalData> getValuesWithStringKey() {
		Map<String, DateIntervalData> valuesWithStringKey = Maps.newLinkedHashMap();
		for (Map.Entry<Enum, DateIntervalData> entry : values.entrySet()) {
			valuesWithStringKey.put(entry.getKey().toString(), entry.getValue());
		}
		return valuesWithStringKey;
	}

	public ScoreCard addToValues(Enum key, DateIntervalData value) {
		if (key != null) {
			values.put(key, value);
			decorateScoreCardQualifier(key);
		}
		return this;
	}

	abstract void decorateScoreCardQualifier(Enum scoreField);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ScoreCard)) return false;

		ScoreCard scoreCard = (ScoreCard) o;

		if (!rating.equals(scoreCard.rating)) return false;
		if (!values.equals(scoreCard.values)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (rating == null) ? 1 : rating.hashCode();
		result = 31 * result + ((values == null) ? 1 : values.hashCode());
		return result;
	}
}
