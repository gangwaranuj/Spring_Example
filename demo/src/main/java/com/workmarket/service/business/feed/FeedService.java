package com.workmarket.service.business.feed;

import com.workmarket.search.gen.FeedMessages.FindWorkFeedResponse;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedRequest;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;

public interface FeedService {
	Feed getFeed(FeedRequestParams feedRequestParams, SolrQuery query) throws SolrServerException;

	void pushFeedToRedis();

	FindWorkFeedResponse findWorkFeed(FindWorkFeedRequest request);
}
