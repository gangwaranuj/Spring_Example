package com.workmarket.domains.model.validation;

public class ConstraintViolation {
	private final String key;
	private final Object[] params;

	public ConstraintViolation(String key, Object... params) {
		this.key = key;
		this.params = params;
	}

	public String getKey() {
		return key;
	}

	public Object[] getParams() {
		return params;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("{key:").append(key).append(", values:[");
		for (Object p : params) {
			buffer.append(p).append(",");
		}
		buffer.append("]}");
		
		return buffer.toString();
	}
}