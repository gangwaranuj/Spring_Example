package com.workmarket.dao.report.internal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;


public interface DailySummaryDAO extends DAOInterface<DailySummary>{
      
	List<DailySummary> findAllSummaries();
	Integer countNewUsers(Calendar start, Calendar end);
	Integer countDrugTests(Calendar start, Calendar end);
	Integer countBackgroundChecks(Calendar start, Calendar end);
	Integer countPublicGroups();
	Integer countInviteOnlyGroups();
	Integer countPrivateGroups();
	Integer countCampaigns(Calendar start, Calendar end);
	Integer countInvitations(Calendar start, Calendar end);
	Integer countNewBuyers(Calendar start, Calendar end);
	BigDecimal calculateTermsExpired();
	BigDecimal calculateTermsOverdue();
	DailySummaryPagination findAllSummaries(DailySummaryPagination pagination);
}
