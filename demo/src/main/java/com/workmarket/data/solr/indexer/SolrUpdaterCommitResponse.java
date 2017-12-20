package com.workmarket.data.solr.indexer;

public class SolrUpdaterCommitResponse {

	private long responseTimeMillis = -1;
	private boolean success = false;

	public long getResponseTimeMillis() {
		return responseTimeMillis;
	}

	public void setResponseTimeMillis(long responseTimeMillis) {
		this.responseTimeMillis = responseTimeMillis;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (responseTimeMillis ^ (responseTimeMillis >>> 32));
		result = prime * result + (success ? 1231 : 1237);
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
		SolrUpdaterCommitResponse other = (SolrUpdaterCommitResponse) obj;
		if (responseTimeMillis != other.responseTimeMillis)
			return false;
		if (success != other.success)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrUpdaterCommitResponse [responseTimeMillis=")
				.append(responseTimeMillis).append(", success=")
				.append(success).append("]");
		return builder.toString();
	}


}
