package com.workmarket.domains.work.service.dashboard;

import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.data.solr.model.SolrWorkData;

import java.util.Collection;

/**
 * Author: rocio
 */
public interface WorkDashboardResultParser {

	DashboardResultList parseResult(Collection<? extends SolrWorkData> workList, DashboardResultList resultList);
}
