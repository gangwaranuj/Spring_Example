package com.workmarket.domains.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "mbo_profile")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "mbo_profile")
public class MboProfile extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	public static final String MBO = "MBO";
	public static final String ME = "ME";
	public static final String NORMAL = "NORMAL";
	public static final String PREREGISTERED = "PREREGISTERED";

	private Boolean missingAddress;
	private String origin;
	private String status;
	private String paymentPreference;
	private Long userId;

	// these two will be set on demand whenever the first opportunity or feed items are created
	private String accountId;
	private String contactParentAccountId;
	private String leadId;
	private String objectId;

	@Column(name = "missing_address")
	public Boolean getMissingAddress() {
		return missingAddress;
	}

	public void setMissingAddress(Boolean missingAddress) {
		this.missingAddress = missingAddress;
	}

	@Column(name = "origin")
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "payment_preference")
	public String getPaymentPreference() {
		return paymentPreference;
	}

	public void setPaymentPreference(String paymentPreference) {
		this.paymentPreference = paymentPreference;
	}

	@Column(name = "account_id")
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Column(name = "contact_parent_account_id")
	public String getContactParentAccountId() {
		return contactParentAccountId;
	}

	public void setContactParentAccountId(String contactParentAccountId) {
		this.contactParentAccountId = contactParentAccountId;
	}

	@Column(name = "lead_id")
	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	@Column(name = "object_id")
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Transient
	public boolean isPayMbo() {
		return MBO.equals(this.getPaymentPreference());
	}

	@Transient
	public boolean isPayMe() {
		return ME.equals(this.getPaymentPreference());
	}
}