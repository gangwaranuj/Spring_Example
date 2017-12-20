package com.workmarket.data.solr.indexer;

import com.workmarket.data.solr.model.SolrData;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;

public interface SolrDocumentMapper<T extends SolrData> {

	/**
	 * Maps an object of type T to a SolrInputDocument
	 *
	 * @param solrData
	 * @return {@link org.apache.solr.common.SolrInputDocument SolrInputDocument} the mapped document.
	 */
	SolrInputDocument toSolrDocument(T solrData);

}
