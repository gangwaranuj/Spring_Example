package com.workmarket.domains.model.rating;

import java.io.Serializable;

public class RatingSummary implements Serializable {

	private static final long serialVersionUID = -1368854167489321587L;
	private Double satisfactionRate;
	private Long count;
	private Double quality;
	private Double professionalism;
	private Double communication;


	public Double getSatisfactionRate() {
		return satisfactionRate;
	}

	public RatingSummary setSatisfactionRate(Double satisfactionRate) {
		this.satisfactionRate = satisfactionRate;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public Double getQuality() {
		return quality;
	}

	public void setQuality(Double quality) {
		this.quality = quality;
	}

	public Double getProfessionalism() {
		return professionalism;
	}

	public void setProfessionalism(Double professionalism) {
		this.professionalism = professionalism;
	}

	public Double getCommunication() {
		return communication;
	}

	public void setCommunication(Double communication) {
		this.communication = communication;
	}

	public RatingSummary setCount(Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RatingSummary)) return false;

		RatingSummary that = (RatingSummary) o;

		if (!satisfactionRate.equals(that.satisfactionRate)) { return false; }
		if (!count.equals(that.count)) { return false; }
		if (!quality.equals(that.quality)) { return false; }
		if (!professionalism.equals(that.professionalism)) { return false; }
		if (!communication.equals(that.communication)) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		int result = (satisfactionRate == null) ? 1 : satisfactionRate.hashCode();
		result = 31 * result + ((count == null) ? 1 : count.hashCode());
		return result;
	}
}
