package com.workmarket.data.solr.indexer.user;

import com.workmarket.data.solr.indexer.SolrDataValidator;
import com.workmarket.data.solr.model.SolrVendorData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for Solr vendor data.
 */
@Component
public class SolrVendorDataValidator implements SolrDataValidator<SolrVendorData> {

    private static final Log logger = LogFactory.getLog(SolrVendorDataValidator.class);

    @Override
    public boolean isDataValid(final SolrVendorData vendor) {
        if (vendor.getUuid() == null) {
            logger.warn("Solr uuid should not be null for vendor: " + vendor.getId());
            return false;
        }

        return true;
    }
}
