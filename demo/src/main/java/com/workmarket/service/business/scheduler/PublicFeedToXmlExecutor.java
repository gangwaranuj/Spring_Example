package com.workmarket.service.business.scheduler;

import com.workmarket.service.business.feed.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;


@Component
@ManagedResource(objectName="bean:name=publicFeedToXml", description="Public feed to XML")
public class PublicFeedToXmlExecutor implements ScheduledExecutor {
	@Autowired private FeedService feedService;

	@Override
	@SuppressWarnings("unchecked")
	@ManagedOperation(description = "feedz2xml")
	public void execute(){
		feedService.pushFeedToRedis();

	}



}
