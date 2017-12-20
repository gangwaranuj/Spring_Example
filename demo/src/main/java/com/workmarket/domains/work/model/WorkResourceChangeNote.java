package com.workmarket.domains.work.model;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "workResourceChangeNote")
@Table(name = "work_resource_change_log_to_note_xref")
@AuditChanges
public class WorkResourceChangeNote extends AuditedEntity {

	private static final long serialVersionUID = -2942328953155774924L;
	private WorkResourceChangeLog workResourceChangeLog;
	private Note note;

	public WorkResourceChangeNote() {
	}

	public WorkResourceChangeNote(Note note, WorkResourceChangeLog workResourceChangeLog) {
		this.note = note;
		this.workResourceChangeLog = workResourceChangeLog;
	}

	@ManyToOne
	@JoinColumn(name = "note_id", referencedColumnName = "id", nullable = false)
	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	@ManyToOne
	@JoinColumn(name = "work_resource_change_log_id", referencedColumnName = "id", nullable = false)
	public WorkResourceChangeLog getWorkResourceChangeLog() {
		return workResourceChangeLog;
	}

	public void setWorkResourceChangeLog(WorkResourceChangeLog workResourceChangeLog) {
		this.workResourceChangeLog = workResourceChangeLog;
	}
}
