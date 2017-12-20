package com.workmarket.domains.model.note;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AuditChanges
@Entity(name="noteMetadata")
@Table(name="note_metadata")
public class NoteMetadata extends AuditedEntity {

	private Long noteId;
	private Note parent;
	private Long onBehalfOfUserId;
	private int level = 0;
	private boolean question;

	@Column(name = "note_id", nullable = false, length = 11)
	public Long getNoteId() {
		return noteId;
	}

	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	public Note getParent() {
		return parent;
	}

	public void setParent(Note parent) {
		this.parent = parent;
	}

	@Column(name = "on_behalf_of_user_id", length = 11)
	public Long getOnBehalfOfUserId() {
		return onBehalfOfUserId;
	}

	public void setOnBehalfOfUserId(Long onBehalfOfUserId) {
		this.onBehalfOfUserId = onBehalfOfUserId;
	}

	@Column(name = "level", nullable = false)
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Column(name = "is_question", nullable = false)
	public boolean isQuestion() {
		return question;
	}

	public void setQuestion(boolean question) {
		this.question = question;
	}
}
