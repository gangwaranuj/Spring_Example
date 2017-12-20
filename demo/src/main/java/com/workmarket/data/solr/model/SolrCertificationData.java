package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrCertificationData extends AbstractSolrData {
	private String certificationVendor;

	public String getCertificationVendor() {
		return certificationVendor;
	}

	public void setCertificationVendor(String certificationVendor) {
		this.certificationVendor = certificationVendor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((certificationVendor == null) ? 0 : certificationVendor
						.hashCode());
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
		SolrCertificationData other = (SolrCertificationData) obj;
		if (certificationVendor == null) {
			if (other.certificationVendor != null)
				return false;
		} else if (!certificationVendor.equals(other.certificationVendor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SolrCertificationData [certificationVendor="
				+ certificationVendor + "]";
	}

	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.CERTIFICATION_IDS;
	}

	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.CERTIFICATION_NAMES;
	}

	
}
