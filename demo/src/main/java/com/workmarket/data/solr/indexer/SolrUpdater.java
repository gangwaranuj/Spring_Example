package com.workmarket.data.solr.indexer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.dao.search.SolrDAO;
import com.workmarket.data.solr.model.SolrData;
import com.workmarket.service.infra.email.RawEmailService;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class SolrUpdater<T extends SolrData> {

	private static final Log logger = LogFactory.getLog(SolrUpdater.class);

	protected final SolrServer solr;
	protected final SolrDocumentMapper<T> mapper;
	protected final SolrDAO<T> solrDAO;
	protected final SolrDataValidator<T> validator;
	protected final SolrDataDecorator<T> decorator;
	@Autowired
	private RawEmailService rawEmailService;
	private final static long BULK_COMMIT_THRESHOLD = 15000L;

	@Value(value = "${solr.retry.limit}")
	private int solrRetryLimit;

	@Value(value = "${solr.buffer.size}")
	protected int bufferSize;

	protected SolrUpdater(SolrDocumentMapper<T> mapper, SolrServer solr, SolrDAO<T> solrDAO, SolrDataValidator<T> validator, SolrDataDecorator<T> decorator) {
		this.solr = checkNotNull(solr);
		this.solrDAO = checkNotNull(solrDAO);
		this.decorator = checkNotNull(decorator);
		//Mapper and Validator are optional
		this.validator = validator;
		this.mapper = mapper;
	}

	/**
	 * Adds or updates an object of type T to the solr index.
	 *
	 * @param data
	 * @return {@link com.workmarket.data.solr.indexer.SolrUpdaterResponse SolrUpdaterResponse} the response.
	 */
	public SolrUpdaterResponse update(T data) {
		if (data == null) {
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}
		final List<T> list = ImmutableList.of(data);
		return update(list);
	}

	/**
	 * Adds or updates a collection of objects of type T to the solr index.
	 *
	 * @param dataList
	 * @return {@link com.workmarket.data.solr.indexer.SolrUpdaterResponse SolrUpdaterResponse} the response.
	 */
	public SolrUpdaterResponse update(List<T> dataList) {

		if (isEmpty(dataList)) {
			return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
		}

		List<SolrInputDocument> inputDocuments = Lists.newArrayListWithCapacity(bufferSize);
		dataList = (List<T>) decorator.decorate(dataList);

		if (validator != null && mapper != null) {
			for (T data : dataList) {
				if (!validator.isDataValid(data)) {
					continue;
				}
				inputDocuments.add(mapper.toSolrDocument(data));

				if (inputDocuments.size() % bufferSize == 0) {
					logger.info(String.format("Added %s docs to the index.", inputDocuments.size()));
					try {
						solr.add(inputDocuments);
					} catch (SolrServerException | IOException e) {
						logger.error("[solrUpdater] error updating documents", e);
					}
					inputDocuments.clear();
				}
			}
		}

		if (isNotEmpty(inputDocuments)) {
			try {
				solr.add(inputDocuments);
			} catch (SolrServerException | IOException e) {
				logger.error("[solrUpdater] error updating documents", e);
			}
			logger.info(String.format("Added %s docs to the index.", inputDocuments.size()));
		}

		return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
	}

	public SolrUpdaterCommitResponse commit() {
		try {
			this.solr.commit();
		} catch (SolrServerException | IOException e) {
			logger.fatal(e);
		}
		return new SolrUpdaterCommitResponse();
	}

	public void optimize() {
		try {
			solr.optimize();
		} catch (SolrServerException | IOException e) {
			logger.fatal(e);
		}
	}

	public SolrUpdaterResponse indexByIds(List<Long> ids) {
		List<Long> dataIds = CollectionUtilities.filterNull(ids);
		if (isEmpty(dataIds)) {
			return new SolrUpdaterResponse();
		}
		List<T> data = this.solrDAO.getSolrDataById(dataIds);
		return update(data);
	}

	public SolrUpdaterResponse indexById(long id) {
		T dataToUpdate = solrDAO.getSolrDataById(id);
		if (dataToUpdate != null) {
			return update(dataToUpdate);
		}

		return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
	}

	public SolrUpdaterResponse indexFromIdToId(long fromId, long toId) {
		List<T> dataToUpdate = this.solrDAO.getSolrDataBetweenIds(fromId, toId);
		// call only is the dataToUpdate size > 0, otherwise nothing to update, and success?
		if (dataToUpdate != null && dataToUpdate.size() > 0) {
			logger.info("About to add " + dataToUpdate.size() + " documents to the index");
			return update(dataToUpdate);
		}
		return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
	}

	protected void addBeans(List<T> beans) {
		if (isNotEmpty(beans)) {
			try {
				UpdateResponse response = solr.addBeans(beans);
				if (response != null && response.getQTime() > BULK_COMMIT_THRESHOLD) {
					logger.error(String.format("Solr commits responding very slowly %d", response.getQTime()));
					sendAlertEmail(response.getQTime());

				}
			} catch (IOException | SolrServerException e) {
				logger.error("Error re-indexing work", e);
			}
		}
	}

	protected void addBeans(List<T> beans, int commitWithin) {
		if (isNotEmpty(beans)) {
			try {
				UpdateResponse response = solr.addBeans(beans, commitWithin);
				if (response != null && response.getQTime() > BULK_COMMIT_THRESHOLD) {
					logger.error(String.format("Solr commits responding very slowly %d", response.getQTime()));
					sendAlertEmail(response.getQTime());

				}
			} catch (IOException | SolrServerException e) {
				logger.error("Error re-indexing work", e);
			}
		}
	}

	private void sendAlertEmail(int qTime) {
		rawEmailService.sendEmail("dev", "dev@workmarket.com", "QA", "qa@workmarket.com",
			"qa@workmarket.com", "Problem with solr! qtime = " + qTime, "please investigate");
	}

	public abstract SolrUpdaterResponse delete(long id);

	public abstract SolrUpdaterResponse delete(List<T> data);

	public abstract SolrUpdaterResponse deleteById(List<Long> ids);

	protected abstract int getCommitDelay();
}
