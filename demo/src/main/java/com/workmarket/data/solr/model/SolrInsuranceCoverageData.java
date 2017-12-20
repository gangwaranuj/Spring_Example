package com.workmarket.data.solr.model;

/**
 * User: alexsilva Date: 12/20/13 Time: 2:34 PM
 */
public class SolrInsuranceCoverageData {
	private long coverage;

	public long getCoverage() {
		return coverage;
	}
	public void setCoverage(long id) {
		this.coverage = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (coverage ^ (coverage >>> 32));
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
		SolrInsuranceCoverageData other = (SolrInsuranceCoverageData) obj;
		if (coverage != other.coverage)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SolrGroupData [coverage=" + coverage + "]";
	}
}
