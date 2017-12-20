package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrSkillData extends AbstractSolrData implements NamedSolrData {

	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.SKILL_IDS;
	}

	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.SKILL_NAMES;
	}
}
