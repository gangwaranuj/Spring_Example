package com.workmarket.web.forms;

public class MetricsForm {

	private static final String METER_TYPE = "meter";
	private static final String DEFAULT_TYPE = METER_TYPE;
	private static final int DEFAULT_COUNT = 1;

	private String type = DEFAULT_TYPE;
	private String value;
	private int count = DEFAULT_COUNT;

	public MetricsForm() {}

	public String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public final void setCount(int count) {
		this.count = count;
	}

	public boolean isMeter() {
		return this.type.equals(METER_TYPE);
	}

}
