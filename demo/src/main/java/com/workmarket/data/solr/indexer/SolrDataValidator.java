package com.workmarket.data.solr.indexer;

import com.workmarket.data.solr.model.SolrData;

public interface SolrDataValidator<T extends SolrData> {

	boolean isDataValid(T solrData);

}
