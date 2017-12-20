package com.workmarket.service.business;

import com.workmarket.data.report.internal.BuyerSummary;
import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;

import java.util.List;

public interface DailySummaryService {

	public DailySummary createNewSummary();

	public DailySummary findSummary(Long id);

	public List<DailySummary> findAllSummaries();

	public List<BuyerSummary> findUniqueBuyersForSummary(Long id);

	public List<Object[]> findRoutedAssignmentsPerCompany(Long id);
	
	public DailySummaryPagination findAllSummaries(DailySummaryPagination pagination); 
}
