package com.workmarket.domains.work.service;

import com.workmarket.domains.work.model.Work;
import com.workmarket.service.exception.WorkMarketException;
import com.workmarket.thrift.work.WorkPublishRequest;

public interface WorkPublishService {

	void publish(WorkPublishRequest workPublishRequest) throws WorkMarketException;

	void publish(Work work);

	void removeFromFeed(WorkPublishRequest workPublishRequest) throws WorkMarketException;

}
