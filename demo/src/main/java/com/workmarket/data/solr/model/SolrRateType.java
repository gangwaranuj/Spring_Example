package com.workmarket.data.solr.model;

public class SolrRateType {
	private double minOnsiteHourlyRate;
	private double minOnsitePrice;
	private double minOffsiteHourlyRate;
	private double minOffsitePrice;
	public double getMinOnsiteHourlyRate() {
		return minOnsiteHourlyRate;
	}
	public void setMinOnsiteHourlyRate(double minOnsiteHourlyRate) {
		this.minOnsiteHourlyRate = minOnsiteHourlyRate;
	}
	public double getMinOnsitePrice() {
		return minOnsitePrice;
	}
	public void setMinOnsitePrice(double minOnsitePrice) {
		this.minOnsitePrice = minOnsitePrice;
	}
	public double getMinOffsiteHourlyRate() {
		return minOffsiteHourlyRate;
	}
	public void setMinOffsiteHourlyRate(double minOffsiteHourlyRate) {
		this.minOffsiteHourlyRate = minOffsiteHourlyRate;
	}
	public double getMinOffsitePrice() {
		return minOffsitePrice;
	}
	public void setMinOffsitePrice(double minOffsitePrice) {
		this.minOffsitePrice = minOffsitePrice;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(minOffsiteHourlyRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minOffsitePrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minOnsiteHourlyRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minOnsitePrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrRateType other = (SolrRateType) obj;
		if (Double.doubleToLongBits(minOffsiteHourlyRate) != Double
				.doubleToLongBits(other.minOffsiteHourlyRate))
			return false;
		if (Double.doubleToLongBits(minOffsitePrice) != Double
				.doubleToLongBits(other.minOffsitePrice))
			return false;
		if (Double.doubleToLongBits(minOnsiteHourlyRate) != Double
				.doubleToLongBits(other.minOnsiteHourlyRate))
			return false;
		if (Double.doubleToLongBits(minOnsitePrice) != Double
				.doubleToLongBits(other.minOnsitePrice))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrRateType [minOnsiteHourlyRate=" + minOnsiteHourlyRate
				+ ", minOnsitePrice=" + minOnsitePrice
				+ ", minOffsiteHourlyRate=" + minOffsiteHourlyRate
				+ ", minOffsitePrice=" + minOffsitePrice + "]";
	}
	
}
