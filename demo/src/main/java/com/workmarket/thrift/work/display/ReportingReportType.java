package com.workmarket.thrift.work.display;

import com.workmarket.thrift.EnumValue;

public enum ReportingReportType implements EnumValue {
	WORK_ASSIGNMENTS(0),
	WORK_RESOURCES(1),
	WORK_CLIENTS(2),
	WORK_PROJECTS(3),
	WORK_MONEY(4);

	private final int value;

	private ReportingReportType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ReportingReportType findByValue(int value) {
		switch (value) {
			case 0:
				return WORK_ASSIGNMENTS;
			case 1:
				return WORK_RESOURCES;
			case 2:
				return WORK_CLIENTS;
			case 3:
				return WORK_PROJECTS;
			case 4:
				return WORK_MONEY;
			default:
				return null;
		}
	}
}
