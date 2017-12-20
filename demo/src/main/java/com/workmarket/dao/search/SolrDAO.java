package com.workmarket.dao.search;

import com.workmarket.data.solr.model.SolrData;

import java.util.Calendar;
import java.util.List;

public interface SolrDAO<T extends SolrData> {

	T getSolrDataById(Long id);
	
	List<T> getSolrDataById(List<Long> ids);

	List<T> getSolrDataBetweenIds(Long fromId, Long toId);

	List<T> getSolrDataChanged(Calendar from);

	List<String> getSolrDataUuidsByIds(List<Long> ids);

}
