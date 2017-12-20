package com.workmarket.reporting.query;

import java.util.List;

import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.domains.work.model.Work;

public interface QueryBuilder {

	public final static Integer MAX_RESULTS = 1000;
	public final static String DESCENDING = "DESC";
	public final static String ASCENDING = "ASC";
	
	/**
	 * @param reportingContext
	 * @param entityRequestForReport
	 * @return
	 * @throws Exception
	 */
	public List<Work> executeQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception;
}
