package com.workmarket.data.solr.model;

/**
 * Created by ianha on 1/8/14
 */
public class SolrContractData {
	private Long contractId;
	private Long contractVersionId;
	private Long companyId;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getContractVersionId() { return contractVersionId; }

	public void setContractVersionId(Long contractVersionId) { this.contractVersionId = contractVersionId; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (contractId ^ (contractId >>> 32));
		result = prime * result + (companyId != null ? (int) (companyId ^ (companyId >>> 32)) : 0);
		result = prime * result + (contractVersionId != null ? (int) (contractVersionId ^ (contractVersionId >>> 32)) : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		SolrContractData other = (SolrContractData) obj;

		if (!contractId.equals(other.getContractId())) {
			return false;
		}

		if (companyId != null ? !companyId.equals(other.getCompanyId()) : other.getCompanyId() != null) {
			return false;
		}

		if (contractVersionId != null ? contractVersionId.equals(other.getContractVersionId()) : other.getContractVersionId() != null) {
			return false;
		}

		return true;
	}
}
