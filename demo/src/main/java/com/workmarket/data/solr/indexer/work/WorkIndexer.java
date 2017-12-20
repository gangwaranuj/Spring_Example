package com.workmarket.data.solr.indexer.work;

import com.workmarket.data.solr.indexer.Indexer;

import java.util.Calendar;
import java.util.Set;

/**
 * Author: rocio
 */
public interface WorkIndexer extends Indexer {

	void reindexWorkByWorkNumbers(Set<String> workNumbers);

	void reindexWorkByLastModifiedDate(Calendar lastModifiedFrom);

	void optimize();

	void pruneDeletedWork();

	void reindexWorkByCompany(Long companyId);
}
