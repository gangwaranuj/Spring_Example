package com.workmarket.data.solr.model;

public class SolrGroupData {
	private long companyId;
	private long groupId;
	private String groupUuid;

	public String getGroupUuid() {
		return groupUuid;
	}

	public SolrGroupData setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
		return this;
	}

	public static SolrGroupData newSolrGroupData() {
		return new SolrGroupData();
	}
	
	public long getGroupId() {
		return groupId;
	}

	public SolrGroupData setGroupId(long groupId) {
		this.groupId = groupId;
		return this;
	}

	public long getCompanyId() {
		return companyId;
	}

	public SolrGroupData setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (companyId ^ (companyId >>> 32));
		result = prime * result + (int) (groupId ^ (groupId >>> 32));
		result = prime * result + groupUuid.hashCode();
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
		SolrGroupData other = (SolrGroupData) obj;
		if (companyId != other.companyId)
			return false;
		if (groupId != other.groupId)
			return false;
		if (!groupUuid.equals(other.groupUuid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SolrGroupData [companyId=" + companyId + ", groupId=" + groupId
				+ "]";
	}

}
