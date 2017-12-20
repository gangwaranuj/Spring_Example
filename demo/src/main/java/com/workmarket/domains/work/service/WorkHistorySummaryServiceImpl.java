package com.workmarket.domains.work.service;

import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.domains.model.summary.work.WorkHistorySummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: alexsilva Date: 3/10/14 Time: 6:28 PM
 */

@Service
public class WorkHistorySummaryServiceImpl implements WorkHistorySummaryService {

	@Autowired private WorkHistorySummaryDAO workHistorySummaryDAO;

	@Override
	public void saveOrUpdate(WorkHistorySummary workHistorySummary) {
		workHistorySummaryDAO.saveOrUpdate(workHistorySummary);
	}
}
