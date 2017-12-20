package com.workmarket.data.solr.indexer;

import com.workmarket.data.solr.model.SolrData;

import java.util.Collection;

public interface SolrDataDecorator<T extends SolrData> {

	Collection<T> decorate(Collection<T> solrData);

	T decorate(T solrData);
}
