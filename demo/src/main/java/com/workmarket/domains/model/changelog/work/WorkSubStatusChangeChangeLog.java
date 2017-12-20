package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_SUB_STATUS_CHANGE)
@AuditChanges
public class WorkSubStatusChangeChangeLog extends WorkChangeLog {
	private static final long serialVersionUID = -3491711502363559339L;

	private WorkSubStatusType oldValue;
	private WorkSubStatusType newValue;
	private WorkNote note;

	public WorkSubStatusChangeChangeLog() {}
	public WorkSubStatusChangeChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, WorkSubStatusType oldValue, WorkSubStatusType newValue, WorkNote note) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
		this.setOldValue(oldValue);
		this.setNewValue(newValue);
		this.setNote(note);
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="old_work_sub_status_type_id", referencedColumnName="id", nullable=true)
	public WorkSubStatusType getOldValue() {
		return oldValue;
	}
	public void setOldValue(WorkSubStatusType oldValue) {
		this.oldValue = oldValue;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="new_work_sub_status_type_id", referencedColumnName="id", nullable=true)
	public WorkSubStatusType getNewValue() {
		return newValue;
	}
	public void setNewValue(WorkSubStatusType newValue) {
		this.newValue = newValue;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transition_note_id", nullable = true)
	public WorkNote getNote() {
		return note;
	}
	public void setNote(WorkNote note) {
		this.note = note;
	}
}
