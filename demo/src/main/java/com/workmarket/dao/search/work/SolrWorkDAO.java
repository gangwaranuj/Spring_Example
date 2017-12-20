package com.workmarket.dao.search.work;

import com.workmarket.dao.search.SolrDAO;
import com.workmarket.data.solr.model.SolrWorkData;

import java.util.Calendar;
import java.util.List;

public interface SolrWorkDAO extends SolrDAO<SolrWorkData> {

	List<SolrWorkData> getSolrDataByWorkNumber(List<String> workNumbers);
}
