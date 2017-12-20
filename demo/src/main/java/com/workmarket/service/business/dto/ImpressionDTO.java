package com.workmarket.service.business.dto;

import org.apache.commons.lang.StringUtils;


public class ImpressionDTO {
	private Long impressionId;
	private Long impressionTypeId;
	private Long campaignId;
	private String referrer;
	private String userAgent;
	private Long userId;
	private String field1Name;
	private String field1Value;
	private String field2Name;
	private String field2Value;
	private String field3Name;
	private String field3Value;

	public Long getImpressionId() {
		return impressionId;
	}

	public void setImpressionId(Long impressionId) {
		this.impressionId = impressionId;
	}

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

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = StringUtils.left(referrer, 200);
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = StringUtils.left(userAgent, 200);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getField1Name() {
		return field1Name;
	}

	public void setField1Name(String field1Name) {
		this.field1Name = field1Name;
	}

	public String getField1Value() {
		return field1Value;
	}

	public void setField1Value(String field1Value) {
		this.field1Value = field1Value;
	}

	public String getField2Name() {
		return field2Name;
	}

	public void setField2Name(String field2Name) {
		this.field2Name = field2Name;
	}

	public String getField2Value() {
		return field2Value;
	}

	public void setField2Value(String field2Value) {
		this.field2Value = field2Value;
	}

	public String getField3Name() {
		return field3Name;
	}

	public void setField3Name(String field3Name) {
		this.field3Name = field3Name;
	}

	public String getField3Value() {
		return field3Value;
	}

	public void setField3Value(String field3Value) {
		this.field3Value = field3Value;
	}
}
