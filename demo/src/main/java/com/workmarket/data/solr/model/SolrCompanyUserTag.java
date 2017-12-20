package com.workmarket.data.solr.model;

import org.apache.commons.lang3.StringUtils;

public class SolrCompanyUserTag {
	
	private String tag;
	private Long companyId;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrCompanyUserTag other = (SolrCompanyUserTag) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CompanyUserTag [tag=" + tag + ", companyId=" + companyId + "]";
	}

	public String getCompanyTag() {
		return getCompanyId() + "_" + getTag();
	}

	public String getCompanyTag(String tag) {
		if (StringUtils.isNotBlank(tag)) return getCompanyId() + "_" + tag.trim();
		return StringUtils.EMPTY;
	}
	
	
}