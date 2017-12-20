package com.workmarket.data.solr.indexer.user;

import com.workmarket.data.solr.indexer.BaseSolrUpdater;
import com.workmarket.data.solr.indexer.SolrDataValidator;
import com.workmarket.data.solr.model.SolrUserData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class SolrUserDataValidator implements SolrDataValidator<SolrUserData> {

	private static final Log logger = LogFactory.getLog(BaseSolrUpdater.class);

	@Override
	public boolean isDataValid(SolrUserData solrData) {
		if (solrData.getUuid() == null) {
			logger.warn("Solr uuid should not be null for user: " + solrData.getId());
			return false;
		}

		// NOTE [11/2/2016]: the following logic invalidates users without address.
		// In solr usercore schema, both lng and lat fields are required, and therefore the validation here.
		// Those two fields are removed and replaced by location and locations in workercore.
		// When employer bulk onboards employees without addresses, those employees will not be indexed.
		// We will decide later whether we want to remove this logic.
		// The main impact would be the missing address on search card.
		if (solrData.getPoint() == null || (solrData.getPoint().getLatitude() == 0.0 && solrData.getPoint().getLongitude() == 0.0)) {
			return false;
		}
		return true;
	}

}
