package com.workmarket.data.solr.indexer.user;

import com.workmarket.dao.search.user.SolrUserDAO;
import com.workmarket.data.solr.indexer.BaseUuidSolrUpdater;
import com.workmarket.data.solr.indexer.SolrDocumentMapper;
import com.workmarket.data.solr.model.SolrUserData;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserSolrUpdater extends BaseUuidSolrUpdater<SolrUserData> {

	@Autowired
	public UserSolrUpdater(@Qualifier("solrUserDocumentMapper") SolrDocumentMapper<SolrUserData> mapper,
						   @Qualifier("userUpdateSolrServer") SolrServer solr,
						   @Qualifier("solrUserDAOImpl") SolrUserDAO solrDAO,
						   @Qualifier("solrUserDataDecorator") SolrUserDataDecorator decorator) {
		super(mapper, solr, solrDAO, new SolrUserDataValidator(), decorator);
	}

	@Override
	protected int getCommitDelay() {
		return 0;
	}
}
