package com.workmarket.domains.work.model.state;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "workSubStatusTypeAssociation")
@Table(name = "work_sub_status_type_association")
@AuditChanges
public class WorkSubStatusTypeAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Work work;

	@NotNull
	private WorkSubStatusType workSubStatusType;

	private WorkNote transitionNote;
	private boolean resolved = false;
	private User resolvedBy;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_id", updatable = false)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = {}, optional = false)
	@JoinColumn(name = "work_sub_status_type_id")
	public WorkSubStatusType getWorkSubStatusType() {
		return workSubStatusType;
	}

	public void setWorkSubStatusType(WorkSubStatusType workSubStatusType) {
		this.workSubStatusType = workSubStatusType;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne
	@JoinColumn(name = "transition_note_id", referencedColumnName = "id", nullable = true)
	public WorkNote getTransitionNote() {
		return transitionNote;
	}

	public void setTransitionNote(WorkNote transitionNote) {
		this.transitionNote = transitionNote;
	}

	@Column(name="resolved", nullable = false)
	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
	public User getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(User resolvedBy) {
		this.resolvedBy = resolvedBy;
	}
}
