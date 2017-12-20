package com.workmarket.data.solr.model;

import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrCompanyData extends AbstractSolrData {
	private CompanyType type;
	private String uuid;

	public CompanyType getType() {
		return type;
	}

	public void setType(CompanyType type) {
		this.type = type;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.COMPANY_ID;
	}

	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.COMPANY_NAME;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrCompanyData other = (SolrCompanyData) obj;
		if (type != other.type)
			return false;
		if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SolrCompanyData [type=" + type + "]";
	}
	
}
