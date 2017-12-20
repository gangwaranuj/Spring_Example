package com.workmarket.data.solr.repository;

import com.workmarket.data.solr.model.SolrWorkData;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

public interface WorkSearchRepository extends CrudRepository<SolrWorkData, String> {

	Page<SolrWorkData> findByTitle(String title);

	void updateWorkTitle(String workId, String title);
}
