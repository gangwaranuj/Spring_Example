package com.workmarket.domains.work.model.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "workSubStatusAudit")
@Table(name = "work_sub_status_audit")
@AuditChanges
public class WorkSubStatusAudit extends AuditedEntity {

	private static final long serialVersionUID = -8836344447913273922L;

	public Long associationId;
	public WorkSubStatusAuditType actionType;
	public Long noteId;

	@Column(name = "work_sub_status_type_association_id", nullable = false)
	public Long getAssociationId() {
		return associationId;
	}
	public void setAssociationId(Long associationId) {
		this.associationId = associationId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "work_sub_status_audit_action_type", nullable = false)
	public WorkSubStatusAuditType getActionType() {
		return actionType;
	}
	public void setActionType(WorkSubStatusAuditType actionType) {
		this.actionType = actionType;
	}

	@Column(name = "transition_note_id", nullable = true)
	public Long getNoteId() {
		return noteId;
	}
	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}
}
