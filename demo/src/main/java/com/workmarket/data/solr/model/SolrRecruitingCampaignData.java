package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrRecruitingCampaignData extends AbstractSolrData implements NamedSolrData {

	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.RECRUITING_CAMPAIGN_ID;
	}

	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.RECRUITING_CAMPAIGN_NAME;
	}

}
