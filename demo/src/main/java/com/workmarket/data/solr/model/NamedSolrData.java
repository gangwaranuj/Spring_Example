package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public interface NamedSolrData {
	
	public long getId();
	public String getName();

	public UserSearchableFields getIdField();
	public UserSearchableFields getNameField();
	
}
