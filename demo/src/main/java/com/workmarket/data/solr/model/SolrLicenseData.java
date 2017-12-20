package com.workmarket.data.solr.model;

import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrLicenseData extends AbstractSolrData {
	private String licenseState;
	private String licenseVendor;
	
	public String getLicenseState() {
		return licenseState;
	}
	public void setLicenseState(String licenseState) {
		this.licenseState = licenseState;
	}
	public String getLicenseVendor() {
		return licenseVendor;
	}
	public void setLicenseVendor(String licenseVendor) {
		this.licenseVendor = licenseVendor;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((licenseState == null) ? 0 : licenseState.hashCode());
		result = prime * result
				+ ((licenseVendor == null) ? 0 : licenseVendor.hashCode());
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
		SolrLicenseData other = (SolrLicenseData) obj;
		if (licenseState == null) {
			if (other.licenseState != null)
				return false;
		} else if (!licenseState.equals(other.licenseState))
			return false;
		if (licenseVendor == null) {
			if (other.licenseVendor != null)
				return false;
		} else if (!licenseVendor.equals(other.licenseVendor))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrLicenseData [licenseState=" + licenseState
				+ ", licenseVendor=" + licenseVendor + "]";
	}
	@Override
	public UserSearchableFields getIdField() {
		return UserSearchableFields.LICENSE_IDS;
	}
	@Override
	public UserSearchableFields getNameField() {
		return UserSearchableFields.LICENSE_NAMES;
	}
	
	
}


