package com.workmarket.data.solr.repository;

import com.workmarket.data.solr.model.SolrWorkData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

public class WorkSearchRepositoryImpl extends SimpleSolrRepository<SolrWorkData> implements WorkSearchRepository {

	@Autowired private SolrOperations solrTemplate;

	@Override
	public Page<SolrWorkData> findByTitle(String title) {
		Query query = new SimpleQuery(new Criteria(WorkSearchableFields.TITLE).is(title));
		return getSolrOperations().queryForPage(query, SolrWorkData.class);
	}

	@Override
	public void updateWorkTitle(String workId, String title) {

		PartialUpdate update = new PartialUpdate(WorkSearchableFields.ID, workId);
		update.setValueOfField(WorkSearchableFields.TITLE.getName(), title);

		solrTemplate.saveBean(update);
		solrTemplate.commit();
	}
}
