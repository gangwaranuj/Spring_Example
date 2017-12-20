package com.workmarket.search.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.DelayQueue;

import static java.util.Collections.emptyList;

public abstract class SolrDelayQueueImpl extends DelayQueue<LongDelay> implements SolrDelayQueue {

	private static final Log logger = LogFactory.getLog(SolrDelayQueueImpl.class);

	@Value(value = "${search.queue.delay}")
	private Long defaultSearchQueueDelay;

	public Long getDefaultSearchQueueDelay() {
		return defaultSearchQueueDelay;
	}

	@ManagedAttribute
	public void setDefaultSearchQueueDelay(Long defaultSearchQueueDelay) {
		this.defaultSearchQueueDelay = defaultSearchQueueDelay;
	}

	@Override
	public void addToQueue(Long id) {
		addToQueue(id, defaultSearchQueueDelay);
	}

	@Override
	public void addAllToQueue(List<Long> updateThese) throws InterruptedException {
		for (Long id : updateThese) {
			if (id <= 0) {
				return;
			}
			LongDelay idDelayed = new LongDelay(id, defaultSearchQueueDelay);

			if (this.contains(idDelayed)) {
				logger.info(id + " was already set for index.  Avoided dupe.");
				return;
			}
			logger.info(idDelayed + " being queued for search update in one minute.");
			this.add(idDelayed);
		}
	}

	@Override
	public void addToQueue(Long id, Long delayInMillis) {
		if (id <= 0) {
			return;
		}
		LongDelay idDelayed = new LongDelay(id, delayInMillis);

		if (this.contains(idDelayed)) {
			logger.info(id + " was already set for index.  Avoided dupe.");
			return;
		}
		logger.info(idDelayed + " being queued for search update in one minute.");
		this.add(idDelayed);

	}

	@Override
	public Long grab() throws InterruptedException {
		LongDelay val = this.take();
		if (val == null) {
			return null;
		}
		return val.getValue();
	}

	@Override
	public List<Long> grabAll() throws InterruptedException {
		Collection<LongDelay> allVals = Sets.newHashSet();
		this.drainTo(allVals);
		if (CollectionUtils.isNotEmpty(allVals)) {
			List<Long> returnVal = Lists.newArrayListWithCapacity(allVals.size());
			for (LongDelay longDelay : allVals) {
				returnVal.add(longDelay.getValue());
			}
			return returnVal;
		}
		return emptyList();
	}
}
