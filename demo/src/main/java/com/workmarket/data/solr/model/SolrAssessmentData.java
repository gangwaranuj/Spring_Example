package com.workmarket.data.solr.model;

public class SolrAssessmentData {
	private long assessmentId;
	private long companyId;
	public long getAssessmentId() {
		return assessmentId;
	}
	public void setAssessmentId(long assessmentId) {
		this.assessmentId = assessmentId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (assessmentId ^ (assessmentId >>> 32));
		result = prime * result + (int) (companyId ^ (companyId >>> 32));
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
		SolrAssessmentData other = (SolrAssessmentData) obj;
		if (assessmentId != other.assessmentId)
			return false;
		if (companyId != other.companyId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrAssessmentData [assessmentId=" + assessmentId
				+ ", companyId=" + companyId + "]";
	}
	
}
