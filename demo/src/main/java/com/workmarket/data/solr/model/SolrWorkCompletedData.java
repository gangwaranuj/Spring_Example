package com.workmarket.data.solr.model;

import java.util.List;

/**
 * Author: rocio
 */
public class SolrWorkCompletedData {
	List<String> keywords;
	Long companyId;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
}
