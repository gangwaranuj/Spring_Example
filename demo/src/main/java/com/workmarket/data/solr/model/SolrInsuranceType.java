package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrInsuranceType extends AbstractSolrData implements NamedSolrData {

	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.INSURANCE_IDS;
	}

	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.INSURANCE_NAMES;
	}

}
