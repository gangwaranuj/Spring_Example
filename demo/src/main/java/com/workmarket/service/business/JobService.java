package com.workmarket.service.business;

public interface JobService {
	static final String PROCESS_PENDING_LANE_REMOVALS = "PROCESS_PENDING_LANE_REMOVALS";
	static final String REINDEX_ALL_DATA = "REINDEX_ALL_DATA";
	static final String REINDEX_USERS = "REINDEX_USERS";
	static final String REINDEX_GROUPS = "REINDEX_GROUPS";
	static final String REINDEX_WORK = "REINDEX_WORK";
	static final String CLEAN_WORK_INDEX = "CLEAN_WORK_INDEX";
	static final String REINDEX_6_MONTHS_WORK = "REINDEX_6_MONTHS_WORK";
	static final String REINDEX_VENDORS = "REINDEX_VENDORS";

	void reindexAllData();
	void reindexUsers();
	void reindexVendors();
	void reindexGroups();
	void reindexWork();
	void reindexLast6MonthsWork();
	void pruneWork();

	/**
	 * Process all lane associations that are pending removal
	 *
	 * @return number of rows processed
	 * @throws Exception
	 */
	int processPendingLaneRemovals() throws Exception;
}
