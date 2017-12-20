package com.workmarket.data.solr.indexer;

import com.google.common.collect.Lists;
import com.workmarket.dao.search.SolrDAO;
import com.workmarket.data.solr.model.UuidSolrData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Base uuid required Solr data updater.
 */
public abstract class BaseUuidSolrUpdater<T extends UuidSolrData> extends SolrUpdater<T> {

    private static final Log logger = LogFactory.getLog(BaseUuidSolrUpdater.class);

    protected BaseUuidSolrUpdater(
        SolrDocumentMapper<T> mapper,
        SolrServer solr, SolrDAO<T> solrDAO,
        SolrDataValidator<T> validator,
        SolrDataDecorator<T> decorator) {

        super(mapper, solr, solrDAO, validator, decorator);
    }

    @Override
    public SolrUpdaterResponse delete(final long id) {
        return deleteById(Lists.newArrayList(id));
    }

    @Override
    public SolrUpdaterResponse delete(final List<T> dataList) {
        if (isNotEmpty(dataList)) {
            List<String> uuidsToDelete = Lists.newArrayListWithCapacity(dataList.size());
            for (T data : dataList) {
                uuidsToDelete.add(data.getUuid());
            }
            try {
                solr.deleteById(uuidsToDelete);
                return new SolrUpdaterResponse(SolrUpdateStatus.SUCCESS);
            } catch (SolrServerException | IOException e) {
                logger.error("There was an error deleting data from solr:", e);
                return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
            }
        } else {
            return new SolrUpdaterResponse(SolrUpdateStatus.FAIL);
        }
    }

    @Override
    public SolrUpdaterResponse deleteById(final List<Long> ids) {
        if (isNotEmpty(ids)) {
            List<String> uuids = solrDAO.getSolrDataUuidsByIds(ids);
            try {
                solr.deleteById(uuids);
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
