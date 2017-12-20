package com.workmarket.domains.model.audit;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.utility.DateUtilities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Calendar;

@MappedSuperclass
public abstract class AuditedEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private Calendar createdOn;
	private Calendar modifiedOn;
	private Long modifierId;
	private Long creatorId;
	private String creatorNumber;
	private String modifierNumber;

	@Column(name = "created_on", nullable = false, updatable = false)
	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "modified_on", nullable = false)
	public Calendar getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	@Column(name = "modifier_id")
	public Long getModifierId() { return modifierId; }

	public void setModifierId(Long modifierId) { this.modifierId = modifierId; }

	@Column(name = "creator_id")
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Transient
	public String getCreatorNumber() {
		return creatorNumber;
	}

	@Transient
	public void setCreatorNumber(String creatorNumber) {
		this.creatorNumber = creatorNumber;
	}

	@Transient
	public String getModifierNumber() {
		return modifierNumber;
	}

	@Transient
	public void setModifierNumber(String modifierNumber) {
		this.modifierNumber = modifierNumber;
	}

	@Transient
	public String getCreatedOnString() {
		return DateUtilities.getISO8601(getCreatedOn());
	}

	@Transient
	public String getModifiedOnString() {
		return DateUtilities.getISO8601(getModifiedOn());
	}
}
