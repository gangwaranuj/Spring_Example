package com.workmarket.search.queue;

import java.util.List;

public interface SolrDelayQueue {

	public void addToQueue(Long id);

	public void addToQueue(Long id, Long delayInMillis);

	public Long grab() throws InterruptedException;

	public boolean isEmpty();

	List<Long> grabAll() throws InterruptedException;

	void addAllToQueue(List<Long> updateThese) throws InterruptedException;

}
