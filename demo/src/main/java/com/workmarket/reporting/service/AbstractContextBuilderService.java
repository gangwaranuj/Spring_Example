package com.workmarket.reporting.service;


import com.workmarket.domains.model.reporting.ReportingContext;

public abstract class AbstractContextBuilderService{

	/*
	 * Instance variables and constants
	 */	
	private ReportingContext reportingContext;

	/**
	 * @param <T>
	 * @return
	 */
	public abstract <T extends ReportingContext> T build();


	/**
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings(value = { "unchecked" })
	public <T extends ReportingContext> T getReportingContext() {
		return (T)reportingContext;
	}

	/**
	 * @param <T>
	 * @param context
	 */
	public <T extends ReportingContext> void setReportingContext(T context) {
		this.reportingContext = context;
	}

}
