package com.workmarket.domains.search.group.indexer.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.search.group.indexer.dao.SolrGroupDAO;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.partition;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class GroupIndexerImpl implements GroupIndexer {

	private static final Log logger = LogFactory.getLog(GroupIndexerImpl.class);

	@Autowired private SolrGroupDAO solrGroupDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserGroupService userGroupService;

	@Value(value = "${solr.buffer.size}")
	private int bufferSize;

	private static final int COMMIT_WITHIN_MS = 1000;

	@Autowired
	@Qualifier("groupUpdateSolrServer")
	private ConcurrentUpdateSolrServer groupSolrServer;

	@Override
	public void reindexAll() {
		Integer maxGroupId = solrGroupDAO.getMaxGroupId();
		if (maxGroupId != null && maxGroupId > 0) {
			Long limit = maxGroupId.longValue();
			Long fromId = limit >= bufferSize ? limit - bufferSize : 1L;
			do {
				eventRouter.sendEvent(new GroupUpdateSearchIndexEvent().setFromId(fromId).setToId(limit));
				limit = fromId;
				if (limit >= bufferSize) {
					fromId = limit - bufferSize;
				} else {
					fromId = 1L;
				}
			} while (fromId > 1);
			eventRouter.sendEvent(new GroupUpdateSearchIndexEvent().setFromId(fromId).setToId(limit));
		}
	}

	@Override
	public void reindexById(Collection<Long> groupIds) {
		if (isNotEmpty(groupIds)) {
			List<List<Long>> subList = partition(Lists.newArrayList(groupIds), bufferSize);
			for (List<Long> list : subList) {
				List<GroupSolrData> groupBuffer = solrGroupDAO.getSolrDataById(list);
				addBeans(groupBuffer);
			}
		}
	}

	@Override
	public void reindexByUUID(final Collection<String> groupUUIDs) {
		if (isNotEmpty(groupUUIDs)) {
			final Map<String, Long> uuidIdPairs =
					userGroupService.findUserGroupUuidIdPairsByUuids(groupUUIDs);

			if (uuidIdPairs == null) {
				return;
			}

			final Collection<Long> groupIds = uuidIdPairs.values();
			if (isNotEmpty(groupIds)) {
				eventRouter.sendEvent(new GroupUpdateSearchIndexEvent(groupIds));
			}
		}
	}


	@Override
	public void reindexById(Long groupId) {
		Assert.notNull(groupId);

		GroupSolrData group = solrGroupDAO.getSolrDataById(groupId);
		Assert.notNull(group);
		addBeans(Lists.newArrayList(group));
	}

	@Override
	public void reindexBetweenIds(long fromId, long toId) {
		List<GroupSolrData> groupSolrDataList = solrGroupDAO.getSolrDataBetweenIds(fromId, toId);
		addBeans(groupSolrDataList);
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	void addBeans(List<GroupSolrData> groupSolrDataList) {
		if (isNotEmpty(groupSolrDataList)) {
			try {
				groupSolrServer.addBeans(groupSolrDataList, COMMIT_WITHIN_MS);
			} catch (SolrServerException | IOException e) {
				logger.error("Exception while indexing groups " + groupSolrDataList, e);
			}
		}
	}

	@Override
	public void deleteById(Collection<Long> userIds) {}

/*
	@Override
	public void indexProperties(Long groupId, Set<String> properties) {}

	@Override
	public void indexProperties(List<Long> groupIds, Set<String> properties) {}

	@Override
	public void indexProperty(Long groupId, String property) {}

	@Override
	public void indexProperty(List<Long> groupIds, String properties) {}
*/
}
