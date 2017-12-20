package com.workmarket.data.solr.indexer;

import com.google.common.collect.Lists;
import com.workmarket.dao.search.SolrDAO;
import com.workmarket.data.solr.model.SolrData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class BaseSolrUpdater<T extends SolrData> extends SolrUpdater<T> {

	private static final Log logger = LogFactory.getLog(BaseSolrUpdater.class);

	protected BaseSolrUpdater(
		SolrDocumentMapper<T> mapper,
		SolrServer solr, SolrDAO<T> solrDAO,
		SolrDataValidator<T> validator,
		SolrDataDecorator<T> decorator) {

		super(mapper, solr, solrDAO, validator, decorator);
	}

	@Override
	public SolrUpdaterResponse delete(final long id) {
		try {
			UpdateResponse solrResponse = solr.deleteById(String.valueOf(id));
			return new SolrUpdaterResponse(solrResponse);
		} catch (SolrServerException | IOException e) {
			logger.error(String.format("There was an error deleting : %s", id), e);
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}
	}

	@Override
	public SolrUpdaterResponse delete(final List<T> dataList) {
		if (isNotEmpty(dataList)) {
			List<Long> killMeIds = Lists.newArrayListWithCapacity(dataList.size());
			for (T data : dataList) {
				killMeIds.add(data.getId());
			}
			return deleteById(killMeIds);
		} else {
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}
	}

	@Override
	public SolrUpdaterResponse deleteById(final List<Long> ids) {
		if (isNotEmpty(ids)) {
			List<String> idStrs = Lists.newArrayListWithExpectedSize(ids.size());
			for (Long id : ids) {
				idStrs.add(String.valueOf(id));
			}
			try {
				solr.deleteById(idStrs);
				return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
			} catch (SolrServerException | IOException e) {
				logger.error("There was an error deleting data from solr:", e);
				return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
			}
		} else {
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}
	}
}
