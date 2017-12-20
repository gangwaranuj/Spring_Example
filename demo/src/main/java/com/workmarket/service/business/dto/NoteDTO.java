package com.workmarket.service.business.dto;

public class NoteDTO {

	private Long noteId;
	private String content;
	private Boolean isPrivate = Boolean.FALSE;
	private Boolean privileged = Boolean.FALSE;
	private Long parentId;
	private Long onBehalfOfUserId;
	private boolean question;

	public NoteDTO() {}

	public NoteDTO(String content) {
		this.content = content;
	}

	public Long getNoteId() {
		return noteId;
	}

	public NoteDTO setNoteId(Long noteId) {
		this.noteId = noteId;
		return this;
	}

	public String getContent() {
		return content;
	}

	public NoteDTO setContent(String content) {
		this.content = content;
		return this;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public NoteDTO setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
		return this;
	}

	public NoteDTO setPrivileged(Boolean privileged){
		this.privileged = privileged;
		return this;
	}

	public Boolean getPrivileged(){
		return this.privileged;
	}

	public Long getParentId() {
		return parentId;
	}

	public NoteDTO setParentId(Long parentId) {
		this.parentId = parentId;
		return this;
	}

	public boolean isQuestion() {
		return question;
	}

	public NoteDTO setQuestion(boolean question) {
		this.question = question;
		return this;
	}

	public boolean hasParent() {
		return parentId != null;
	}

	public boolean isTopNote() {
		return !this.hasParent();
	}

	public Long getOnBehalfOfUserId() {
		return onBehalfOfUserId;
	}

	public NoteDTO setOnBehalfOfUserId(Long onBehalfOfUserId) {
		this.onBehalfOfUserId = onBehalfOfUserId;
		return this;
	}
}
