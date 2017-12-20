package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.audit.AuditChanges;


@Entity
@NamedQueries({
})
@DiscriminatorValue(WorkChangeLog.WORK_NOTE_CREATED)
@AuditChanges
public class WorkNoteCreatedChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = -1584163793936383545L;
	@NotNull private WorkNote note;

	public WorkNoteCreatedChangeLog() {
	}

	public WorkNoteCreatedChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, WorkNote note) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
		this.note = note;
	}

	@Transient
	public static String getDescription() {
		return "note created";
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_note_id", nullable = false, unique = false)
	public WorkNote getNote() {
		return note;
	}

	public void setNote(WorkNote note) {
		this.note = note;
	}
}
