package com.workmarket.data.solr.model;

/**
 * User: alexsilva Date: 8/6/14 Time: 4:24 PM
 */
public class SolrSharedGroupData {
	private long networkId;
	private long groupId;
	private String groupUuid;

	public String getGroupUuid() {
		return groupUuid;
	}

	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public long getNetworkId() {
		return networkId;
	}

	public void setNetworkId(long companyId) {
		this.networkId = companyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (networkId ^ (networkId >>> 32));
		result = prime * result + (int) (groupId ^ (groupId >>> 32));
		result = prime * result + groupUuid.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SolrSharedGroupData other = (SolrSharedGroupData) obj;
		if (networkId != other.networkId) {
			return false;
		}
		if (groupId != other.networkId) {
			return false;
		}
		if (!groupUuid.equals(other.groupUuid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SolrSharedGroupsData [networkId=" + networkId + ", groupId=" + groupId
				+ "]";
	}

}
