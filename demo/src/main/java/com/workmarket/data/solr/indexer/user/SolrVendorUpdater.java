package com.workmarket.data.solr.indexer.user;

import com.workmarket.data.solr.indexer.BaseUuidSolrUpdater;
import com.workmarket.data.solr.indexer.SolrDocumentMapper;
import com.workmarket.dao.search.user.SolrVendorDAO;
import com.workmarket.data.solr.model.SolrVendorData;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * SolrUpdater for vendor.
 */
@Component
public class SolrVendorUpdater extends BaseUuidSolrUpdater<SolrVendorData> {

    @Autowired
    public SolrVendorUpdater(
        @Qualifier("solrVendorDocumentMapper") SolrDocumentMapper<SolrVendorData> mapper,
        @Qualifier("userUpdateSolrServer") SolrServer solr,
        @Qualifier("solrVendorDAOImpl") SolrVendorDAO solrVendorDAO,
        @Qualifier("solrVendorDataDecorator") SolrVendorDataDecorator decorator) {
        super(mapper, solr, solrVendorDAO, new SolrVendorDataValidator(), decorator);
    }

    @Override
    protected int getCommitDelay() {
        return 0;
    }
}
