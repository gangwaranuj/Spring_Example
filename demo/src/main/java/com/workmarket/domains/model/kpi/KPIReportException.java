package com.workmarket.domains.model.kpi;

import java.util.List;

public class KPIReportException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<KPIReportError> errors;

	public KPIReportException() {
	}

	public KPIReportException(String why, List<KPIReportError> errors) {
		super(why);
		this.errors = errors;
	}

	public String getWhy() {
		return super.getMessage();
	}

	public List<KPIReportError> getErrors() {
		return errors;
	}
}