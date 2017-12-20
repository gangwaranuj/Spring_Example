package com.workmarket.domains.model.session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "impression")
@Table(name = "impression")
@AuditChanges
public class Impression extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private ImpressionType impressionType;
	private Long campaignId;
	private Long userId;
	private String referrer;
	private String userAgent;
	private String field1Name;
	private String field1Value;
	private String field2Name;
	private String field2Value;
	private String field3Name;
	private String field3Value;

	@Column(name = "impression_type_id", nullable = false)
	public ImpressionType getImpressionType() {
		return impressionType;
	}

	public void setImpressionType(ImpressionType impressionType) {
		this.impressionType = impressionType;
	}

	@Column(name = "campaign_id", nullable = false)
	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	@Column(name = "referrer", nullable = true, length = 200)
	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@Column(name = "user_agent", nullable = true, length = 200)
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Column(name = "user_id", nullable = true)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "field_1_name", nullable = true, length = 20)
	public String getField1Name() {
		return field1Name;
	}

	public void setField1Name(String field1Name) {
		this.field1Name = field1Name;
	}

	@Column(name = "field_1_value", nullable = true, length = 50)
	public String getField1Value() {
		return field1Value;
	}

	public void setField1Value(String field1Value) {
		this.field1Value = field1Value;
	}

	@Column(name = "field_2_name", nullable = true, length = 20)
	public String getField2Name() {
		return field2Name;
	}

	public void setField2Name(String field2Name) {
		this.field2Name = field2Name;
	}

	@Column(name = "field_2_value", nullable = true, length = 50)
	public String getField2Value() {
		return field2Value;
	}

	public void setField2Value(String field2Value) {
		this.field2Value = field2Value;
	}

	@Column(name = "field_3_name", nullable = true, length = 20)
	public String getField3Name() {
		return field3Name;
	}

	public void setField3Name(String field3Name) {
		this.field3Name = field3Name;
	}

	@Column(name = "field_3_value", nullable = true, length = 50)
	public String getField3Value() {
		return field3Value;
	}

	public void setField3Value(String field3Value) {
		this.field3Value = field3Value;
	}

}
