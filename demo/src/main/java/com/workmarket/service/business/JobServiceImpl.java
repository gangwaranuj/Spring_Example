package com.workmarket.service.business;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.service.search.SearchService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JobServiceImpl implements JobService {
	private static final Log logger = LogFactory.getLog(JobServiceImpl.class);

	@Autowired private LaneService laneService;
	@Autowired private SearchService searchService;
	@Autowired private WorkIndexer workIndexer;

	@Override
	public int processPendingLaneRemovals() throws Exception {
		if (logger.isInfoEnabled())
			logger.debug("@@@@@@@@@@@@@@@@@@@@ processPendingLaneRemovals BEGIN @@@@@@@@@@@@@@@@@@@@");

		int rowCount = 0;
		List<LaneAssociation> associations = laneService.findAllAssociationsWithApprovalStatus(ApprovalStatus.PENDING_REMOVAL);

		for (LaneAssociation association : associations) {
			if (logger.isInfoEnabled())
				logger.debug("Removing user id " + association.getUser().getId() + " from lane " + association.getLaneType() + " of company id " + association.getCompany().getId());

			laneService.removeUserFromCompanyLane(association.getUser().getId(), association.getCompany().getId());

			if (logger.isInfoEnabled())
				logger.debug("Removed user id " + association.getUser().getId() + " from lane " + association.getLaneType() + " of company id " + association.getCompany().getId());
			rowCount++;
		}

		if (logger.isInfoEnabled())
			logger.debug("@@@@@@@@@@@@@@@@@@@@ processPendingLaneRemovals END @@@@@@@@@@@@@@@@@@@@");

		return rowCount;
	}

	@Override
	public void reindexAllData() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexAllData BEGIN @@@@@@@@@@@@@@@@@@@@");
		searchService.reindexAllData();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexAllData END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public void reindexUsers() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexUsers BEGIN @@@@@@@@@@@@@@@@@@@@");
		searchService.reindexAllUsers();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexUsers END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public void reindexVendors() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexVendors BEGIN @@@@@@@@@@@@@@@@@@@@");
		searchService.reindexAllVendors();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexVendors END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public void reindexGroups() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexGroups BEGIN @@@@@@@@@@@@@@@@@@@@");
		searchService.reindexAllGroups();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexGroups END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public void reindexWork() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexWork BEGIN @@@@@@@@@@@@@@@@@@@@");
		searchService.reindexAllWork();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexWork END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override public void reindexLast6MonthsWork() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexLast6MonthsWork BEGIN @@@@@@@@@@@@@@@@@@@@");
		workIndexer.reindexWorkByLastModifiedDate(DateUtilities.getMidnight6MonthsAgo());
		logger.debug("@@@@@@@@@@@@@@@@@@@@ reindexLast6MonthsWork END @@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public void pruneWork() {
		logger.debug("@@@@@@@@@@@@@@@@@@@@ pruneWork BEGIN @@@@@@@@@@@@@@@@@@@@");
		workIndexer.pruneDeletedWork();
		logger.debug("@@@@@@@@@@@@@@@@@@@@ pruneWork END @@@@@@@@@@@@@@@@@@@@");
	}
}
