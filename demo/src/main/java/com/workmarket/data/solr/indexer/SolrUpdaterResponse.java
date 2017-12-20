package com.workmarket.data.solr.indexer;

import org.apache.solr.client.solrj.response.UpdateResponse;

import java.util.List;

public class SolrUpdaterResponse {

	private SolrUpdateStatus solrUpdateStatus;
	private List<Long> dataUpdated;
	private List<Long> dataDeleted;
	private Integer qTime;

	public SolrUpdaterResponse() {
	}

	public SolrUpdaterResponse(SolrUpdateStatus solrUpdateStatus) {
		this.solrUpdateStatus = solrUpdateStatus;
	}

	public SolrUpdaterResponse(UpdateResponse updateResponse) {
		if (updateResponse != null) {
			this.qTime = updateResponse.getQTime();
			this.solrUpdateStatus = SolrUpdateStatus.SUCCESS;
		}
	}

	public List<Long> getDataDeleted() {
		return dataDeleted;
	}

	public void setDataDeleted(List<Long> dataDeleted) {
		this.dataDeleted = dataDeleted;
	}

	public List<Long> getDataUpdated() {
		return dataUpdated;
	}

	public void setDataUpdated(List<Long> dataUpdated) {
		this.dataUpdated = dataUpdated;
	}

	public Integer getqTime() {
		return qTime;
	}

	public void setqTime(Integer qTime) {
		this.qTime = qTime;
	}

	public SolrUpdateStatus getSolrUpdateStatus() {
		return solrUpdateStatus;
	}

	public void setSolrUpdateStatus(SolrUpdateStatus solrUpdateStatus) {
		this.solrUpdateStatus = solrUpdateStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SolrUpdaterResponse)) return false;

		SolrUpdaterResponse that = (SolrUpdaterResponse) o;

		if (dataDeleted != null ? !dataDeleted.equals(that.dataDeleted) : that.dataDeleted != null) return false;
		if (dataUpdated != null ? !dataUpdated.equals(that.dataUpdated) : that.dataUpdated != null) return false;
		if (qTime != null ? !qTime.equals(that.qTime) : that.qTime != null) return false;
		if (solrUpdateStatus != that.solrUpdateStatus) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = solrUpdateStatus != null ? solrUpdateStatus.hashCode() : 0;
		result = 31 * result + (dataUpdated != null ? dataUpdated.hashCode() : 0);
		result = 31 * result + (dataDeleted != null ? dataDeleted.hashCode() : 0);
		result = 31 * result + (qTime != null ? qTime.hashCode() : 0);
		return result;
	}
}