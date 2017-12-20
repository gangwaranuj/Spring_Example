package com.workmarket.search.queue;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "bean:name=SolrUserSearchDelayQueue", description = "Bean that queues and delays adding to the solr index.")
public class SolrUserSearchDelayQueueImpl extends SolrDelayQueueImpl  implements SolrUserSearchDelayQueue {
}
