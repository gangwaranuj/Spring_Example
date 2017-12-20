package com.workmarket.data.solr.indexer.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.dao.search.work.SolrWorkDAO;
import com.workmarket.data.solr.indexer.SolrUpdater;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.EntityIdPagination;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.partition;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class WorkIndexerImpl implements WorkIndexer {

	private static final Log logger = LogFactory.getLog(WorkIndexerImpl.class);

	@Qualifier("workSolrUpdater")
	@Autowired private SolrUpdater<SolrWorkData> workDataSolrUpdater;
	@Autowired private WorkDAO workDAO;
	@Autowired private SolrWorkDAO solrWorkDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkService workService;

	@Value(value = "${solr.buffer.size}")
	private int bufferSize;

	@Override
	public void deleteById(Collection<Long> workIds) {
		if (isNotEmpty(workIds)) {
			workDataSolrUpdater.deleteById(new ArrayList<>(workIds));
		}
	}

	@Override
	public void optimize() {
		pruneDeletedWork();
		workDataSolrUpdater.optimize();
	}

	@Override
	public void pruneDeletedWork() {
		logger.info("Cleaning deleted assignments from SOLR index");
		Set<Long> assignments = Sets.newHashSet(workDAO.findAllEntityIdsBy("deleted", true));
		deleteById(assignments);
	}

	@Override
	public void reindexAll() {
		Integer maxAssignmentId = workDAO.getMaxAssignmentId();

		Long limit = maxAssignmentId.longValue();
		Long fromId = limit - bufferSize;
		do {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent().setFromId(fromId).setToId(limit));
			limit = fromId;
			if (limit >= bufferSize) {
				fromId = limit - bufferSize;
			} else {
				fromId = 1L;
			}
		} while (fromId > 1);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent().setFromId(fromId).setToId(limit));
	}

	@Override
	public void reindexById(Long workId) {
		workDataSolrUpdater.indexById(workId);
	}

	@Override
	public void reindexById(Collection<Long> workIds) {
		if (CollectionUtils.isEmpty(workIds)) {
			return;
		}

		if (workIds.size() > bufferSize) {
			List<List<Long>> subList = partition(Lists.newArrayList(workIds), bufferSize);
			for (List<Long> list : subList) {
				eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(list));
			}
			return;
		}

		if (workIds.size() > 1) {
			workDataSolrUpdater.indexByIds(Lists.newArrayList(workIds));
		} else {
			workDataSolrUpdater.indexById(CollectionUtilities.first(workIds));
		}

		logger.info(String.format("Reindexed work: %s", workIds.toString()));
	}

	@Override
	public void reindexBetweenIds(long fromId, long toId) {
		workDataSolrUpdater.indexFromIdToId(fromId, toId);
	}

	@Override
	public void reindexByUUID(final Collection<String> workUUIDs) {
		if (isNotEmpty(workUUIDs)) {
			final List<Long> workIds = workService.findAllWorkIdsByUUIDs(Lists.newArrayList(workUUIDs));
			if (isNotEmpty(workIds)) {
				eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
			}
		}
	}

	@Override
	public void reindexWorkByWorkNumbers(Set<String> workNumbers) {
		workDataSolrUpdater.update(solrWorkDAO.getSolrDataByWorkNumber(Lists.newArrayList(workNumbers)));
	}

	@Override
	public void reindexWorkByLastModifiedDate(Calendar lastModifiedFrom) {
		Assert.notNull(lastModifiedFrom);
		EntityIdPagination pagination = new EntityIdPagination();
		pagination.setResultsLimit(100);
		pagination = workDAO.findAllWorkIdsByLastModifiedDate(lastModifiedFrom, pagination);
		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(Lists.newArrayList(pagination.getResults())));
			pagination.nextPage();
			pagination.setSkipTotalCount(true);
			pagination = workDAO.findAllWorkIdsByLastModifiedDate(lastModifiedFrom, pagination);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void reindexWorkByCompany(Long companyId) {
		Assert.notNull(companyId);
		EntityIdPagination pagination = new EntityIdPagination();
		pagination.setResultsLimit(100);
		pagination = workDAO.findAllWorkIdsByCompanyId(companyId, pagination);
		for (int i = 0; i < pagination.getNumberOfPages(); i++) {
			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(Lists.newArrayList(pagination.getResults())));
			pagination.nextPage();
			pagination.setSkipTotalCount(true);
			pagination = workDAO.findAllWorkIdsByCompanyId(companyId, pagination);
		}
	}

/*
	@Override
	public void indexProperties(Long workId, Set<String> properties) {}

	@Override
	public void indexProperties(List<Long> workIds, Set<String> properties) {}

	@Override
	public void indexProperty(Long workIds, String property) {}

	@Override
	public void indexProperty(List<Long> workIds, String properties) {}
*/
}
