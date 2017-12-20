package com.workmarket.service.business.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workmarket.service.business.DailySummaryService;


@Service
public class DailySummaryExecutor implements ScheduledExecutor {

	@Autowired private DailySummaryService dailySummaryService;
	
	private static final Log logger = LogFactory.getLog(DailySummaryExecutor.class);
	

	@Override
	public void execute(){
		logger.debug("[dailySummary] Creating new summary");
		dailySummaryService.createNewSummary();
	}
	
	
}
