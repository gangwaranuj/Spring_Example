package com.workmarket.data.solr.indexer;

/**
 * Author: rocio
 */
public enum SolrUpdateStatus {

	SUCCESS(),
	FAIL();

	public boolean isSuccess() {
		return SolrUpdateStatus.SUCCESS.equals(this);
	}
}
