package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.indexer.SolrUpdater;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.domains.search.solr.SolrThreadLocal;
import com.workmarket.search.queue.SolrUserSearchDelayQueue;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.jms.JmsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.partition;
import static com.google.gdata.util.common.base.Preconditions.checkNotNull;
import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static java.lang.Thread.sleep;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class UserIndexerImpl implements UserIndexer {

	private static final Log logger = LogFactory.getLog(UserIndexerImpl.class);

	private static final AtomicInteger attemptNumToCommit = new AtomicInteger(0);

	@Qualifier("userSolrUpdater")
	@Autowired private SolrUpdater<SolrUserData> userSolrUpdater;
	@Autowired private SolrUserSearchDelayQueue delayQueue;
	@Autowired private JmsService jmsService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private EventRouter eventRouter;

	@Value(value = "${solr.buffer.size}")
	private int bufferSize;

	@Override
	public void reindexById(Long userId) {
		delayQueue.addToQueue(userId);
	}

	private void indexUsers(List<Long> userIds) {
		if (isNotEmpty(userIds)) {
			userIds.remove(Constants.WORKMARKET_SYSTEM_USER_ID);
			if (userIds.size() > bufferSize) {
				List<List<Long>> subList = partition(userIds, bufferSize);
				for (List<Long> list : subList) {
					eventRouter.sendEvent(new UserSearchIndexEvent().setUserIds(Sets.newHashSet(list)));
				}
				return;
			}

			userSolrUpdater.indexByIds(userIds);
			logger.info(String.format("Re-indexed users: %s", userIds.toString()));
		}
	}

	@Override
	public void reindexByUUID(final Collection<String> userUUIDs) {
		if (isNotEmpty(userUUIDs)) {
			final Set<Long> userIds = userService.findAllUserIdsByUuids(userUUIDs);
			if (isNotEmpty(userIds)) {
				eventRouter.sendEvent(new UserSearchIndexEvent(Lists.newArrayList(userIds)));
			}
		}
	}

	@Override
	public void reindexById(Collection<Long> userIds) {
		if (isNotEmpty(userIds)) {
			indexUsers(Lists.newArrayList(userIds));
		}
	}

	@Override
	public void deleteById(Collection<Long> userIds) {
		if (isNotEmpty(userIds)) {
			userSolrUpdater.deleteById(new ArrayList<>(userIds));
		}
	}

	@Override
	public void reindexAll() {
		Integer maxUserId = userService.getMaxUserId();
		Long limit = maxUserId.longValue();
		Long fromId = limit - bufferSize;
		do {
			eventRouter.sendEvent(new UserSearchIndexEvent().setDirectedTowards(SolrThreadLocal.getDirectedTowards()).setToId(limit).setFromId(fromId));
			limit = fromId;
			if (limit >= bufferSize) {
				fromId = limit - bufferSize;
			} else {
				fromId = 1L;
			}
		} while (fromId > 1);
		eventRouter.sendEvent(new UserSearchIndexEvent().setDirectedTowards(SolrThreadLocal.getDirectedTowards()).setFromId(fromId).setToId(limit));
	}

	@Override
	public void reindexBetweenIds(long fromId, long toId) {
		userSolrUpdater.indexFromIdToId(fromId, toId);
	}


	@Scheduled(fixedDelay = 30000)
	public void checkQueue() {
		if (attemptNumToCommit.get() == 0) {
			checkQueueHelper();
		}
	}

	private void checkQueueHelper() {
		attemptNumToCommit.incrementAndGet();
		List<Long> updateThese = Collections.emptyList();
		try {
			updateThese = delayQueue.grabAll();
			if (isEmpty(updateThese)) {
				attemptNumToCommit.set(0);
				return;
			}
			logger.debug("Updating the following ids to index: " + updateThese);
			jmsService.sendEventMessage(new UserSearchIndexEvent(updateThese));
			attemptNumToCommit.set(0);

		} catch (Exception e) {
			logger.fatal("Unknown exception in the indexer updater.  Data attempted - readding to queue:" + delayQueue, e);
			checkNotNull(userSolrUpdater);
			checkNotNull(delayQueue);

			if (attemptNumToCommit.get() >= 50) {
				logger.fatal("Did *NOT* update the following users due to outage: " + updateThese, e);
				attemptNumToCommit.set(0);
				return;
			}

			if (!isEmpty(updateThese)) {
				try {
					delayQueue.addAllToQueue(updateThese);
					try {
						sleep(10000);
					} catch (InterruptedException e1) {
						logger.error(e1);
					}
				} catch (InterruptedException e1) {
					logger.error(e1);
				}
			} else {
				attemptNumToCommit.set(0);
				return;
			}

			checkQueueHelper();
		}
	}

}
