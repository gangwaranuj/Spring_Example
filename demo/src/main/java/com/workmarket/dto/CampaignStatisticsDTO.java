package com.workmarket.dto;


public class CampaignStatisticsDTO {
	private Long impressionTypeId;
	private Long campaignId;
	private Long totalImpressionCount;
	private Long uniqueImpressionCount;
	private Long anonymousTotalImpressionCount;
	private Long anonymousUniqueImpressionCount;

	public Long getImpressionTypeId() {
		return impressionTypeId;
	}

	public void setImpressionTypeId(Long impressionTypeId) {
		this.impressionTypeId = impressionTypeId;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Long getTotalImpressionCount() {
		return totalImpressionCount;
	}

	public void setTotalImpressionCount(Long totalImpressionCount) {
		this.totalImpressionCount = totalImpressionCount;
	}

	public Long getUniqueImpressionCount() {
		return uniqueImpressionCount;
	}

	public void setUniqueImpressionCount(Long uniqueImpressionCount) {
		this.uniqueImpressionCount = uniqueImpressionCount;
	}

	public Long getAnonymousTotalImpressionCount() {
		return anonymousTotalImpressionCount;
	}

	public void setAnonymousTotalImpressionCount(Long anonymousTotalImpressionCount) {
		this.anonymousTotalImpressionCount = anonymousTotalImpressionCount;
	}

	public Long getAnonymousUniqueImpressionCount() {
		return anonymousUniqueImpressionCount;
	}

	public void setAnonymousUniqueImpressionCount(Long anonymousUniqueImpressionCount) {
		this.anonymousUniqueImpressionCount = anonymousUniqueImpressionCount;
	}
}
