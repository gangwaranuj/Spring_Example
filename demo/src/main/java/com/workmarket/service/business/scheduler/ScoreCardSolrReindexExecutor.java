package com.workmarket.service.business.scheduler;


import ch.lambdaj.function.convert.PropertyExtractor;
import com.workmarket.data.solr.configuration.UserIndexerConfiguration;
import com.workmarket.data.solr.indexer.user.SolrVendorIndexer;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.summary.user.UserSummary;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.summary.SummaryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Service
@ManagedResource(objectName="bean:name=scoreCardReindex", description="ScoreCard Reindex")
public class ScoreCardSolrReindexExecutor implements ScheduledExecutor {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private SummaryService summaryService;
	@Autowired private CompanyService companyService;
	@Autowired private SolrVendorIndexer vendorIndexer;

	private static final Log logger = LogFactory.getLog(ScoreCardSolrReindexExecutor.class);

	@Override
	@ManagedOperation(description = "Scorecared Reindex")
	public void execute() {
			logger.debug("ScoreCardSolrReindexExecutor: start");
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		Calendar onTimeThresholdDate = UserIndexerConfiguration.getOnTimePercentageThresholdDate();
		Calendar deliverableOnTimeThresholdDate = UserIndexerConfiguration.getDeliverableOnTimePercentageThresholdDate();
		Calendar earliestThresholdDate = onTimeThresholdDate.before(deliverableOnTimeThresholdDate) ? onTimeThresholdDate : deliverableOnTimeThresholdDate;

		List<UserSummary> users = summaryService.findAllUsersWithLastAssignedDateBetweenDates(earliestThresholdDate, Calendar.getInstance());

		if (CollectionUtils.isNotEmpty(users)) {
			userIndexer.reindexById(convert(users, new PropertyExtractor("userId")));

			// now update the companies
			List<Long> companyIds = companyService.findCompanyIdsForUsers(convert(users, new PropertyExtractor("userId")));
			vendorIndexer.reindexById(companyIds);

		}

		logger.debug("ScoreCardSolrReindexExecutor: done");
	}
}
