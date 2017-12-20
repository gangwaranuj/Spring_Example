package com.workmarket.data.solr.indexer.work;

import com.google.common.collect.Lists;
import com.workmarket.dao.search.work.SolrWorkDAO;
import com.workmarket.data.solr.indexer.BaseSolrUpdater;
import com.workmarket.data.solr.indexer.SolrUpdateStatus;
import com.workmarket.data.solr.indexer.SolrUpdaterResponse;
import com.workmarket.data.solr.model.SolrWorkData;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class WorkSolrUpdater extends BaseSolrUpdater<SolrWorkData> {

	@Value(value = "${solr.work.commit.delay}")
	private int solrWorkCommitDelay;

	@Autowired
	public WorkSolrUpdater(@Qualifier("workUpdateSolrServer") SolrServer solr,
						   @Qualifier("solrWorkDAOImpl") SolrWorkDAO solrDAO,
						   @Qualifier("solrWorkDataDecorator") SolrWorkDataDecorator decorator) {
		super(null, solr, solrDAO, null, decorator);
	}

	@Override
	protected int getCommitDelay() {
		return solrWorkCommitDelay;
	}

	@Override
	public SolrUpdaterResponse update(List<SolrWorkData> dataList) {
		if (isEmpty(dataList)) {
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}

		dataList = (List<SolrWorkData>) decorator.decorate(dataList);
		if (isNotEmpty(dataList)) {
			addBeans(dataList);
		}

		return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
	}

	@Override
	public SolrUpdaterResponse indexById(long id) {
		SolrWorkData solrWorkData = solrDAO.getSolrDataById(id);
		if (solrWorkData != null) {
			addBeans(Lists.newArrayList(decorator.decorate(solrWorkData)));
		}

		return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
	}

}
