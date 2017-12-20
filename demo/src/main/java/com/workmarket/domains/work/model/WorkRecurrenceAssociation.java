package com.workmarket.domains.work.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name = "workRecurrenceAssociation")
@Table(name = "work_to_recurrence_association")
@AuditChanges
@Access(AccessType.PROPERTY)
public class WorkRecurrenceAssociation implements Serializable {

	private static final long serialVersionUID = 2449030598895403764L;

	private Boolean deleted = Boolean.FALSE;
	private Calendar createdOn;
	private Calendar modifiedOn;
	private Long modifierId;
	private Long creatorId;
	private WorkRecurrencePK workRecurrence;
	private AbstractWork work;
	private Long recurringWorkId;

	public WorkRecurrenceAssociation() {
	}

	public WorkRecurrenceAssociation(
			Long workId,
			Long recurringWorkId,
			String recurrenceUUID) {
		this.workRecurrence = new WorkRecurrencePK(workId, recurrenceUUID);
		this.recurringWorkId = recurringWorkId;
	}

	@Column(name = "deleted", nullable = false)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@EmbeddedId
	public WorkRecurrencePK getWorkRecurrence() {
		return workRecurrence;
	}

	public void setWorkRecurrence(WorkRecurrencePK workRecurrence) {
		this.workRecurrence = workRecurrence;
	}

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
	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Column(name = "creator_id")
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@OneToOne
	@JoinColumn(name = "work_id")
	@MapsId("workRecurrence")
	public AbstractWork getWork() {
		return this.work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@Column(name = "recurring_work_id")
	public Long getRecurringWorkId() {
		return recurringWorkId;
	}

	public void setRecurringWorkId(Long recurringWorkId) {
		this.recurringWorkId = recurringWorkId;
	}
}
