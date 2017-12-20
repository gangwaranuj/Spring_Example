package com.workmarket.domains.model.note.concern;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="concern")
@DiscriminatorValue("baseConcern")
@AuditChanges
public abstract class Concern extends Note {

	private static final long serialVersionUID = 1L;

	private boolean resolved = false;
	private User resolvedBy;

	public Concern() {
		super();
	}

	public Concern(String content) {
		super(content);
	}

	@Transient
	public abstract String getType();

	@Transient
	public abstract Long getEntityId();

	@Transient
	public abstract String getEntityNumber();

	@Column(name="resolved")
	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "resolved_by", nullable = false)
	public User getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(User resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

}
