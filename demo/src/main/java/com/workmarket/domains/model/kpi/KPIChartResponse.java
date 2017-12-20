package com.workmarket.domains.model.kpi;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KPIChartResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String xAxisLabel;
	private String yAxisLabel;
	private List<DataPoint> chartData;

	public KPIChartResponse() {
	}

	public KPIChartResponse(String xAxisLabel, String yAxisLabel, List<DataPoint> chartData) {
		this();
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.chartData = chartData;
	}

	public String getXAxisLabel() {
		return this.xAxisLabel;
	}

	public KPIChartResponse setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
		return this;
	}

	public boolean isSetXAxisLabel() {
		return this.xAxisLabel != null;
	}

	public String getYAxisLabel() {
		return this.yAxisLabel;
	}

	public KPIChartResponse setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
		return this;
	}

	public boolean isSetYAxisLabel() {
		return this.yAxisLabel != null;
	}

	public int getChartDataSize() {
		return (this.chartData == null) ? 0 : this.chartData.size();
	}

	public java.util.Iterator<DataPoint> getChartDataIterator() {
		return (this.chartData == null) ? null : this.chartData.iterator();
	}

	public void addToChartData(DataPoint elem) {
		if (this.chartData == null) {
			this.chartData = new ArrayList<>();
		}
		this.chartData.add(elem);
	}

	public List<DataPoint> getChartData() {
		return this.chartData;
	}

	public KPIChartResponse setChartData(List<DataPoint> chartData) {
		this.chartData = chartData;
		return this;
	}

	public boolean isSetChartData() {
		return this.chartData != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof KPIChartResponse)
			return this.equals((KPIChartResponse) that);
		return false;
	}

	private boolean equals(KPIChartResponse that) {
		if (that == null)
			return false;

		boolean this_present_xAxisLabel = true && this.isSetXAxisLabel();
		boolean that_present_xAxisLabel = true && that.isSetXAxisLabel();
		if (this_present_xAxisLabel || that_present_xAxisLabel) {
			if (!(this_present_xAxisLabel && that_present_xAxisLabel))
				return false;
			if (!this.xAxisLabel.equals(that.xAxisLabel))
				return false;
		}

		boolean this_present_yAxisLabel = true && this.isSetYAxisLabel();
		boolean that_present_yAxisLabel = true && that.isSetYAxisLabel();
		if (this_present_yAxisLabel || that_present_yAxisLabel) {
			if (!(this_present_yAxisLabel && that_present_yAxisLabel))
				return false;
			if (!this.yAxisLabel.equals(that.yAxisLabel))
				return false;
		}

		boolean this_present_chartData = true && this.isSetChartData();
		boolean that_present_chartData = true && that.isSetChartData();
		if (this_present_chartData || that_present_chartData) {
			if (!(this_present_chartData && that_present_chartData))
				return false;
			if (!this.chartData.equals(that.chartData))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_xAxisLabel = true && (isSetXAxisLabel());
		builder.append(present_xAxisLabel);
		if (present_xAxisLabel)
			builder.append(xAxisLabel);

		boolean present_yAxisLabel = true && (isSetYAxisLabel());
		builder.append(present_yAxisLabel);
		if (present_yAxisLabel)
			builder.append(yAxisLabel);

		boolean present_chartData = true && (isSetChartData());
		builder.append(present_chartData);
		if (present_chartData)
			builder.append(chartData);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("KPIChartResponse(");
		boolean first = true;

		sb.append("xAxisLabel:");
		if (this.xAxisLabel == null) {
			sb.append("null");
		} else {
			sb.append(this.xAxisLabel);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("yAxisLabel:");
		if (this.yAxisLabel == null) {
			sb.append("null");
		} else {
			sb.append(this.yAxisLabel);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("chartData:");
		if (this.chartData == null) {
			sb.append("null");
		} else {
			sb.append(this.chartData);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

