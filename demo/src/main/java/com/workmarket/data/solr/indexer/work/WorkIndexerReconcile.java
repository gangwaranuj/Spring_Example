package com.workmarket.data.solr.indexer.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.MapUtils.isEmpty;

/**
 * Author: rocio
 */
@Repository
public class WorkIndexerReconcile {

	private static final Log logger = LogFactory.getLog(WorkIndexerReconcile.class);

	@Autowired @Qualifier("workUpdateSolrServer")
	private ConcurrentUpdateSolrServer workSolrServer;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkIndexer workIndexer;

	private final static int WORK_INDEX_UPDATE_DELAY_THRESHOLD_IN_SECONDS = 60;
	private final static int WORK_INDEX_RECONCILIATION_NUMBER_OF_ROWS = 500;

	public int reconcileWork() {
		Map<Long, Calendar> assignmentsByModifiedDate = workDAO.findLastModifiedDate(WORK_INDEX_RECONCILIATION_NUMBER_OF_ROWS);
		if (isEmpty(assignmentsByModifiedDate)) {
			return 0;
		}

		Set<Long> mismatches = Sets.newHashSet();
		List<SolrWorkData> resultsFromSolr = Lists.newArrayListWithExpectedSize(assignmentsByModifiedDate.size());
		List<String> workIdsList = Lists.newArrayListWithExpectedSize(assignmentsByModifiedDate.size());
		for (Long id : assignmentsByModifiedDate.keySet()) {
			workIdsList.add("id:" + id);
		}

		SolrQuery query = new SolrQuery();
		query.set("qt", "/workSearch");
		query.setQuery(CollectionUtilities.join(workIdsList, " OR "));
		query.setStart(0);
		query.setRows(assignmentsByModifiedDate.size());


		try {
			QueryResponse rsp = workSolrServer.query(query);
			if (rsp != null) {
				resultsFromSolr = rsp.getBeans(SolrWorkData.class);
			}
			int delay = 0;
			if (isNotEmpty(resultsFromSolr)) {
				for (SolrWorkData solrWorkData : resultsFromSolr) {
					if (solrWorkData.getLastModifiedDate() != null) {
						Calendar lastModifiedDate = (Calendar) MapUtils.getObject(assignmentsByModifiedDate, solrWorkData.getWorkId(), null);
						if (lastModifiedDate != null) {
							Calendar solrLastModifiedDate = DateUtilities.getCalendarFromDate(solrWorkData.getLastModifiedDate());
							int secondsBetween = DateUtilities.getSecondsBetween(lastModifiedDate, solrLastModifiedDate);
							if (secondsBetween >= WORK_INDEX_UPDATE_DELAY_THRESHOLD_IN_SECONDS) {
								mismatches.add(solrWorkData.getId());
							}
							delay = Math.max(secondsBetween, delay);
						} else {
							mismatches.add(solrWorkData.getId());
						}
					}
				}
			}

			if (isNotEmpty(mismatches)) {
				workIndexer.reindexById(mismatches);
				return mismatches.size();
			}
		} catch (SolrServerException e) {
			logger.error("Error searching for work " + query, e);
		}
		return 0;
	}
}
