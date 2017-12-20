package com.workmarket.domains.work.service.upload;

import java.util.Calendar;

import org.joda.time.DateTime;

public enum WorkUploadColumnType {
	STRING(String.class),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	FLOAT(Float.class),
	DATETIME(Calendar.class),
	TIME(Long.class), 
	DATE(DateTime.class),
	BOOLEAN(Boolean.class),
	POSITIVE_PRICE(Float.class);
	
	private Class<?> classType;
	
	private WorkUploadColumnType(Class<?> classType) {
		this.classType = classType;
	}
	
	public Class<?> getClassType() {
		return classType;
	}
	
}
